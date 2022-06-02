package com.project.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

// class that implements a Model to render
public class Model implements IModel {
	private int drawCount; // counter of draw calls necessary to render the model
	private int VBOid; // id of the Vertex Buffer Array that stores the vertices of the model
	protected int TEXVBOid; // id of the Vertex Buffer Array that stores the UVs of the texture
	private int INDid; // id of the Element Array Buffer that stores the indices of the vertices in order to be rendered
	
	private int bbINDid; // id of the Element Array Buffer that stores the indices of the vertices in order to render lines for debug
	private int bbPINDid; // id of the Element Array Buffer that stores the indices of the vertices in order to render points for debug
	
	private float x; // the model's position coordinates
	private float y;
	private float rotationValue; // rotation amount by the Z axis
	private float scaleValue; // scale amount (uniform in every axis)
	private float scaleMul; // scale multiplier (always applied regardless of the scaleValue)
	
	private Matrix4f scale; // scale matrix
	private Matrix4f rotation; // rotation matrix
	private Matrix4f translation; // translation matrix
	private Matrix4f target; // Model matrix (S * R * T)
	
	protected ITexture tex; // texture object
	protected IShader shader; // shader object
	protected IShader bbShader; // bounding box shader object
	
	private float borderX; // size of the model
	private float borderY;
	
	private int animationSteps; // amount of frames in an animation
	private int animationPosition; // current frame in the animation
	private int counter; // frame counter to sync animations
	private float animationSpeed; // frames per second of the animation
	private int animation; // current animation index
	private int animationsCount; // counter of animations available
	private int maxAnimationSteps; // max number of frames for the longest animation in the sheet
	private List<Integer> animationFrames; // list of amount of frames for each animation
	
	private float opacity; // opacity of the model
	
	// Constructor
	public Model() {
		// vertices to make a square to fit the texture
		float[] vertices = new float[] {
			-0.5f,  0.5f, // TOP LEFT
			 0.5f,  0.5f, // TOP RIGHT
			-0.5f, -0.5f, // BOTTOM LEFT
			 0.5f, -0.5f  // BOTTOM RIGHT
		};
		// UV values to apply the texture to the square
		float[] textureUV = new float[] {
			0, 0,
			1, 0,
			0, 1,
			1, 1
		};
		// indices to form triangles to render the square
		int[] indices = new int[] {
			0, 1, 2,
			2, 1, 3
		};
		
		// indices to form lines to render the bounding box outline
		int[] bbIndices = new int[] {
				0, 1,
				1, 3,
				3, 2,
				2, 0
		};
		
		// indices of the vertices that correspond to the edges of the model
		int[] bbPointIndices = new int[] {
				0, 1, 2, 3
		};
		
		// field to keep track of how many vertices need to be rendered
		this.drawCount = indices.length;
		
		// create a Vertex Buffer Object to store the values of the vertices to render the square in GPU
		this.VBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, this.createBuffer(vertices), GL_DYNAMIC_DRAW);
		
		// create a Vertex Buffer Object to store the values of the UV coordinates to apply the texture to the square
		this.TEXVBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glBufferData(GL_ARRAY_BUFFER, this.createBuffer(textureUV), GL_STATIC_DRAW);
		
