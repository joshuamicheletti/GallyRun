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
			
			// calculate the new position of the entity (influenced by force, acceleration and speed)
			this.entityBuffer.get(i).calculatePosition();
			
			// check for collisions against hitboxes or other entities and update the position to resolve the collision
			this.entityBuffer.get(i).checkCollision(this.entityBuffer, this.worldHitboxes);
			
			// if we're updating the position of the player
			if (this.entityBuffer.get(i).getName() == "player") {
				// move the camera according to the new position of the player
				this.engine.camera.setPosition(new Vector3f(-this.entityBuffer.get(i).model.getX(), -this.entityBuffer.get(i).model.getY(), 0));
			}
			
			// update the animation of the entity
			this.entityBuffer.get(i).updateAnimation();
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
		
//		glfwSetWindowSizeCallback(this.window, this.windowResize);
//		glfwSetFramebufferSizeCallback(this.window, function -> {
//			
//		});
		
//		GLFWFramebufferSizeCallback(this.window, resizeWindow);
		glfwSetFramebufferSizeCallback(this.window, resizeWindow);
//		glfwSetWindowSizeCallback();
//		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
//				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//		});
	}
	
	 private static GLFWFramebufferSizeCallback resizeWindow = new GLFWFramebufferSizeCallback(){
			public void invoke(long window, int width, int height){
			  glViewport(0,0,width,height);
			  //update any other window vars you might have (aspect ratio, MVP matrices, etc)
			}
	  };
	
	
	
	// method for loading the entities when the game loads
	private void loadStartingEntities() {
		// creating the entity objects
		Player player = new Player();
		Entity pengu = new Entity();
		Entity blob = new Entity();
		
		// giving them a name
		player.setName("player");
		pengu.setName("Heart Pengu");
		blob.setName("blob");
		
		// setting the parameters of each object
		player.model.loadAnimationAndAdapt("./assets/textures/gally3.png", 3, 8);
		player.model.setAnimationSpeed(10f);
		player.model.setPosition(1200, 1500);
		player.setNewPosition(1200, 1500);
		player.setScale(0.5f);
		player.model.setBBScale(0.75f, 1f);
		player.allert.loadAnimationAndAdapt("./assets/textures/allert.png", 2, 2);
	
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2, 1);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(1200, 2500);
		pengu.model.setPosition(1200, 2500);
		
		blob.model.loadAnimationAndAdapt("./assets/textures/gally2.png", 3, 5);
		blob.model.setAnimationSpeed(5f);
		blob.model.setPosition(-50f, 200);
		blob.model.setScale(0.25f);

		// loading them into the entityBuffer
		this.entityBuffer.add(player);
		this.entityBuffer.add(pengu);
//		this.entityBuffer.add(blob);
		
		
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
			this.world[i][this.worldSizeY / 2] = 3;
			this.world[i][this.worldSizeY / 2 - 1] = 3 + 25;
			this.world[i][this.worldSizeY / 2 - 2] = 3 + 25;
		}
		
		// create a background structure, this will not have hitboxes and will function as a background for the foreground tiles
		Structure background = new Structure();
		background.loadStructure("./assets/world/adventure pack/background.str");
		background.applyStructure(0, 1, this.background);
		
		// create a foreground structure, this will contain the tiles that can be collided with, and will function as the interactive part of the map
		Structure map = new Structure();
		map.loadStructureWithHitbox("./assets/world/adventure pack/map.str", this.engine.getTileSize());
		map.applyStructureWithHitbox(0, 1, this.world, this.worldHitboxes);
		
		// additional structures to add to the existing foreground and background areas for ease of use
		Structure jumpPower = new Structure();
		jumpPower.loadStructure("./assets/world/adventure pack/jumpPower.str");
		jumpPower.applyStructure(13, 6, this.world);
		jumpPower.applyStructure(14, 6, this.world);
		
		
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
