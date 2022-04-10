package com.test;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;


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
	
	private double winTimer;
	
	private Mixer mixer;
	
	private long audioContext;
	private long audioDevice;
	
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
		this.worldSizeX = 118;
		this.worldSizeY = 69;
		
		// initialize the world and background tile values
		this.world = new int[this.worldSizeX][this.worldSizeY];
		this.background = new int[this.worldSizeX][this.worldSizeY];
		
		for (int i = 0; i < this.worldSizeX; i++) {
			for (int j = 0; j < this.worldSizeY; j++) {
				this.world[i][j] = -1;
				this.background[i][j] = -1;
			}
		}
		
		this.mixer = new Mixer();
		
		// load the starting tiles
		this.loadStartingEntities();
		
		// load the starting entities
		this.loadStartingTiles();
		
		// initialize the controller that controls the player
		this.controller = new Controller((Player)this.findByName("player", this.entityBuffer), this.engine);
		
		this.winTimer = 0;
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
				
				
				if (this.mixer.playingSong() == 1) {
					System.out.println("Winning song!");
				}
			}
			
			// if the engine can render to screen (once every 1/60s)
			if (this.engine.canRender()) {
				// render the background tiles, the foreground tiles and the entities (in that order) while keeping track of the time to do that
				double time = this.engine.render(this.entityBuffer, this.world, this.background);
				// print the frame time and fps info
				this.timer.fps(time);
			}
			
			if (this.winTimer != 0) {
				this.executeWin();
			}
		}
		
		// destroy audio
		alcDestroyContext(this.audioContext);
		alcCloseDevice(this.audioDevice);
		
		// terminate the window
		glfwTerminate();
	}
	
	// Method for updating entities (position, animation)
	public void updateEntities() {
		// scroll through the entities
		for (int i = 0; i < this.entityBuffer.size(); i++) {
			Entity current = this.entityBuffer.get(i);
			
			// update the AI of the enemy
			if (current instanceof Enemy) {
				Enemy currentEnemy = (Enemy)current;
				currentEnemy.control();
			}
			
			// update the timers on the player
			if (current instanceof Player) {
				Player player = (Player)current;
				
//				System.out.println("x: " + player.model.getX() + ", y: " + player.model.getY());
				
				player.calculateState();
			}
			
			if (current instanceof Portal) {
				Portal portal = (Portal)current;
				
				if (portal.getWin() && this.winTimer == 0) {
					this.winTimer = System.nanoTime() / 1000000000L;
				}
			}
			
			// calculate the new position of the entity (influenced by force, acceleration and speed)
			current.calculatePosition();
			
			// check for collisions against hitboxes or other entities and update the position to resolve the collision
			current.checkCollision(this.entityBuffer, this.worldHitboxes);
			
			// if we're updating the position of the player
			if (current instanceof Player) {
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
		
		// AUDIO
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		this.audioDevice = alcOpenDevice(defaultDeviceName);
		
		int[] attributes = {0};
		this.audioContext = alcCreateContext(audioDevice, attributes);
		
		alcMakeContextCurrent(this.audioContext);
		
		ALCCapabilities alcCapabilities = ALC.createCapabilities(this.audioDevice);
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		if (!alCapabilities.OpenAL10) {
			assert false : "Audio library not supported";
		}
	}
	
	// callback function for updating the window resolution when it gets resized
	private GLFWFramebufferSizeCallback resizeWindow = new GLFWFramebufferSizeCallback() {
		public void invoke(long window, int width, int height){
			engine.setWindowSize(width, height);
		}
	};
	
	// method for loading the entities when the game loads
	private void loadStartingEntities() {
		int mapX = -this.worldSizeX / 2 * this.engine.getTileSize();
		int mapY = (-this.worldSizeY / 2 + 5) * this.engine.getTileSize();
		
		
		// creating the entity objects
		Player player = new Player();
		Entity pengu = new Entity();
		Entity blob = new Entity();
		Enemy enemy = new Enemy(player);
		Enemy enemy2 = new Enemy(player);
		Enemy enemy3 = new Enemy(player);
		Boss boss = new Boss(player, this.entityBuffer, this.mixer);
		
		// giving them a name
		player.setName("player");
		pengu.setName("Heart Pengu");
		blob.setName("blob");
		enemy.setName("enemy");
		enemy2.setName("enemy2");
		enemy3.setName("enemy3");
		boss.setName("boss");
		
		// setting the parameters of each object
		player.model.loadAnimationAndAdapt("./assets/textures/gally5.png", 3, 10);
		player.model.setAnimationSpeed(10f);
		player.model.setPosition(mapX + 1300, mapY + 1753);
//		player.setNewPosition(mapX + 1300, mapY + 1753);
//		player.model.setPosition(mapX + 93 * this.engine.getTileSize(), mapY + 60 * this.engine.getTileSize());
//		mapY + 1852
		player.setScale(0.5f);
		player.model.setBBScale(0.75f, 1f);
		player.allert.loadAnimationAndAdapt("./assets/textures/allert.png", 2, 3);
		player.setSleep(true);
	
		pengu.model.loadAnimationAndAdapt("./assets/textures/pengu2.png", 2, 1);
		pengu.model.setAnimationSpeed(1f);
		pengu.model.setPosition(-6700, 2500);
		pengu.model.setPosition(-6700, 2500);
		
		enemy.model.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy.model.setAnimationSpeed(10f);
		enemy.model.setPosition(mapX + 61 * this.engine.getTileSize(), mapY + 2500);
		enemy.model.setScale(0.5f);
		enemy.model.setBBScale(0.75f, 1f);
		enemy.setBehaviour(1);
		
		enemy2.model.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy2.model.setAnimationSpeed(10f);
		enemy2.model.setPosition(mapX + 1700, mapY + 1160);
		enemy2.model.setScale(0.5f);
		enemy2.model.setBBScale(0.75f, 1f);
		enemy2.setBehaviour(0);
		
		enemy3.model.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy3.model.setAnimationSpeed(10f);
		enemy3.model.setPosition(mapX + 79 * this.engine.getTileSize(), mapY + 50 * this.engine.getTileSize());
		enemy3.model.setScale(0.5f);
		enemy3.model.setBBScale(0.75f, 1f);
		enemy3.setSpeed(5);
		enemy3.setBehaviour(1);
		
		boss.model.loadAnimationAndAdapt("./assets/textures/boss2.png", 2, 2);
		boss.model.setAnimationSpeed(10f);
		boss.model.setPosition(mapX + 97 * this.engine.getTileSize(), mapY + 9 * this.engine.getTileSize());
		boss.model.setScale(1.5f);
		boss.model.setBBScale(0.75f, 1f);
		
		
		DoubleJump powerup = new DoubleJump();
		powerup.model.setPosition((-this.worldSizeX / 2 + 49) * this.engine.getTileSize(), (-this.worldSizeY / 2 + 18 + 5) * this.engine.getTileSize());
		
		this.entityBuffer.add(player);
//		this.entityBuffer.add(pengu);
		this.entityBuffer.add(enemy);
		this.entityBuffer.add(enemy2);
		this.entityBuffer.add(enemy3);
		this.entityBuffer.add(powerup);
		this.entityBuffer.add(boss);
		
		for (int i = 0; i < 10; i++) {
			Coin coin = new Coin();
			coin.model.setPosition((-this.worldSizeX / 2) * this.engine.getTileSize() + ((31 + (i % 2)) * this.engine.getTileSize()),
								   (-this.worldSizeY / 2 + 5) * this.engine.getTileSize() + ((27 - (i / 2)) * this.engine.getTileSize()));
			this.entityBuffer.add(coin);
		}

		Coin coin = new Coin();
		coin.model.setPosition(mapX + 63 * this.engine.getTileSize(), mapY + 37 * this.engine.getTileSize());
		this.entityBuffer.add(coin);
		
		Coin coin1 = new Coin();
		coin1.model.setPosition(mapX + 63 * this.engine.getTileSize(), mapY + 45 * this.engine.getTileSize());
		this.entityBuffer.add(coin1);
		
		Coin coin2 = new Coin();
		coin2.model.setPosition(mapX + 67 * this.engine.getTileSize(), mapY + 41 * this.engine.getTileSize());
		this.entityBuffer.add(coin2);
		
		Coin coin3 = new Coin();
		coin3.model.setPosition(mapX + 93 * this.engine.getTileSize(), mapY + 55 * this.engine.getTileSize());
		this.entityBuffer.add(coin3);
		
		Coin coin4 = new Coin();
		coin4.model.setPosition(mapX + 91 * this.engine.getTileSize(), mapY + 48 * this.engine.getTileSize());
		this.entityBuffer.add(coin4);
		Coin coin5 = new Coin();
		coin5.model.setPosition(mapX + 91 * this.engine.getTileSize(), mapY + 42 * this.engine.getTileSize());
		this.entityBuffer.add(coin5);
		Coin coin6 = new Coin();
		coin6.model.setPosition(mapX + 91 * this.engine.getTileSize(), mapY + 36 * this.engine.getTileSize());
		this.entityBuffer.add(coin6);
		Coin coin7 = new Coin();
		coin7.model.setPosition(mapX + 90 * this.engine.getTileSize(), mapY + 30 * this.engine.getTileSize());
		this.entityBuffer.add(coin7);
		Coin coin8 = new Coin();
		coin8.model.setPosition(mapX + 89 * this.engine.getTileSize(), mapY + 24 * this.engine.getTileSize());
		this.entityBuffer.add(coin8);
		Coin coin9 = new Coin();
		coin9.model.setPosition(mapX + 88 * this.engine.getTileSize(), mapY + 18 * this.engine.getTileSize());
		this.entityBuffer.add(coin9);
		Coin coin10 = new Coin();
		coin10.model.setPosition(mapX + 86 * this.engine.getTileSize(), mapY + 14 * this.engine.getTileSize());
		this.entityBuffer.add(coin10);
		
		Coin coin11 = new Coin();
		coin11.model.setPosition(mapX + 97 * this.engine.getTileSize(), mapY + 15 * this.engine.getTileSize());
		this.entityBuffer.add(coin11);
		
		// loading them into the entityBuffer		
		
		
		this.mixer.uploadSong("./assets/sounds/normalTheme.ogg", true);
		this.mixer.uploadSong("./assets/sounds/winTheme.ogg", true);
		this.mixer.uploadSong("./assets/sounds/bossTheme2.ogg", true);
		
		this.mixer.playSong(0);
		
//		Sound sound = new Sound("./assets/sounds/normalTheme.ogg", true);
//		
//		sound.play();
	}
	
	// method for loading the starting tiles that compose the world
	private void loadStartingTiles() {
		// application position of the map (so everything can be in relation to this
		int mapX = -this.worldSizeX / 2;
		int mapY = -this.worldSizeY / 2;
		
		// create a background structure, this will not have hitboxes and will function as a background for the foreground tiles
		Structure background = new Structure();
		background.loadStructure("./assets/world/adventure pack/background.str");
		background.applyStructure(mapX, mapY, this.background);
		
		// additional structures to add to the existing foreground and background areas for ease of use
		Structure jumpPower = new Structure();
		jumpPower.loadStructure("./assets/world/adventure pack/jumpPower.str");
		jumpPower.applyStructure(mapX + 31, mapY + 25, this.world);
		jumpPower.applyStructure(mapX + 32, mapY + 25, this.world);
		
		Structure tree = new Structure();
		tree.loadStructureWithHitbox("./assets/world/adventure pack/trees.str", this.engine.getTileSize());
		tree.applyStructureWithHitbox(mapX + 3, mapY + 32, this.world, this.worldHitboxes);
		
		Structure treeBackground = new Structure();
		treeBackground.loadStructure("./assets/world/adventure pack/treesB.str");
		treeBackground.applyStructure(mapX + 3, mapY + 32, this.background);	
		
//		Structure backgroundTree = new Structure();
//		backgroundTree.loadStructure("./assets/world/adventure pack/backgroundTree.str");
//		backgroundTree.applyStructure(mapX + 58, mapY + 30, this.background);
		
		Structure platformTree = new Structure();
		platformTree.loadStructureWithHitbox("./assets/world/adventure pack/platformTree.str", this.engine.getTileSize());
		platformTree.applyStructureWithHitbox(mapX + 54, mapY + 36, this.world, this.worldHitboxes);
		
		Structure platformTreeB = new Structure();
		platformTreeB.loadStructure("./assets/world/adventure pack/platformTreeB.str");
		platformTreeB.applyStructure(mapX + 54, mapY + 36, this.background);
		
		// create a foreground structure, this will contain the tiles that can be collided with, and will function as the interactive part of the map
		Structure map = new Structure();
		map.loadStructureWithHitbox("./assets/world/adventure pack/map.str", this.engine.getTileSize());
		map.applyStructureWithHitbox(mapX, mapY, this.world, this.worldHitboxes);
		
		
		// world limit
//		for (int i = 0; i < this.worldSizeX; i++) {
//			for (int j = 0; j < this.worldSizeY; j++) {
//				if ((i == 0 || i == this.worldSizeX - 1) ||
//					(j == 0 || j == this.worldSizeY - 1)) {
//					world[i][j] = 181;
//				}
//			}
//		}
		
	}
	
	
	public void executeWin() {
		double current = System.nanoTime() / 1000000000L;
		double delta = current - this.winTimer;
		
		if (delta != 0) {
			int displayNumber = (int)delta;
			this.engine.ui.setWinTimer(10 - displayNumber);
			
			if (delta >= 10) {
				glfwSetWindowShouldClose(this.window, true);
			}
		}
		
//		if (delta >= 5) {
//			System.out.println("CLOSE");
//			glfwSetWindowShouldClose(this.window, true);
//		} else if (delta >= 4) {
//			System.out.println("1");
//			this.engine.ui.setWinTimer(1);
//		} else if (delta >= 3) {
//			System.out.println("2");
//			this.engine.ui.setWinTimer(2);
//		} else if (delta >= 2) {
//			System.out.println("3");
//			this.engine.ui.setWinTimer(3);
//		} else if (delta >= 1) {
//			System.out.println("4");
//			this.engine.ui.setWinTimer(4);
//		} else if (delta >= 0) {
//			System.out.println("5");
//			this.engine.ui.setWinTimer(5);
//		}
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