		// create an Element Array Buffer to store the indices of the vertices in order to draw 2 triangles (a square)
		this.INDid = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.INDid);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		// create an Element Array Buffer to store the indices of the vertices in order to draw the outline of the model
		this.bbINDid = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbINDid);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, bbIndices, GL_STATIC_DRAW);
		
		// create an Element Array Buffer to store the indices of the vertices in order to draw 4 points at the edge of the model
		this.bbPINDid = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbPINDid);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, bbPointIndices, GL_STATIC_DRAW);
		
		
		// unbind the buffers so that there is no current buffer selected
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// create a new texture
		this.tex = new Texture();
		// create a new shader for rendering the object
		this.shader = new Shader("entity/shader");
		// create a new shader for rendering the bounding box outline
		this.bbShader = new Shader("bb/shader");
		
		// set the default scale multiplier to 256 (otherwise the objects would be squares 1px wide and 1px tall
		this.scaleMul = 256;
		
		// create a new scale matrix
		this.scale = new Matrix4f().scale(this.scaleMul); // | Sx |  0 |  0 | 0 |   Sx = Scale component for the X axis
		   												  // |  0 | Sy |  0 | 0 |   Sy = Scale component for the Y axis
		   												  // |  0 |  0 | Sz | 0 |   Sz = Scale component for the Z axis
		   												  // |  0 |  0 |  0 | 1 |
		
		// create a new rotation matrix
		this.rotation = new Matrix4f();	   				  // | cos(θ) | -sin(θ) | 0 | 0 |   θ = angle of rotation by the Z axis
		   								   				  // | sin(θ) | -cos(θ) | 0 | 0 |
		   								   				  // |    0   |    0    | 1 | 0 |
		   								   				  // |    0   |    0    | 0 | 1 |
		
		// create a new translation matrix
		this.translation = new Matrix4f(); 				  // | 1 | 0 | 0 | Tx |   Tx = translation component for the X axis
										   				  // | 0 | 1 | 0 | Ty |   Ty = translation component for the Y axis
										   				  // | 0 | 0 | 1 | Tz |   Tz = translation component for the Z axis
										   				  // | 0 | 0 | 0 |  1 |
		
		// create a new target matrix (this will be the model matrix, which includes scale, rotation and translation)
		this.target = new Matrix4f(); // S * R * T = target / model matrix
		
		// dimensions of the model
		this.borderX = 0;
		this.borderY = 0;
		
		// number of frames in the current animation
		this.animationSteps = 0;
		// counter of the position of the frame in the current animation
		this.animationPosition = 0;
		// counter to go through animations every x seconds
		this.counter = 0;
		// number of times the animation updates per second
		this.animationSpeed = 1.0f;
		// current animation
		this.animation = 0;
		// amount of animations in the sprite sheet
		this.animationsCount = 1;
		// max amount of frames in any animation
		this.maxAnimationSteps = 0;
		// list of frames per animation
		this.animationFrames = new LinkedList<Integer>();
		
		// opacity of the model
		this.opacity = 1;
	}
	
	public void render(ICamera camera, boolean debug) {
		// calculate the model matrix (S * R * T) and store it in the target matrix
		this.calculateModelMatrix();
		
		// bind the object's texture to the slot 0
		this.tex.bind(0);
		
		// enable 2 VBOs (one for the vertices, one for the texture UVs)
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		// bind the shader to render the object
		this.shader.bind();
		// notify the shader that the texture (sampler) is located in the slot 0
		this.shader.setUniform("sampler", 0);
		// pass the model matrix multiplied by the camera projection matrix to obtain the correct perspective
		this.shader.setUniform("projection", camera.getProjection().mul(this.target));
		// pass the opacity to the shader
		this.shader.setUniform("opacity", this.opacity);
	
		// bind the VBO containing the model vertices
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		// bind the VBO containing the texture UVs
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);	
		// bind the indices buffer containing the indices of the vertices and UVs used to render the square
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.INDid);
	
		// draw 2 triangles that represent the square of the model
		glDrawElements(GL_TRIANGLES, this.drawCount, GL_UNSIGNED_INT, 0);
		
		// if debug mode is active
		if (debug) {
			// bind the bounding box shader
			this.bbShader.bind();
			// pass it the model matrix multiplied by the camera projection matrix
			this.bbShader.setUniform("projection", camera.getProjection().mul(this.target));
			
			// bind the indices buffer containing the location of the vertices where to draw points (the edges of the bounding box)
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbPINDid);
			
			// set the point size to be 10px
			glPointSize(10.0f);
			// draw 4 points at the edges of the model
			glDrawElements(GL_POINTS, 4, GL_UNSIGNED_INT, 0);
			
			// bind the indices buffer containing the vertices where to draw lines (edges of the bounding box)
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbINDid);
			// draw 4 lines at the edges of the model
			glDrawElements(GL_LINES, 8, GL_UNSIGNED_INT, 0);
		}
		
		// unbind any previously bound buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// disable the vertex arrays in the GPU
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}
	
	// method for creating and flipping a FloatBuffer (used to load vertices and UVs into the OpenGL buffers)
	protected FloatBuffer createBuffer(float[] data) {
		FloatBuffer buffer;
		buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return(buffer);
	}
	
	// method for setting the position / translation of the model
	public void setPosition(float x, float y) {
		// store the position values
		this.x = x;
		this.y = y;
		
		// calculate a new translation matrix with the new position
		this.translation = new Matrix4f().translate(this.x, this.y, 0);
	}
	
	// method for setting the rotation of the model by the Z axis
	public void setRotation(float rotation) {
		// store the angle of rotation
		this.rotationValue = rotation;
		
		// calculate a new rotation matrix with the new rotation angle
		this.rotation = new Matrix4f().rotate(this.rotationValue, 0, 0, 1);
	}
	
	// method for setting the scale of the model (uniform around the 3 axis)
	public void setScale(float scale) {
		// store the scale value
		this.scaleValue = scale;
		
		// calculate a new scale matrix with the new scale value
		this.scale = new Matrix4f().scale(this.scaleValue * this.scaleMul);
	}
	
	// method for getting the combination of the 3 matrices
	private void calculateModelMatrix() {
		// start from a blank identity matrix
		this.target = new Matrix4f();
		
		// multiply the matrices to the target matrix (ORDER IS IMPORTANT)
		this.target.mul(this.translation, this.target);
		this.target.mul(this.rotation, this.target);
		this.target.mul(this.scale, this.target);
	}
	
	// getters for the values that change the model position, rotation and scale
	public float getX() {
		return(this.x);
	}
	public float getY() {
		return(this.y);
	}
	public float getRotation() {
		return(this.rotationValue);
	}
	public float getScale() {
		return(this.scaleValue);
	}
	
	// load a texture and adapt the ratio of the square to match the ratio of the image
	public void loadTextureAndAdapt(String filename) {
		// load the image
		this.tex.loadImage(filename);
		// adapt the ratio
		this.adaptToTexture();
	}
	
	// load an animation and adapt the ratio of the square to match the ratio of the image
	public void loadAnimationAndAdapt(String filename, int steps, int animations) {
		// load the image
		this.tex.loadImage(filename);
		// adapt the ratio and single frame size
		this.adaptToSheet(steps, animations);
	}
	
	// method to adapt the ratio of the model to match the ratio of the texture
	private void adaptToTexture() {
		if (this.tex.getWidth() > this.tex.getHeight()) { // if the width is larger than the height
			// keep the width
			this.borderX = 0.5f;
			// adapt the height
			this.borderY = this.borderX * ((float)this.tex.getHeight() / (float)this.tex.getWidth());
		} else if (this.tex.getWidth() < this.tex.getHeight()) { // if the height is larger than the width
			// keep the height
			this.borderY = 0.5f;
			// adapt the width
			this.borderX = this.borderY * ((float)this.tex.getWidth() / (float)this.tex.getHeight());
		} else { // otherwise if it's a square image, keep both the same
			this.borderX = 0.5f;
			this.borderY = 0.5f;
		}
		
		// update the vertex buffer with the new adapted values
		float [] vertices = new float[] {
				-this.borderX,  this.borderY, // TOP LEFT
				 this.borderX,  this.borderY, // TOP RIGHT
				-this.borderX, -this.borderY, // BOTTOM LEFT
				 this.borderX, -this.borderY  // BOTTOM RIGHT
		};
		
		// replace the old vertices for the new ones
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
	}
	
	// method to adapt the ratio of a model to match the ratio of a sprite and find the amount of frames per animation within a spritesheet
	private void adaptToSheet(int steps, int animations) {
		// animation steps (amount of frames for the current animation)
		this.animationSteps = steps;
		// max animation steps (amount of frames for the longest animation in the sheet, amount of columns)
		this.maxAnimationSteps = steps;
		// amount of animations (rows in the sheet)
		this.animationsCount = animations;
		
		// store the pixels of the sheet texture
		int[] pixels = this.tex.getPixels();
		
		// flag to keep track of whether or not a new frame for an animation was found
		boolean found = false;
		
		// the program scrolls through every square (frame) of every animation (row), for every frame, it scrolls through every
		// pixel (row and column) until it finds a pixel that has an alpha value that isn't 0 (meaning that there is something there)
		// if the algorithm finds something (a new frame for the current animation), it updates the amount of frames for that animation
		// this is required since not all animations within the same sheet don't have the same amount of frames
		
		for (int h = 0; h < this.animationsCount; h++) { // moving through animations
			this.animationFrames.add(1);
			
			for (int w = 0; w < this.maxAnimationSteps; w++) { // moving through steps of animation
				for (int i = h * (this.tex.getHeight() / this.animationsCount); (i < (h + 1) * (this.tex.getHeight() / this.animationsCount)) && !found; i++) { // moving through pixel rows
					for (int j = w * (this.tex.getWidth() / this.maxAnimationSteps); (j < (w + 1) * (this.tex.getWidth() / this.maxAnimationSteps)) && !found; j++) { // moving through pixel columns
						// pixels are stored linearly in an array, to access them through row and column numbers, we need to calculate
						// the index through the formula: "i * width + j"
						int pixel = pixels[i * this.tex.getWidth() + j];
						
						if (((pixel >> 24) & 0xFF) != 0) { // if the alpha value is different than 0
							found = true;				   // we found a new frame for the animation
						}
					} // column
				} // row
				
				// if we found a new frame
				if (found) {
					// reset the flag
					found = false;
					// update the frame count for the current animation
					this.animationFrames.set(h, w + 1);
				}
			} // animation step
		} // animation

		// calculate the width of a single frame (uniform between frames)
		float spriteWidth = (float)this.tex.getWidth() / this.maxAnimationSteps;
		
		
		if (spriteWidth > (this.tex.getHeight() / (float)this.animationsCount)) { // if the sprite is wider than tall
			// keep the width
			this.borderX = 0.5f;
			// adjust the height
			this.borderY = this.borderX * (((float)this.tex.getHeight() / this.animationsCount) / spriteWidth);
		} else if (spriteWidth < (this.tex.getHeight() / (float)this.animationsCount)) { // if the sprite is taller than wide
			// keep the height
			this.borderY = 0.5f;
			// adjust the width
			this.borderX = this.borderY * (spriteWidth / ((float)this.tex.getHeight() / this.animationsCount));
		} else { // if the frames are squares, keep the ratio
			this.borderX = 0.5f;
			this.borderY = 0.5f;
		}
		
		// update the vertices with the new adjusted values
		float [] vertices = new float[] {
			-this.borderX,  this.borderY, // TOP LEFT
			 this.borderX,  this.borderY, // TOP RIGHT
			-this.borderX, -this.borderY, // BOTTOM LEFT
			 this.borderX, -this.borderY  // BOTTOM RIGHT
		};
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
		
		// update the UVs to only show one frame of the whole sprite sheet
		float [] textureUV = new float[] {
			0,                        0,
			1f / this.animationSteps, 0,
			0,                        1,
			1f / this.animationSteps, 1
		};
		// replace the UV buffer with the new values
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
	}
	
	// method for scaling a model vertices horizontally
	public void scaleHorizontal(float percentage) {
		float newBorderX = this.borderX * percentage;
		
		float [] vertices = new float[] {
				-newBorderX,  this.borderY, // TOP LEFT
				 newBorderX,  this.borderY, // TOP RIGHT
				-newBorderX, -this.borderY, // BOTTOM LEFT
				 newBorderX, -this.borderY  // BOTTOM RIGHT
		};
		
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
	}
	
	// method for updating the animation frame
	public void updateAnimation(boolean direction) {
		if (this.animationSteps != 0) {
			// at every call, increase a counter variable. this is used to sync the animations with timers
			this.counter++;
			
			// array to store the new UV coordinates to show the new frame of the animation
			float[] textureUV;

			// change frame animationSpeed times per second
			if ((float)this.counter % Math.round(60 / this.animationSpeed) == 0) {
				if (direction == true) { // if the model is facing right												
					textureUV = new float[] {										   
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation + 1),
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation + 1)
					};

				} else { // if the model is facing left
					textureUV = new float[] {
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation + 1),
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation + 1)
					};
				}
				
				// update the counter for the current frame in the animation
				this.animationPosition++;
				
				// loop the animation when it ends
				if (this.animationPosition >= this.animationFrames.get(this.animation)) {
					this.animationPosition = 0;
				}
			
				// replace the texture UV buffer with the new one
				glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
				glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
			}
		}
	}
	
	// method for setting the current animation
	public void setCurrentAnimation(int animation) {
		if (animation >= 0 && animation <= this.animationsCount) {
			if (this.animation != animation) {
				this.animation = animation;
				this.animationPosition = 0;
				this.counter = Math.round(60 / this.animationSpeed) - 1;
			}
		}
	}
	
	// method for setting the animation speed
	public void setAnimationSpeed(float animationSpeed) {
		this.animationSpeed = animationSpeed;
	}
	
	// method for calculating the boundingBox of the actual rendered model
	public List<Float> calculateBoundingBox() {
		this.calculateModelMatrix(); // calculate the model matrix
		
		Vector4f position1 = new Vector4f( this.borderX,  this.borderY, 1, 1); // create 2 vertices that contain the size of the borders
		Vector4f position3 = new Vector4f(-this.borderX, -this.borderY, 1, 1);
		
		position1.mul(this.target, position1); // multiply the vertices by the model matrix to obtain the same border coordinates but
		position3.mul(this.target, position3); // in world coordinates instead
		
		List<Float> boundingBox = new LinkedList<Float>();
		boundingBox.add(Math.abs(position1.x - position3.x)); // calculate and store the width and height of the bounding box
		boundingBox.add(Math.abs(position1.y - position3.y)); // in world coordinates
		
		return(boundingBox);		
	}
	
	// getters and setters
	public List<Integer> getFrames() {
		return(this.animationFrames);
	}
	public float getScaleMul() {
		return(this.scaleValue * this.scaleMul);
	}
	public void setOpacity(float value) {
		this.opacity = value;
	}
	public float getOpacity() {
		return(this.opacity);
	}
	
}
