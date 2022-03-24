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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;

public class Game {
	private List<Entity> entityBuffer;
	private List<Entity> tileBuffer;
	
	private int worldSizeX;
	private int worldSizeY;
	private int[][] world;
	private int[][] background;
	
	private List<Hitbox> worldHitboxes;
	
	private Timer timer;
	private Engine engine;
	private Controller controller;
	private long window;
	
	public Game() {
		this.initWindow();
		
		this.entityBuffer = new ArrayList<Entity>();
		
		this.worldHitboxes = new ArrayList<Hitbox>();
		
		this.timer = new Timer();
		
		this.timer.setFramerate(60);
		
		this.engine = new Engine(this.window);
		
		this.engine.loadTiles("./assets/world/adventure pack/Assets.png", 25, 25);
//		this.engine.loadTiles("./assets/world/adventure pack/Assets.png", 25, 25);
		
		this.worldSizeX = 256;
		this.worldSizeY = 64;
		
		this.world = new int[this.worldSizeX][this.worldSizeY];
		this.background = new int[this.worldSizeX][this.worldSizeY];
		
		for (int i = 0; i < this.worldSizeX; i++) {
			for (int j = 0; j < this.worldSizeY; j++) {
				this.world[i][j] = -1;
				this.background[i][j] = -1;
			}
		}
		
		this.loadStartingEntities();
		
		this.loadStartingTiles();
		
		this.controller = new Controller(this.engine.getCamera(), (Player)this.findByName("player", this.entityBuffer), this.engine);
	}
	
	public void loop() {
		while (!glfwWindowShouldClose(this.window)) {
			while(this.timer.elapsed()) {
				
				this.controller.pollEvents(this.window);
				
				this.updateEntities();
				
				this.engine.enableRender();
			}
			
			if (this.engine.canRender()) {
				double time = this.engine.render(this.getEntityBuffer(), this.world, this.background);
				this.timer.fps(time);
			}
		}
		
		glfwTerminate();
	}
	
