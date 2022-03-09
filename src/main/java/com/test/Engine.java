package com.test;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;


public class Engine {
	
	private long window;
	
	private Entity player;
	private Entity background;
	private Entity boyo;
	private Entity pengu;
	
	private List<Entity> entityBuffer;
	
	private Camera camera;
	
	private Timer timer;
	
	private float x;
	private float y;
	private float cameraX;
	private float cameraY;
	private float speed;
	private float cameraSpeed;
	private float rotation;
	private float scale;
	
	private double frameCap;
	private double time;
	private double unprocessed;
	private double frameTime;
	private int frames;
	
	public Engine() {
		
		if (!glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW!");
		}
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		this.window = glfwCreateWindow(640, 480, "LWJGL", 0, 0);
		
		if (this.window == 0) {
			throw new IllegalStateException("Failed to create window!");
		}
		
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		glfwSetWindowPos(this.window, (videoMode.width() - 640) / 2, (videoMode.height() - 480) / 2);
		
		glfwShowWindow(this.window);
		
		glfwMakeContextCurrent(this.window);
		
		GL.createCapabilities();
		
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		this.camera = new Camera(640, 480);
		
		this.pengu = new Entity(camera);
		this.pengu.model.loadTextureAndAdapt("./assets/textures/pengu.png");
		
		
		this.pengu.model.setPosition(200, 0);
		
		Random rand = new Random();
		
		this.entityBuffer = new ArrayList<Entity>();
		
		for (int i = 0; i < 10; i++) {
			this.entityBuffer.add(new Entity(camera));
			this.entityBuffer.get(i).model.loadTextureAndAdapt("./assets/textures/blend.png");
			this.entityBuffer.get(i).model.setScale(0.3f);
			this.entityBuffer.get(i).model.setPosition((rand.nextFloat() - 0.5f) * 400f, (rand.nextFloat() - 0.5f) * 400f);
		}
		
		this.entityBuffer.add(new Entity(camera));
		
		this.entityBuffer.get(this.entityBuffer.size() - 1).model.loadTextureAndAdapt("./assets/textures/aspectRatio.jpg");

		
		//this.entityBuffer.get(this.entityBuffer.size() - 1).model.setScale(128f);
		
		this.boyo = new Entity(camera);
		
		this.boyo.model.loadTextureAndAdapt("./assets/textures/boyo.jpg");
		
		this.background = new Entity(camera);
		
		this.background.model.loadTextureAndAdapt("./assets/textures/grass.png");
		
		this.background.model.setScale(3);
		
		this.player = new Entity(camera);
		
		//this.player.model.loadTextureAndAdapt("./assets/textures/sprite.png");
		this.player.model.loadAnimationAndAdapt("./assets/textures/sprite.png", 6);
		this.player.model.setAnimationSpeed(9.0f);
		
		this.x = 0;
		this.y = 0;
		this.cameraX = 0;
		this.cameraY = 0;
		this.speed = 0.03f;
		this.cameraSpeed = 3.0f;
		this.rotation = 0;
		this.scale = 0;
		
		
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		this.timer = new Timer();
			
		this.timer.setFramerate(60);
	}
	
	
	public void loop() {		
		while (!glfwWindowShouldClose(this.window)) {
			boolean canRender = false;
			
//			this.timer.update();
			
//			double time_2 = this.timer.getTime();
////			double time_2 = Timer.getTime();
//			double passed = time_2 - time;
//			this.unprocessed += passed;
			
//			this.time = time_2;
			
			
			while(this.timer.elapsed()) {
//			while(this.unprocessed >= this.frameCap) {
//				this.unprocessed -= this.frameCap;
				canRender = true;
				
				glfwPollEvents();
				
				if (glfwGetKey(this.window, GLFW_KEY_A) == GL_TRUE) {
					this.x -= this.cameraSpeed;
				} else if (glfwGetKey(this.window, GLFW_KEY_D) == GL_TRUE) {
					this.x += this.cameraSpeed;
				}
				
				if (glfwGetKey(this.window, GLFW_KEY_W) == GL_TRUE) {
					this.y += this.cameraSpeed;
				} else if (glfwGetKey(this.window, GLFW_KEY_S) == GL_TRUE) {
					this.y -= this.cameraSpeed;
				}
				
				if (glfwGetKey(this.window, GLFW_KEY_E) == GL_TRUE) {
					this.rotation += this.speed;
				} else if (glfwGetKey(this.window, GLFW_KEY_Q) == GL_TRUE) {
					this.rotation -= this.speed;
				}
				
				if (glfwGetKey(this.window, GLFW_KEY_LEFT) == GL_TRUE) {
					this.cameraX += this.cameraSpeed;
				} else if (glfwGetKey(this.window, GLFW_KEY_RIGHT) == GL_TRUE) {
					this.cameraX -= this.cameraSpeed;
				}
				
				if (glfwGetKey(this.window, GLFW_KEY_UP) == GL_TRUE) {
					this.cameraY -= this.cameraSpeed;
				} else if (glfwGetKey(this.window, GLFW_KEY_DOWN) == GL_TRUE) {
					this.cameraY += this.cameraSpeed;
				}
				
				if (glfwGetKey(this.window, GLFW_KEY_ESCAPE) == GL_TRUE) {
					glfwSetWindowShouldClose(window, true);
				}
				
				this.camera.setPosition(new Vector3f(this.cameraX, this.cameraY, 1));
				
				this.scale += this.speed;
				
				this.player.model.setPosition(this.x, this.y);
				this.player.model.setRotation(this.rotation);
				
				this.player.model.updateAnimation();
				this.player.model.setScale((float)Math.sin(this.scale) * 0.5f + 1f);
				this.player.model.setAnimationSpeed((float)Math.sin(this.scale) * 5f + 8f);
				
			}
			

			if (canRender) {
				this.render();
				
//				try {
//					Thread.sleep(20);
//				} catch (Exception e) {
//					System.out.println(e);
//				}
				
				this.timer.fps();
			}
		}
		
		glfwTerminate();
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		
		this.background.model.render();
		this.boyo.model.render();
		this.pengu.model.render();
		
		for (int i = 0; i < this.entityBuffer.size(); i++) {
			this.entityBuffer.get(i).model.render();
		}
		
		this.player.model.render();
		
		//glfwSwapInterval(1);
		glfwSwapBuffers(this.window);
	}
}
