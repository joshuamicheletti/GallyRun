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
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;

public class Game {
	private List<Entity> entityBuffer;
	private List<Entity> tileBuffer;
	
	private int worldSizeX;
	private int worldSizeY;
	private int[][] world;
	
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
		
		this.engine.loadTiles("./assets/textures/minecraft.png", 16, 16);
		
		this.worldSizeX = 128;
		this.worldSizeY = 128;
		
		this.world = new int[this.worldSizeX][this.worldSizeY];
		
		for (int i = 0; i < this.worldSizeX; i++) {
			for (int j = 0; j < this.worldSizeY; j++) {
				this.world[i][j] = -1;
			}
		}
		
		this.loadStartingEntities();
		
		this.loadStartingTiles();
		
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
				double time = this.engine.render(this.getEntityBuffer(), this.world);
				this.timer.fps(time);
			}
		}
		
		glfwTerminate();
	}
	
	public void updateEntities() {
		for (int i = 0; i < this.entityBuffer.size(); i++) {
//			System.out.println("Player: " + this.findByName("player", this.entityBuffer).model.getX() + ", " + this.findByName("player", this.entityBuffer).model.getY());
			
			this.entityBuffer.get(i).calculatePosition();
			
			this.entityBuffer.get(i).checkCollision(this.entityBuffer);
			
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
		background.model.setScale(0f);
		background.setCollision(false);
		background.setGravity(0);
		
		foreground.model.loadTextureAndAdapt("./assets/textures/block1.png");
		foreground.model.setScale(3.0f);
		foreground.model.setPosition(0, -200);
		foreground.model.setPosition(0, -200);
		foreground.setNewPosition(0, -200);
		foreground.setGravity(0);

		player.model.loadAnimationAndAdapt("./assets/textures/gally2.png", 3, 8);
//		player.model.loadTextureAndAdapt("./assets/textures/gally.png");
		player.model.setAnimationSpeed(10f);
		player.model.setPosition(-50, 200);
		player.setNewPosition(-50, 200);
		player.model.setScale(0.5f);
		player.model.setBBScale(0.85f, 0.85f);
		
//		player.setGravity(0);
//		player.setGravity(-0.5f);
//		pengu.model.setAnimations(1);
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2, 1);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(150, 120);
		pengu.model.setPosition(150, 120);
		
//		blob.model.setAnimations(1);
		blob.model.loadAnimationAndAdapt("./assets/textures/gally2.png", 3, 5);
		blob.model.setAnimationSpeed(5f);
		blob.model.setPosition(-50f, 200);
		blob.model.setScale(0.25f);
		blob.model.setBBScale(0.85f,  0.85f);
		
		block.model.loadTextureAndAdapt("./assets/textures/block1.png");
		block.model.setPosition(-350, -20);
		block.model.setPosition(-350, -20);
		block.setGravity(0);
		
		Entity ground = new Entity();
		
		ground.setName("ground");
		ground.setHitbox(true);
		ground.model.setBoundingBox(2048, 16, -2048, 0);
		ground.setGravity(0);
		
		

		this.entityBuffer.add(background); // WITHOUT THIS THE COLLISIONS DON'T WORK, IDK WHY
//		this.entityBuffer.add(foreground);
		this.entityBuffer.add(player);
//		this.entityBuffer.add(pengu);
//		this.entityBuffer.add(blob);
//		this.entityBuffer.add(block);
		this.entityBuffer.add(ground);
		
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
	
	private void loadStartingTiles() {
		Random rand = new Random();
		
		for (int i = 0; i < 128; i++) {
			this.world[i][64] = 2;
			this.world[i][63] = 3;
			
			float ground = rand.nextFloat();
			
			if (ground <= 0.5f) {
				this.world[i][62] = 3;
			} else {
				this.world[i][62] = 0;
			}
			
//			this.world[i][62] = 0;
			this.world[i][61] = 0;
		
			for (int j = 0; j < 2; j++) {
				float ore = rand.nextFloat();
				
				if (ore <= 0.02) {
					this.world[i][60 - j] = 105;
				} else if (ore <= 0.07) {
					this.world[i][60 - j] = 16;
				} else if (ore <= 0.17){
					this.world[i][60 - j] = 17;
				} else if (ore <= 0.3) {
					this.world[i][60 - j] = 18;
				} else {
					this.world[i][60 - j] = 0;
				}
			}
			
			
			
			
//			for (int j = 0; j < 128; j++) {
//				this.world[i][j] = i;
//			}
		}
		
//		this.world[99][99] = 1;
		
		
		
		
		
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