	public void updateEntities() {
		for (int i = 0; i < this.entityBuffer.size(); i++) {
//			System.out.println("Player: " + this.findByName("player", this.entityBuffer).model.getX() + ", " + this.findByName("player", this.entityBuffer).model.getY());
			
			this.entityBuffer.get(i).calculatePosition();
			
			this.entityBuffer.get(i).checkCollision(this.entityBuffer, this.worldHitboxes);
			
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
		Player player = new Player();
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

		player.model.loadAnimationAndAdapt("./assets/textures/gally3.png", 3, 8);
//		player.model.loadTextureAndAdapt("./assets/textures/gally.png");
		player.model.setAnimationSpeed(10f);
		player.model.setPosition(1200, 1500);
		player.setNewPosition(1200, 1500);
		player.setScale(0.5f);
		player.model.setBBScale(0.75f, 1f);
		player.allert.loadAnimationAndAdapt("./assets/textures/allert.png", 2, 2);
		
//		player.setGravity(0);i 
//		player.setGravity(-0.5f);
//		pengu.model.setAnimations(1);
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2, 1);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(1200, 2500);
		pengu.model.setPosition(1200, 2500);
		
//		blob.model.setAnimations(1);
		blob.model.loadAnimationAndAdapt("./assets/textures/gally2.png", 3, 5);
		blob.model.setAnimationSpeed(5f);
		blob.model.setPosition(-50f, 200);
		blob.model.setScale(0.25f);
//		blob.model.setBBScale(0.85f,  0.85f);
		
		block.model.loadTextureAndAdapt("./assets/textures/block1.png");
		block.model.setPosition(-350, -20);
		block.model.setPosition(-350, -20);
		block.setGravity(0);
		
		Entity ground = new Entity();
		
		ground.setName("ground");
		ground.setHitbox(true);
		ground.model.setBoundingBox(this.engine.getTileSize() * this.worldSizeX / 2, this.engine.getTileSize() / 2, -this.engine.getTileSize() * this.worldSizeX / 2, -this.engine.getTileSize() / 2);
		ground.setGravity(0);
		
		Hitbox groundHitbox = new Hitbox(this.engine.getTileSize() * this.worldSizeX / 2, this.engine.getTileSize() / 2, -this.engine.getTileSize() * this.worldSizeX / 2, -this.engine.getTileSize() / 2);
		Hitbox wallLeft = new Hitbox(-900, 200, -1000, 0);
		Hitbox wallRight = new Hitbox(-800, 200, -900, 0);
		
		this.worldHitboxes.add(groundHitbox);
		this.worldHitboxes.add(wallLeft);
		this.worldHitboxes.add(wallRight);
		

		this.entityBuffer.add(background); // WITHOUT THIS THE COLLISIONS DON'T WORK, IDK WHY
//		this.entityBuffer.add(foreground);
		this.entityBuffer.add(player);
		this.entityBuffer.add(pengu);
//		this.entityBuffer.add(blob);
//		this.entityBuffer.add(block);
//		this.entityBuffer.add(ground);
		
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
		
		for (int i = 0; i < this.worldSizeX; i++) {
			this.world[i][this.worldSizeY / 2] = 3;
			this.world[i][this.worldSizeY / 2 - 1] = 3 + 25;
			this.world[i][this.worldSizeY / 2 - 2] = 3 + 25;
		}
		
//		Random rand = new Random();
//		
//		for (int i = 0; i < this.worldSizeX; i++) {
//			this.world[i][this.worldSizeY / 2] = 2;
//			this.world[i][this.worldSizeY / 2 - 1] = 3;
//			
//			float ground = rand.nextFloat();
//			
//			if (ground <= 0.5f) {
//				this.world[i][this.worldSizeY / 2 - 2] = 3;
//			} else {
//				this.world[i][this.worldSizeY / 2 - 2] = 0;
//			}
//			
//			this.world[i][this.worldSizeY / 2 - 3] = 0;
//		
//			for (int j = 0; j < 2; j++) {
//				float ore = rand.nextFloat();
//				
//				if (ore <= 0.02) {
//					this.world[i][this.worldSizeY / 2 - 4 - j] = 105;
//				} else if (ore <= 0.07) {
//					this.world[i][this.worldSizeY / 2 - 4 - j] = 16;
//				} else if (ore <= 0.17){
//					this.world[i][this.worldSizeY / 2 - 4 - j] = 17;
//				} else if (ore <= 0.3) {
//					this.world[i][this.worldSizeY / 2 - 4 - j] = 18;
//				} else {
//					this.world[i][this.worldSizeY / 2 - 4 - j] = 0;
//				}
//			}
//		}
	
//		Structure house = new Structure();
//		
//		house.loadStructureWithHitbox("./assets/world/minecraft/house.str", this.engine.getTileSize());
//		house.applyStructure(-10, 1, this.world);
//		house.applyStructureWithHitbox(-2, 1, this.world, this.worldHitboxes);
//		
//		Structure tammy = new Structure();
//		
//		tammy.loadStructureWithHitbox("./assets/world/minecraft/tammy.str", this.engine.getTileSize());
//		tammy.applyStructureWithHitbox(20,  1, this.world, this.worldHitboxes);
//		
//		Structure tree = new Structure();
//		
//		tree.loadStructureWithHitbox("./assets/world/minecraft/tree.str", this.engine.getTileSize());
//		tree.applyStructure(-15, 1, this.world);
//		tree.applyStructureWithHitbox(-30, 1, this.world, this.worldHitboxes);	
		
//		Structure tree = new Structure();
//		tree.loadStructureWithHitbox("./assets/world/minecraft/tree.str", this.engine.getTileSize());
//		tree.applyStructureWithHitbox(0, 1, world, worldHitboxes);	
		
		Structure background = new Structure();
		
		background.loadStructure("./assets/world/adventure pack/background.str");
		background.applyStructure(0, 1, this.background);
		
		Structure map = new Structure();
//		map.loadStructureWithHitbox("./assets/world/adventure pack/map.str", this.engine.getTileSize());
//		map.applyStructureWithHitbox(0, 0, world, worldHitboxes);
		map.loadStructureWithHitbox("./assets/world/adventure pack/map.str", this.engine.getTileSize());
		map.applyStructureWithHitbox(0, 1, world, worldHitboxes);

		// world limit
		
		for (int i = 0; i < this.worldSizeX; i++) {
			for (int j = 0; j < this.worldSizeY; j++) {
				if ((i == 0 || i == this.worldSizeX - 1) ||
					(j == 0 || j == this.worldSizeY - 1)) {
					world[i][j] = 181;
				}
			}
		}
		
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
