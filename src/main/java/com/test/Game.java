package com.test;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFWVidMode;

public class Game {
	private List<Entity> entityBuffer;
	private Timer timer;
	private Engine engine;
	private Controller controller;
	private long window;
	
	public Game() {
		this.initWindow();
		
		this.entityBuffer = new ArrayList<Entity>();
		
		this.timer = new Timer();
		
		this.timer.setFramerate(60);
		
		this.engine = new Engine(this.window);
		
		this.loadStartingEntities();
		
		this.controller = new Controller(this.engine.getCamera(), this.findByName("player", this.entityBuffer), this.engine);
	}
	
	public void loop() {
		while (!glfwWindowShouldClose(this.window)) {
			while(this.timer.elapsed()) {
				
				this.controller.pollEvents(this.window);
				
				this.updateEntities();
				
				this.engine.enableRender();
			}
			
			if (this.engine.canRender()) {
				double time = this.engine.render(this.getEntityBuffer());
				this.timer.fps(time);
			}
		}
		
		glfwTerminate();
	}
	
	public void updateEntities() {
		
//		System.out.println("Player: (" + this.entityBuffer.get(1).model.getX() + ", " + this.entityBuffer.get(1).model.getY() + ")");
		
//		for (int i = 0; i < this.entityBuffer.size(); i++) {
			this.entityBuffer.get(1).calculatePosition();
//		}
		
//		this.entityBuffer.get(1).model.calculateBoundingBox();
		
		for (int i = 0; i < this.entityBuffer.size(); i++) {
			if (i != 0) {
				this.entityBuffer.get(i).checkCollision(this.entityBuffer);
			}
			
			
			
//			this.entityBuffer.get(i).calculateForces();
			
			this.entityBuffer.get(i).model.updateAnimation();
		}
	}
	
	public List<Entity> getEntityBuffer() {
		return(this.entityBuffer);
	}
	
	private void initWindow() {
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
	}
	
	private void loadStartingEntities() {
		Entity background = new Entity();
		Entity foreground = new Entity();
		Entity player = new Entity();
		Entity pengu = new Entity();
		
		player.setName("player");
		background.setName("background");
		foreground.setName("foreground");
		pengu.setName("Heart Pengu");
		
		background.model.loadTextureAndAdapt("./assets/textures/stars.jpg");
		background.model.setScale(3.0f);
		background.setCollision(false);
		
		foreground.model.loadTextureAndAdapt("./assets/textures/ground.png");
		foreground.model.setScale(3.0f);
		foreground.model.setPosition(0, -160);
		foreground.model.setPosition(0, -160);

		player.model.loadAnimationAndAdapt("./assets/textures/blob.png", 3);
		player.model.setAnimationSpeed(5f);
		player.model.setPosition(-50, 100);
		player.model.setScale(0.5f);
		
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(150, 120);
		pengu.model.setPosition(150, 120);
		
		
		this.entityBuffer.add(background);
//		this.entityBuffer.add(foreground);
		this.entityBuffer.add(player);
		this.entityBuffer.add(pengu);
		
	}
	
	
	public Entity findByName(String name, List<Entity> entityBuffer) {
		
		for (int i = 0; i < entityBuffer.size(); i++) {
			if (entityBuffer.get(i).getName() == name) {
				return(entityBuffer.get(i));
			}
		}
		
		
		return(null);
	}
}
