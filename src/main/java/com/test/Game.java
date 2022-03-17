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

import org.joml.Vector3f;
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
		
		for (int i = 0; i < this.entityBuffer.size(); i++) {
			this.entityBuffer.get(i).calculatePosition();
			
			if (i != 0) {
				this.entityBuffer.get(i).checkCollision(this.entityBuffer);
			}
			
			this.entityBuffer.get(i).applyNewPosition();
			
			Vector3f position = new Vector3f(-this.findByName("player", this.entityBuffer).model.getX(), -this.findByName("player", this.entityBuffer).model.getY(), 0);
			
//			Vector3f position = new Vector3f(-this.findByName("player", this.entityBuffer).model.getX(),this.engine.camera.getPosition().y, 0);
			
			this.engine.camera.setPosition(position);
			
			this.entityBuffer.get(i).updateAnimation();
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
		Entity blob = new Entity();
		Entity block = new Entity();
		
		player.setName("player");
		background.setName("background");
		foreground.setName("foreground");
		pengu.setName("Heart Pengu");
		blob.setName("blob");
		block.setName("block");
		
		
		background.model.loadTextureAndAdapt("./assets/textures/grid.png");
		background.model.setScale(3.0f);
		background.setCollision(false);
		background.setGravity(0);
		
		foreground.model.loadTextureAndAdapt("./assets/textures/block1.png");
		foreground.model.setScale(3.0f);
		foreground.model.setPosition(0, -200);
		foreground.model.setPosition(0, -200);
		foreground.setNewPosition(0, -200);
		foreground.setGravity(0);

		player.model.loadAnimationAndAdapt("./assets/textures/gally2.png", 3, 5);
//		player.model.loadTextureAndAdapt("./assets/textures/gally.png");
		player.model.setAnimationSpeed(10f);
		player.model.setPosition(-50, 200);
		player.setNewPosition(-50, 200);
		player.model.setScale(0.5f);
		player.model.setBBScale(0.85f, 1f);
		
//		player.setGravity(0);
//		player.setGravity(-0.5f);
//		pengu.model.setAnimations(1);
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2, 1);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(150, 120);
		pengu.model.setPosition(150, 120);
		
//		blob.model.setAnimations(1);
		blob.model.loadAnimationAndAdapt("./assets/textures/blob.png", 3, 1);
		blob.model.setAnimationSpeed(5f);
		blob.model.setPosition(-50f, 200);
		blob.model.setScale(0.25f);
		
		block.model.loadTextureAndAdapt("./assets/textures/block1.png");
		block.model.setPosition(-350, -20);
		block.model.setPosition(-350, -20);
		block.setGravity(0);

		
		
		this.entityBuffer.add(background);
		this.entityBuffer.add(foreground);
		this.entityBuffer.add(player);
//		this.entityBuffer.add(pengu);
		this.entityBuffer.add(blob);
		this.entityBuffer.add(block);
		
//		for (int i = 0; i < 20; i++) {
//			Entity entity = new Entity();
//			
//			entity.model.loadAnimationAndAdapt("./assets/textures/gally.png", 3);
//			entity.model.setScale(0.25f);
//			entity.model.setPosition(i % 5 * 120 + i, i * 120 + 200);
//			entity.setNewPosition(i % 5 * 120 + i, i * 120 + 200);
//			this.entityBuffer.add(entity);
//			
//		}
		
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
