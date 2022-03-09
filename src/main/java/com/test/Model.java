package com.test;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Model {
	private int drawCount;
	private int VBOid;
	private int TEXVBOid;
	private int INDid;
	
	private float x;
	private float y;
	private float rotationValue;
	private float scaleValue;
	
	private int animationSteps;
	
	private int animationPosition;
	
	public Texture tex;
	public Shader shader;
	public Camera camera;
	
	private Matrix4f scale;
	private Matrix4f rotation;
	private Matrix4f translation;
	private Matrix4f target;
	
	private float scaleMul;
	
	private int counter;
	
	private float animationSpeed;
	
	public Model(Camera camera) {
		
		float [] vertices = new float[] {
			-0.5f,  0.5f, // TOP LEFT
			 0.5f,  0.5f, // TOP RIGHT
			-0.5f, -0.5f, // BOTTOM LEFT
			 0.5f, -0.5f  // BOTTOM RIGHT
		};
			
			
		float [] textureUV = new float[] {
			0, 0,
			1, 0,
			0, 1,
			1, 1
		};
		
		
		int[] indices = new int[] {
			0, 1, 2,
			2, 1, 3
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

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		this.tex = new Texture();
		this.shader = new Shader("shader");
		
		this.scaleMul = 256;
		
		this.scale = new Matrix4f().scale(this.scaleMul);
		this.rotation = new Matrix4f();
		this.translation = new Matrix4f();
		
		this.target = new Matrix4f();
		
		this.camera = camera;
		
		this.animationSteps = 0;
		
		this.animationPosition = 0;
		
		this.counter = 0;
		
		this.animationSpeed = 1.0f;
	}
	
	public void render() {
//		if (this.animationSteps != 0 && this.counter % 30 == 0) {
//			this.updateAnimation();
//		}
		
		// this.counter++;
		
		this.getProjection();
		
		this.tex.bind(0);
		this.shader.bind();
		this.shader.setUniform("sampler", 0);
		this.shader.setUniform("projection", this.camera.getProjection().mul(this.target));
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
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
	
	public void loadAnimationAndAdapt(String filename, int steps) {
		this.tex.loadImage(filename);
		
		this.adaptToSheet(steps);
	}
	
	
	public void adaptToTexture() {
		float x;
		float y;
		
		if (this.tex.getWidth() > this.tex.getHeight()) {
			x = 0.5f;
			y = x * ((float)this.tex.getHeight() / (float)this.tex.getWidth());
		} else if (this.tex.getWidth() < this.tex.getHeight()) {
			y = 0.5f;
			x = y * ((float)this.tex.getWidth() / (float)this.tex.getHeight());
		} else {
			x = 0.5f;
			y = 0.5f;
		}
		
		float [] vertices = new float[] {
				-x,  y, // TOP LEFT
				 x,  y, // TOP RIGHT
				-x, -y, // BOTTOM LEFT
				 x, -y  // BOTTOM RIGHT
		};
		
		this.VBOid = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, this.VBOid);
		glBufferData(GL_ARRAY_BUFFER, createBuffer(vertices), GL_STATIC_DRAW);
	}
	
	public void adaptToSheet(int steps) {
		this.animationSteps = steps;
		
		float x;
		float y;
		
		float spriteWidth = (float)this.tex.getWidth() / this.animationSteps;
		
		if (spriteWidth > this.tex.getHeight()) {
			x = 0.5f;
			y = x * ((float)this.tex.getHeight() / spriteWidth);
		} else if (spriteWidth < this.tex.getHeight()) {
			y = 0.5f;
			x = y * (spriteWidth / (float)this.tex.getHeight());
		} else {
			x = 0.5f;
			y = 0.5f;
		}
		
		float [] vertices = new float[] {
				-x,  y, // TOP LEFT
				 x,  y, // TOP RIGHT
				-x, -y, // BOTTOM LEFT
				 x, -y  // BOTTOM RIGHT
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
	
	
	public void updateAnimation() {
		this.counter++;
		if ((float)this.counter % Math.round(60 / this.animationSpeed) == 0) {
			float [] textureUV = new float[] {
					1f / this.animationSteps * this.animationPosition,       0,
					1f / this.animationSteps * (this.animationPosition + 1), 0,
					1f / this.animationSteps * this.animationPosition,       1,
					1f / this.animationSteps * (this.animationPosition + 1), 1
			};
			
			this.animationPosition++;
			
			if (this.animationPosition == this.animationSteps) {
				this.animationPosition = 0;
			}
			
			this.TEXVBOid = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
			glBufferData(GL_ARRAY_BUFFER, createBuffer(textureUV), GL_STATIC_DRAW);
		}
	}
	
	public void setAnimationSpeed(float animationSpeed) {
		this.animationSpeed = animationSpeed;
	}
}
