package com.test;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;


public class Engine {
	
	private long window;
	
	public Camera camera;
	
	private boolean canRender;
	
	private boolean debug;
	
	private Entity tileSet;
	
	private Model sky;
	
	private int tileW;
	private int tileH;
	
	private int w;
	private int h;
	
	public Engine(long window) {
		
		this.window = window;
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(window, width, height);
		this.w = width.get(0);
		this.h = height.get(0);
		
		glfwMakeContextCurrent(this.window);
		
		GL.createCapabilities();
		
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		this.camera = new Camera(640, 480);
		
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		glfwSwapInterval(0);
		
		this.canRender = false;
		
		this.debug = false;
		
		this.sky = new Model();
		this.sky.loadTextureAndAdapt("./assets/textures/background2.png");
		this.sky.setScale(0.015f);
	}
	
	public void loadTiles(String texture, int w, int h) {
		this.tileSet = new Entity();
		this.tileSet.model.loadTileSet(texture);
		
		this.tileW = w;
		this.tileH = h;
		
		this.tileSet.model.setScale(1f / 8f);
	}
	
	
	public double render(List<Entity> entityBuffer, int[][] world) {
		double t1 = System.nanoTime();
		
		glClear(GL_COLOR_BUFFER_BIT);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		
		this.sky.renderSky(this.camera);
		
		this.renderTiles(world);
		
		for (int i = 0; i < entityBuffer.size(); i++) {
			if (!entityBuffer.get(i).getHitbox()) {
				entityBuffer.get(i).model.render(this.camera, this.debug);
			}
			
		}
		
		glfwSwapBuffers(this.window);
		
		this.canRender = false;
		
		double t2 = System.nanoTime();
		
		return(Math.round(((t2 - t1) / (double)1000000L) * (double)100) / (double)100);
	}
	
	private void renderTiles(int[][] world) {
		for (int i = 0; i < world[0].length; i++) {
			for (int j = 0; j < world.length; j++) {
				if (world[i][j] >= 0) {
					int positionX = i * 32 - (32 * (world[0].length / 2));
					int positionY = j * 32 - (32 * (world.length / 2));
					
					
//					System.out.println(this.camera.getPosition().x + this.w / 2);
//					System.out.println(this.camera.getPosition().x - this.w / 2);
//					System.out.println(this.camera.getPosition().y + this.h / 2);
//					System.out.println(this.camera.getPosition().y - this.h / 2);
					
					if (positionX - 16 >= -this.camera.getPosition().x + this.w / 2 ||
						positionX + 16 <= -this.camera.getPosition().x - this.w / 2 ||
						positionY - 16 >= -this.camera.getPosition().y + this.h / 2 ||
						positionY + 16 <= -this.camera.getPosition().y - this.h / 2) {
						
					} else {
						this.tileSet.model.changeTileUV(world[i][j], this.tileW, this.tileH);
						this.tileSet.model.setPosition(positionX, positionY);
						this.tileSet.model.render(this.camera, this.debug);
					}
					
//					if (((positionX + 16 < this.camera.getPosition().x + this.w / 2 && positionX + 16 > this.camera.getPosition().x - this.w / 2) ||
//						 (positionX - 16 < this.camera.getPosition().x + this.w / 2 && positionX - 16 > this.camera.getPosition().x - this.w / 2)) &&
//						((positionY + 16 < this.camera.getPosition().y ))) {
//						
//					}
					
					
					
				}
			}
		}
	}
	
	public void enableRender() {
		this.canRender = true;
	}
	
	public boolean canRender() {
		return(this.canRender);
	}
	
	public Camera getCamera() {
		return(this.camera);
	}
	
	public boolean getDebug() {
		return(this.debug);
	}
	
	public void setDebug(boolean flag) {
		this.debug = flag;
	}
}
