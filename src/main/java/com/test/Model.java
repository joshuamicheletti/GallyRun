package com.test;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Model {
	private int drawCount;
	private int VBOid;
	private int TEXVBOid;
	private int INDid;
	
	private int bbINDid;
	private int bbPINDid;
	
	private float x;
	private float y;
	
	private float prevX;
	private float prevY;
	
	private float rotationValue;
	private float scaleValue;
	
	private int animationSteps;
	
	private int animationPosition;
	
	public Texture tex;
	public Shader shader;
	public Shader bbShader;
	
	private Matrix4f scale;
	private Matrix4f rotation;
	private Matrix4f translation;
	private Matrix4f target;
	
	private float scaleMul;
	
	private int counter;
	
	private float animationSpeed;
	
	private float borderX;
	private float borderY;
	
	private int animation;
	
	private int animationsCount;
	
	private int maxAnimationSteps;
	
	private List<Integer> animationFrames;
	
	private float bbScaleX;
	private float bbScaleY;
	
	private float bbBorderX0;
	private float bbBorderY0;
	private float bbBorderX2;
	private float bbBorderY2;
	
	public Model() {
		
		float[] vertices = new float[] {
			-0.5f,  0.5f, // TOP LEFT
			 0.5f,  0.5f, // TOP RIGHT
			-0.5f, -0.5f, // BOTTOM LEFT
			 0.5f, -0.5f  // BOTTOM RIGHT
		};
			
			
		float[] textureUV = new float[] {
			0, 0,
			1, 0,
			0, 1,
			1, 1
		};
		
		
		int[] indices = new int[] {
			0, 1, 2,
			2, 1, 3
		};
		
		int[] bbIndices = new int[] {
				0, 1,
				1, 3,
				3, 2,
				2, 0
		};
		
		int[] bbPointIndices = new int[] {
				0, 1, 2, 3
		};
		
		
		this.drawCount = indices.length;
		
		this.VBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_DYNAMIC_DRAW);
		
		
		this.TEXVBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
		
		
		IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
		buffer.put(indices);
		buffer.flip();
		
		this.INDid = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.INDid);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		
		buffer = BufferUtils.createIntBuffer(bbIndices.length);
		buffer.put(bbIndices);
		buffer.flip();
		
		this.bbINDid = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbINDid);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, bbIndices, GL_STATIC_DRAW);
		
		buffer = BufferUtils.createIntBuffer(bbPointIndices.length);
		buffer.put(bbPointIndices);
		buffer.flip();
		
		this.bbPINDid = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbPINDid);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, bbPointIndices, GL_STATIC_DRAW);
		
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		this.tex = new Texture();
		this.shader = new Shader("entity/shader");
		this.bbShader = new Shader("bb/shader");
		
		this.scaleMul = 256;
		
		this.scale = new Matrix4f().scale(this.scaleMul);
		this.rotation = new Matrix4f();
		this.translation = new Matrix4f();
		
		this.target = new Matrix4f();
		
		this.animationSteps = 0;
		
		this.animationPosition = 0;
		
		this.counter = 0;
		
		this.animationSpeed = 1.0f;
		
		this.borderX = 0;
		this.borderY = 0;
		
		this.prevX = 0;
		this.prevY = 0;
		
		this.animation = 0;
		
		this.animationsCount = 1;
		
		this.maxAnimationSteps = 0;
		
		this.animationFrames = new ArrayList();
		
		this.bbScaleX = 1;
		this.bbScaleY = 1;
	}
	
	public void render(Camera camera, boolean debug) {
		this.getProjection();
		
		this.tex.bind(0);
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		this.shader.bind();
		this.shader.setUniform("sampler", 0);
		this.shader.setUniform("projection", camera.getProjection().mul(this.target));
	
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.INDid);
	
		glDrawElements(GL_TRIANGLES, this.drawCount, GL_UNSIGNED_INT, 0);
		
		if (debug) {
			this.bbShader.bind();
			this.bbShader.setUniform("projection", camera.getProjection().mul(this.target));
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbPINDid);
			
			glPointSize(10.0f);
			glDrawElements(GL_POINTS, 4, GL_UNSIGNED_INT, 0);
			
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.bbINDid);
			
			glDrawElements(GL_LINES, 8, GL_UNSIGNED_INT, 0);
		}
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}
	
	public void renderSky(Camera camera) {
		this.getProjection();
		
		this.tex.bind(0);
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		this.shader.bind();
		this.shader.setUniform("sampler", 0);
		this.shader.setUniform("projection", this.target);
	
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.INDid);
	
		glDrawElements(GL_TRIANGLES, this.drawCount, GL_UNSIGNED_INT, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}
	
	private FloatBuffer createBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		
		buffer.put(data);
		buffer.flip();
		
		return(buffer);
	}
	
	
	private void printMatrix(Matrix4f mat)  {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				System.out.print(mat.get(j, i) + " ");
			}
			System.out.print("\n");
		}
	}
	
	public void setPosition(float x, float y) {
		this.prevX = this.x;
		this.prevY = this.y;
		
		this.x = x;
		this.y = y;
		
		this.translation = new Matrix4f().translate(this.x, this.y, 0);
	}
	
	public void rollbackPosition(float x, float y) {
		this.x = x;
		this.y = y;
		
		this.translation = new Matrix4f().translate(this.x, this.y, 0);
	}
	
	public void setRotation(float rotation) {
		this.rotationValue = rotation;
		
		this.rotation = new Matrix4f().rotate(this.rotationValue, 0, 0, 1);
	}
	
	public void setScale(float scale) {
		this.scaleValue = scale;
		
		this.scale = new Matrix4f().scale(this.scaleValue * this.scaleMul);
	}
	
	
	public void getProjection() {
		this.target = new Matrix4f();
		
		this.target.mul(this.translation, this.target);
		this.target.mul(this.rotation, this.target);
		this.target.mul(this.scale, this.target);
	}
	
	
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
	
	
	public void loadTextureAndAdapt(String filename) {
		this.tex.loadImage(filename);
		
		this.adaptToTexture();
	}
	
	public void loadAnimationAndAdapt(String filename, int steps, int animations) {
		this.tex.loadImage(filename);
		
		this.adaptToSheet(steps, animations);
	}
	
	
	public void adaptToTexture() {
		
		if (this.tex.getWidth() > this.tex.getHeight()) {
			this.borderX = 0.5f;
			this.borderY = this.borderX * ((float)this.tex.getHeight() / (float)this.tex.getWidth());
		} else if (this.tex.getWidth() < this.tex.getHeight()) {
			this.borderY = 0.5f;
			this.borderX = this.borderY * ((float)this.tex.getWidth() / (float)this.tex.getHeight());
		} else {
			this.borderX = 0.5f;
			this.borderY = 0.5f;
		}
		
		float [] vertices = new float[] {
				-this.borderX,  this.borderY, // TOP LEFT
				 this.borderX,  this.borderY, // TOP RIGHT
				-this.borderX, -this.borderY, // BOTTOM LEFT
				 this.borderX, -this.borderY  // BOTTOM RIGHT
		};
		
		this.VBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
	}
	
	public void adaptToSheet(int steps, int animations) {
		this.animationSteps = steps;
		this.maxAnimationSteps = steps;
		this.animationsCount = animations;
		
		int[] pixels = this.tex.getPixels();
		
//		i * width + j
		
		boolean found = false;
		
		for (int h = 0; h < this.animationsCount; h++) { // moving through animations
			this.animationFrames.add(1);
			
			for (int w = 0; w < this.maxAnimationSteps; w++) { // moving through steps of animation
				for (int i = h * (this.tex.getHeight() / this.animationsCount); (i < (h + 1) * (this.tex.getHeight() / this.animationsCount)) && !found; i++) { // moving through pixel rows
					for (int j = w * (this.tex.getWidth() / this.maxAnimationSteps); (j < (w + 1) * (this.tex.getWidth() / this.maxAnimationSteps)) && !found; j++) { // moving through pixel columns
						int pixel = pixels[i * this.tex.getWidth() + j];
						
						if (((pixel >> 24) & 0xFF) != 0) {
							found = true;
						}

//						System.out.println("(" + ((pixel >> 16) & 0xFF) + ", " + ((pixel >> 8) & 0xFF) + ", " + ((pixel >> 0) & 0xFF) + ", " + ((pixel >> 24) & 0xFF) + ")");
					} // column
				} // row
				
				if (found) {
					found = false;
					this.animationFrames.set(h, w + 1);
				}
			} // animation step
		} // animation
		
		for (int i = 0; i < this.animationFrames.size(); i++) {
			System.out.println(this.animationFrames.get(i));
		}
		
		
		float spriteWidth = (float)this.tex.getWidth() / this.maxAnimationSteps;
		
		if (spriteWidth > (this.tex.getHeight() / (float)this.animationsCount)) {
			this.borderX = 0.5f;
			this.borderY = this.borderX * (((float)this.tex.getHeight() / this.animationsCount) / spriteWidth);
		} else if (spriteWidth < this.tex.getHeight() / (float)this.animationsCount) {
			this.borderY = 0.5f;
			this.borderX = y * (spriteWidth / ((float)this.tex.getHeight() / this.animationsCount));
		} else {
			this.borderX = 0.5f;
			this.borderY = 0.5f;
		}
		
		float [] vertices = new float[] {
			-this.borderX,  this.borderY, // TOP LEFT
			 this.borderX,  this.borderY, // TOP RIGHT
			-this.borderX, -this.borderY, // BOTTOM LEFT
			 this.borderX, -this.borderY  // BOTTOM RIGHT
		};
		
		this.VBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
		
		float [] textureUV = new float[] {
			0,                        0,
			1f / this.animationSteps, 0,
			0,                        1,
			1f / this.animationSteps, 1
		};
		
		this.TEXVBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
	}
	
	
	public void updateAnimation(boolean direction) {
		if (this.animationSteps != 0) {
			this.counter++;
			
			float [] textureUV;

			if ((float)this.counter % Math.round(60 / this.animationSpeed) == 0) {		
				if (direction == true) {												
					textureUV = new float[] {										   
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation + 1),
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation + 1)
					};

				} else {
					textureUV = new float[] {
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation),
							(1f / this.maxAnimationSteps) * (this.animationPosition + 1), (1f / this.animationsCount) * (this.animation + 1),
							(1f / this.maxAnimationSteps) * this.animationPosition,       (1f / this.animationsCount) * (this.animation + 1)
					};
				}
							
				this.animationPosition++;
				
				if (this.animationPosition >= this.animationFrames.get(this.animation)) {
					this.animationPosition = 0;
				}
				
