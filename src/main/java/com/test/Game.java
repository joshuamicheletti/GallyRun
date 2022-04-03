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
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWWindowSizeCallback;
import static org.lwjgl.glfw.Callbacks.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Game {
	private List<Entity> entityBuffer; // list containing all the entities in the game
	
	private int worldSizeX; // size of the world in tiles
	private int worldSizeY; // size of the world in tiles
	private int[][] world;		// matrix containing all the tiles in the world
	private int[][] background; // matrix containing all the background tiles in the world
	
	private List<Hitbox> worldHitboxes; // list containing all the hitboxes of the tiles
	
	private Timer timer; // timer for monitoring frametimes, framerate and for regulating tickrate
	private Engine engine; // engine object to render entities and tiles
	private Controller controller; // controller object to listen to inputs for the controls
	private long window; // id of the window object
	
	// Constructor
	public Game() {
		// initialize the window
		this.initWindow();
		
		// initialize the buffers
		this.entityBuffer = new ArrayList<Entity>();
		
		this.worldHitboxes = new ArrayList<Hitbox>();
		
		// initialize the timer
		this.timer = new Timer();
		// and set the tickrate to 60tps
		this.timer.setFramerate(60);
		
		// initialize the engine to render on the window
		this.engine = new Engine(this.window);
		// load the tilemap
		this.engine.loadTiles("./assets/world/adventure pack/Assets.png", 25, 25);
		
		// define the world size
		this.worldSizeX = 256;
		this.worldSizeY = 64;
		
		// initialize the world and background tile values
		this.world = new int[this.worldSizeX][this.worldSizeY];
		this.background = new int[this.worldSizeX][this.worldSizeY];
		
		for (int i = 0; i < this.worldSizeX; i++) {
			for (int j = 0; j < this.worldSizeY; j++) {
				this.world[i][j] = -1;
				this.background[i][j] = -1;
			}
		}
		
		// load the starting tiles
		this.loadStartingEntities();
		
		// load the starting entities
		this.loadStartingTiles();
		
		// initialize the controller that controls the player
		this.controller = new Controller((Player)this.findByName("player", this.entityBuffer), this.engine);
	}
	
	// Game loop
	public void loop() {
		// run until the window should run
		while (!glfwWindowShouldClose(this.window)) {
			// makes the updates happen only after the tick time has passed (60 ticks per second, every 1/60s)
			while(this.timer.elapsed()) {
				// listen for inputs from the user
				this.controller.pollEvents(this.window);
				// update the entities accordingly
				this.updateEntities();
				// allow the renderer to render on the window (this allows to lock the framerate to the tick rate, but not vice versa
				this.engine.enableRender();
			}
			
			// if the engine can render to screen (once every 1/60s)
			if (this.engine.canRender()) {
				// render the background tiles, the foreground tiles and the entities (in that order) while keeping track of the time to do that
				double time = this.engine.render(this.entityBuffer, this.world, this.background);
				// print the frame time and fps info
				this.timer.fps(time);
			}
		}
		
		// terminate the window
		glfwTerminate();
	}
	
	// Method for updating entities (position, animation)
	public void updateEntities() {
		// scroll through the entities
		for (int i = 0; i < this.entityBuffer.size(); i++) {
//			System.out.println("Player: " + this.findByName("player", this.entityBuffer).model.getX() + ", " + this.findByName("player", this.entityBuffer).model.getY());
			Entity current = this.entityBuffer.get(i);
			
			if (current instanceof Enemy) {
				Enemy currentEnemy = (Enemy)current;
				currentEnemy.control();
			}
			
			if (current instanceof Player) {
				Player player = (Player)current;
				player.calculateState();
			}
			
			// calculate the new position of the entity (influenced by force, acceleration and speed)
			current.calculatePosition();
			
			// check for collisions against hitboxes or other entities and update the position to resolve the collision
			current.checkCollision(this.entityBuffer, this.worldHitboxes);
			
			// if we're updating the position of the player
			if (current.getName() == "player") {
				// move the camera according to the new position of the player
				this.engine.camera.setPosition(new Vector3f(-current.model.getX(), -current.model.getY(), 0));
			}
			
			// update the animation of the entity
			current.updateAnimation();
		}
	}
	
	
	// Method for initializing the window
	private void initWindow() {
		// check if glwf can be initialized
		if (!glfwInit()) {
			throw new IllegalStateException("Failed to initialize GLFW!");
		}
		
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		// create a new glfw window of resolution 640x480
		this.window = glfwCreateWindow(1280, 720, "LWJGL", 0, 0);
		
		// check if the window was succesfully created
		if (this.window == 0) {
			throw new IllegalStateException("Failed to create window!");
		}
		
		// get the screen resolution
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		// place the window at the center of the screen
		glfwSetWindowPos(this.window, (videoMode.width() - 1280) / 2, (videoMode.height() - 720) / 2);
		// make the window visible
		glfwShowWindow(this.window);
		// update the framebuffer and camera to adapt to the new resolution when the window is resized
		glfwSetFramebufferSizeCallback(this.window, this.resizeWindow);
	}
	
	// callback function for updating the window resolution when it gets resized
	private GLFWFramebufferSizeCallback resizeWindow = new GLFWFramebufferSizeCallback() {
		public void invoke(long window, int width, int height){
			engine.setWindowSize(width, height);
		}
	};
	
	// method for loading the entities when the game loads
	private void loadStartingEntities() {
		// creating the entity objects
		Player player = new Player();
		Entity pengu = new Entity();
		Entity blob = new Entity();
		Enemy enemy = new Enemy();
		
		// giving them a name
		player.setName("player");
		pengu.setName("Heart Pengu");
		blob.setName("blob");
		enemy.setName("enemy");
		
		// setting the parameters of each object
		player.model.loadAnimationAndAdapt("./assets/textures/gally5.png", 3, 10);
		player.model.setAnimationSpeed(10f);
		player.model.setPosition(-6000, 1500);
		player.setNewPosition(-6000, 1500);
		player.setScale(0.5f);
		player.model.setBBScale(0.75f, 1f);
		player.allert.loadAnimationAndAdapt("./assets/textures/allert.png", 2, 2);
	
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2, 1);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(-6700, 2500);
		pengu.model.setPosition(-6700, 2500);
		
		enemy.model.loadAnimationAndAdapt("./assets/textures/gally5.png", 3, 10);
		enemy.model.setAnimationSpeed(10f);
		enemy.model.setPosition(-5000, 1500);
		enemy.model.setScale(0.25f);
		enemy.model.setBBScale(0.75f, 1f);
		
		DoubleJump powerup = new DoubleJump();
		powerup.model.setPosition((-this.worldSizeX / 2 + 49) * this.engine.getTileSize(), (-this.worldSizeY / 2 + 20 + 18) * this.engine.getTileSize());
		
		this.entityBuffer.add(player);
		this.entityBuffer.add(pengu);
		this.entityBuffer.add(enemy);
		this.entityBuffer.add(powerup);
		
		
		for (int i = 0; i < 10; i++) {
			Coin coin = new Coin();
			coin.model.setScale(0.25f);
			coin.model.setAnimationSpeed(10f);
			coin.model.setPosition((-this.worldSizeX / 2) * this.engine.getTileSize() + ((31 + (i % 2)) * this.engine.getTileSize()),
								   (-this.worldSizeY / 2 + 20) * this.engine.getTileSize() + ((27 - (i / 2)) * this.engine.getTileSize()));
			this.entityBuffer.add(coin);
		}

		// loading them into the entityBuffer
		
//		Hitbox groundHitbox = new Hitbox(this.engine.getTileSize() * this.worldSizeX / 2, this.engine.getTileSize() / 2, -this.engine.getTileSize() * this.worldSizeX / 2, -this.engine.getTileSize() / 2);
//		Hitbox wallLeft = new Hitbox(-900, 200, -1000, 0);
//		Hitbox wallRight = new Hitbox(-800, 200, -900, 0);
//		
//		this.worldHitboxes.add(groundHitbox);
//		this.worldHitboxes.add(wallLeft);
//		this.worldHitboxes.add(wallRight);
		
	}
	
	// method for loading the starting tiles that compose the world
	private void loadStartingTiles() {
		
		// floor for reference
		for (int i = 0; i < this.worldSizeX; i++) {
			this.world[i][this.worldSizeY / 2] = 181;
//			this.world[i][this.worldSizeY / 2 - 1] = 3 + 25;
//			this.world[i][this.worldSizeY / 2 - 2] = 3 + 25;
		}
		
		for (int i = 0; i < this.worldSizeY; i++) {
			this.world[this.worldSizeX / 2][i] = 181;
		}
		
		int mapX = -this.worldSizeX / 2;
		int mapY = -this.worldSizeY / 2 + 20;
		
		// create a background structure, this will not have hitboxes and will function as a background for the foreground tiles
		Structure background = new Structure();
		background.loadStructure("./assets/world/adventure pack/background.str");
		background.applyStructure(mapX, mapY, this.background);
		
		// additional structures to add to the existing foreground and background areas for ease of use
		Structure jumpPower = new Structure();
		jumpPower.loadStructure("./assets/world/adventure pack/jumpPower.str");
//		jumpPower.applyStructure(-this.worldSizeX / 2 + 31, 8, this.world);
//		jumpPower.applyStructure(-this.worldSizeX / 2 + 32, 8, this.world);
		jumpPower.applyStructure(mapX + 31, mapY + 20, this.world);
		jumpPower.applyStructure(mapX + 32, mapY + 20, this.world);
		
		Structure tree = new Structure();
		tree.loadStructureWithHitbox("./assets/world/adventure pack/trees.str", this.engine.getTileSize());
		tree.applyStructureWithHitbox(mapX + 3, mapY + 27, this.world, this.worldHitboxes);
		
		Structure treeBackground = new Structure();
		treeBackground.loadStructure("./assets/world/adventure pack/treesB.str");
		treeBackground.applyStructure(mapX + 3, mapY + 27, this.background);
		
//		tree.applyStructure(mapX + 5, mapY + 27, this.background);
//		treeBackground.applyStructure(mapX + 5, mapY + 27, world);
		
		
		// create a foreground structure, this will contain the tiles that can be collided with, and will function as the interactive part of the map
		Structure map = new Structure();
		map.loadStructureWithHitbox("./assets/world/adventure pack/map.str", this.engine.getTileSize());
		map.applyStructureWithHitbox(mapX, mapY, this.world, this.worldHitboxes);
		
		
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
	
	// method for finding an entity in the entityBuffer by name
	public Entity findByName(String name, List<Entity> entityBuffer) {
		for (int i = 0; i < entityBuffer.size(); i++) {
			if (entityBuffer.get(i).getName() == name) {
				return(entityBuffer.get(i));
			}
		}
		return(null);
	}
}