//				this.TEXVBOid = glGenBuffers();
				glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
				glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
			}
		}
	}
	
	public void setIdle(boolean idle) {
		
		if (idle) {
			if (this.animation != 5) {
				this.animation = 5;
				this.animationPosition = 0;
			}
		} else {
			if (this.animation != 0) {
				this.animation = 0;
				this.animationPosition = 0;
			}
		}
	}
	
	public void setRunning() {
		if (this.animation != 1) {
			this.animation = 1;
			this.animationPosition = 0;
		}
	}
	
	public void setJumping() {
		if (this.animation != 2) {
			this.animation = 2;
			this.animationPosition = 0;
		}
	}
	
	public void setMidAir() {
		if (this.animation != 3) {
			this.animation = 3;
			this.animationPosition = 0;
		}
	}
	
	public void setFalling() {
		if (this.animation != 4) {
			this.animation = 4;
			this.animationPosition = 0;
		}
	}
	
	public void setGoingToSleep() {
		if (this.animation != 6) {
			this.animation = 6;
			this.animationPosition = 0;
		}
	}
	
	public void setSleeping() {
		if (this.animation != 7) {
			this.animation = 7;
			this.animationPosition = 0;
		}
	}
	
	
	public void setAnimations(int number) {
		this.animationsCount = number;
	}
	
	public void setAnimationSpeed(float animationSpeed) {
		this.animationSpeed = animationSpeed;
	}
	
	
	public float getPrevX() {
		return(this.prevX);
	}
	
	public float getPrevY() {
		return(this.prevY);
	}
	
	public List<Vector4f> calculateBoundingBox(boolean hitbox) {
		if (hitbox) {
			List<Vector4f> boundingPoints = new ArrayList();
			
			Vector4f position1 = new Vector4f(this.bbBorderX0, this.bbBorderY0, 1, 1);
			Vector4f position2 = new Vector4f(this.bbBorderX0, this.bbBorderY2, 1, 1);
			Vector4f position3 = new Vector4f(this.bbBorderX2, this.bbBorderY2, 1, 1);
			Vector4f position4 = new Vector4f(this.bbBorderX2, this.bbBorderY0, 1, 1);
			
			boundingPoints.add(position1);
			boundingPoints.add(position2);
			boundingPoints.add(position3);
			boundingPoints.add(position4);
			
			return(boundingPoints);
			
		} else {
			this.getProjection();
			
			Vector4f position1 = new Vector4f( this.borderX * this.bbScaleX,  this.borderY * this.bbScaleY, 1, 1);
			Vector4f position2 = new Vector4f( this.borderX * this.bbScaleX, -this.borderY, 1, 1);
			Vector4f position3 = new Vector4f(-this.borderX * this.bbScaleX, -this.borderY, 1, 1);
			Vector4f position4 = new Vector4f(-this.borderX * this.bbScaleX,  this.borderY * this.bbScaleY, 1, 1);
			
			position1.mul(this.target, position1);
			position2.mul(this.target, position2);
			position3.mul(this.target, position3);
			position4.mul(this.target, position4);
			
			List<Vector4f> boundingPoints = new ArrayList();
			
			boundingPoints.add(position1);
			boundingPoints.add(position2);
			boundingPoints.add(position3);
			boundingPoints.add(position4);
			
//			System.out.println("Bounding Box: (" + position.x + ", " + position.y + ")");
			
			return(boundingPoints);
		}
		
		
	}
	
	public List<Vector4f> calculatePrevBoundingBox(boolean hitbox) {
		if (hitbox) {
			List<Vector4f> boundingPoints = new ArrayList();
			
			Vector4f position1 = new Vector4f(this.bbBorderX0, this.bbBorderY0, 1, 1);
			Vector4f position2 = new Vector4f(this.bbBorderX0, this.bbBorderY2, 1, 1);
			Vector4f position3 = new Vector4f(this.bbBorderX2, this.bbBorderY2, 1, 1);
			Vector4f position4 = new Vector4f(this.bbBorderX2, this.bbBorderY0, 1, 1);
			
			boundingPoints.add(position1);
			boundingPoints.add(position2);
			boundingPoints.add(position3);
			boundingPoints.add(position4);
			
			return(boundingPoints);
			
		} else {
		
			Vector4f position1 = new Vector4f( this.borderX * this.bbScaleX,  this.borderY * this.bbScaleY, 1, 1);
			Vector4f position2 = new Vector4f( this.borderX * this.bbScaleX, -this.borderY, 1, 1);
			Vector4f position3 = new Vector4f(-this.borderX * this.bbScaleX, -this.borderY, 1, 1);
			Vector4f position4 = new Vector4f(-this.borderX * this.bbScaleX,  this.borderY * this.bbScaleY, 1, 1);
			
			Matrix4f prevTarget = new Matrix4f();
			
			prevTarget.mul(new Matrix4f().translate(this.prevX, this.prevY, 0), prevTarget);
			prevTarget.mul(this.rotation, prevTarget);
			prevTarget.mul(this.scale, prevTarget);
			
			position1.mul(prevTarget, position1);
			position2.mul(prevTarget, position2);
			position3.mul(prevTarget, position3);
			position4.mul(prevTarget, position4);
			
			List<Vector4f> boundingPoints = new ArrayList();
			
			boundingPoints.add(position1);
			boundingPoints.add(position2);
			boundingPoints.add(position3);
			boundingPoints.add(position4);
			
			return(boundingPoints);
		}
	}

	public void setBBScale(float x, float y) {
		this.bbScaleX = x;
		this.bbScaleY = y;
	}
	
	public List<Integer> getFrames() {
		return(this.animationFrames);
	}
	
	public void setBoundingBox(float x0, float y0, float x2, float y2) {
		this.bbBorderX0 = x0;
		this.bbBorderY0 = y0;
		this.bbBorderX2 = x2;
		this.bbBorderY2 = y2;
	}
	
	
	public void loadTileSet(String texture) {
		this.tex.loadImage(texture);
	}
	
	public void changeTileUV(int tile, int w, int h) {		
		int positionX = tile % w;
		int positionY = (int)Math.floor((float)tile / (float)w);
	
		float [] textureUV;		
		
		textureUV = new float[] {										   
				(1f / w) *  positionX,      (1f / h) * (positionY),
				(1f / w) * (positionX + 1), (1f / h) * (positionY),
				(1f / w) *  positionX,      (1f / h) * (positionY + 1),
				(1f / w) * (positionX + 1), (1f / h) * (positionY + 1)
		};
			
//		this.TEXVBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
	}
	
}
