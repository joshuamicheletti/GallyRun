package project.game;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import project.entities.*;
import project.rendering.*;
import project.sound.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;

// class that implements a game
public class Game {
	private List<IEntity> entityBuffer; // list containing all the entities in the game
	
	private int worldSizeX; // size of the world in tiles
	private int worldSizeY; // size of the world in tiles
	private int[][] world;		// matrix containing all the tiles in the world
	private int[][] background; // matrix containing all the background tiles in the world
	
	private List<Hitbox> worldHitboxes; // list containing all the hitboxes of the tiles
	
	private Timer timer; // timer for monitoring frametimes, framerate and for regulating tickrate
	private IEngine engine; // engine object to render entities and tiles
	private Controller controller; // controller object to listen to inputs for the controls
	private long window; // id of the window object
	
	private double winTimer; // variable to keep track of the time when the player wins
	
	private IMixer mixer; // object to store and play songs
	
	private long audioContext; // variables to handle OpenAL context and devices
	private long audioDevice;
	
	// Constructor
	public Game() {
		// initialize the window
		this.initWindow();
		
		// initialize the buffers
		this.entityBuffer = new LinkedList<IEntity>();
		this.worldHitboxes = new LinkedList<Hitbox>();
		
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
		// load the songs for the game
		this.loadSongs();
		
		// load the starting tiles
		this.loadStartingEntities();
		// load the starting entities
		this.loadStartingTiles();
		
		// initialize the controller that controls the player
		this.controller = new Controller((IPlayer)this.findByName("player", this.entityBuffer), this.engine);
		// initialize the winTimer to 0
		this.winTimer = 0;
	}
	
	// Game loop
	public void loop() {
		// run until the window should run
		while (!glfwWindowShouldClose(this.window)) {
			// makes the updates happen only after the tick time has passed (60 ticks per second, every 1/60s)
			while(this.timer.elapsed()) {
				// listen for inputs from the user
				this.controller.pollEvents(this.window); // CONTROLLER
				// update the entities accordingly
				this.updateEntities();					 // MODEL
				// allow the renderer to render on the window (this allows to lock the framerate to the tick rate, but not vice versa
				this.engine.enableRender();				 // VIEW
			}
			
			// if the engine can render to screen (once every 1/60s)
			if (this.engine.canRender()) {
				// render the background tiles, the foreground tiles and the entities (in that order) while keeping track of the time to do that
				double time = this.engine.render(this.entityBuffer, this.world, this.background);
				// print the frame time and fps info
				this.timer.fps(time);
			}
		}
		
		// destroy audio
		alcDestroyContext(this.audioContext);
		alcCloseDevice(this.audioDevice);
		
		// terminate the window
		glfwTerminate();
	}
	
	// Method for updating entities (position, animation)
	private void updateEntities() {
		// scroll through the entities
		for (int i = 0; i < this.entityBuffer.size(); i++) {
			IEntity current = this.entityBuffer.get(i);
			// remove any entity that is tagged to be removed
			if (current.isToRemove()) {
				entityBuffer.remove(i);
				i--;
			} else {
				// update the AI of the enemy
				if (current instanceof IEnemy) {
					IEnemy currentEnemy = (IEnemy)current;
					currentEnemy.control();
				}
				
				// update the timers on the player
				if (current instanceof IPlayer) {
					IPlayer player = (IPlayer)current;
					player.calculateState();
				}
				
				// check whether the end portal notices that the player won
				if (current instanceof Portal) {
					Portal portal = (Portal)current;
					
					if (portal.getWin()) {
						if (this.winTimer == 0) {
							this.winTimer = System.nanoTime() / 1000000000L;
						}
						this.executeWin();
					}
				}
				
				// calculate the new position of the entity (influenced by force, acceleration and speed)
				current.calculatePosition();
				
				// check for collisions against hitboxes or other entities and update the position to resolve the collision
				current.checkCollision(new LinkedList<IPhysicsBody>(this.entityBuffer), false);
				current.checkCollision(new LinkedList<IPhysicsBody>(this.worldHitboxes), true);
				
				// if we're updating the position of the player
				if (current instanceof IPlayer) {
					// move the camera according to the new position of the player
					this.engine.getCamera().setPosition(-current.getX(), -current.getY());
				}
				
				// update the animation of the entity
				current.updateAnimation();
			}
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
		// get the default audio device
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		
		if (defaultDeviceName == null) { // if there isn't any available audio device
			System.out.println("No audio device found"); // notify it
		} else { // otherwise
			this.audioDevice = alcOpenDevice(defaultDeviceName); // open the default audio device and store it
			
			int[] attributes = {0};
			this.audioContext = alcCreateContext(audioDevice, attributes); // create an OpenAL audio context
			
			alcMakeContextCurrent(this.audioContext); // make the context current
			
			ALCCapabilities alcCapabilities = ALC.createCapabilities(this.audioDevice); // create OpenAL audio capabilities for the device
			ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
			
			if (!alCapabilities.OpenAL10) { // check that the right version of OpenAL is supported by the audio device
				assert false : "Audio library not supported";
			}
		}
	}
	
	// callback function for updating the window resolution when it gets resized
	private GLFWFramebufferSizeCallback resizeWindow = new GLFWFramebufferSizeCallback() {
		public void invoke(long window, int width, int height){
			engine.setWindowSize(width, height);
		}
	};

	// method for loading the songs to be played in the game
	private void loadSongs() {
		// load the 3 songs
		this.mixer.uploadSong("./assets/sounds/normalTheme.ogg", true); // normal theme
		this.mixer.uploadSong("./assets/sounds/winTheme.ogg", true);    // winning theme
		this.mixer.uploadSong("./assets/sounds/bossTheme2.ogg", true);  // boss theme
		// play the normal overworld theme
		this.mixer.playSong(0);
	}
	
	// method for loading the entities when the game loads
	private void loadStartingEntities() {
		int mapX = -this.worldSizeX / 2 * this.engine.getTileSize();
		int mapY = (-this.worldSizeY / 2 + 5) * this.engine.getTileSize();
		
		// create, setup and load every entity in the game
		// Player
		IPlayer player = new Player();
		player.setName("player");
		player.loadAnimationAndAdapt("./assets/textures/gally5.png", 3, 10);
		player.setBBWidth(player.getBBWidth() * 0.75f);
		player.getModel().setAnimationSpeed(10f);
		player.setPosition(mapX + 1300, mapY + 27 * this.engine.getTileSize() + 25.1f);
		player.getAllert().loadAnimationAndAdapt("./assets/textures/allert.png", 2, 3);
		player.setSleep(true);
		this.entityBuffer.add(player);;

		// First enemy
		IEnemy enemy = new Enemy(player);
		enemy.setName("enemy");
		enemy.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy.setBBWidth(enemy.getBBWidth() * 0.75f);
		enemy.getModel().setAnimationSpeed(10f);
		enemy.setPosition(mapX + 61 * this.engine.getTileSize(), mapY + 2500);
		enemy.setBehaviour(1);
		this.entityBuffer.add(enemy);
		
		// Second enemy
		IEnemy enemy2 = new Enemy(player);
		enemy2.setName("enemy2");
		enemy2.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy2.setBBWidth(enemy2.getBBWidth() * 0.75f);
		enemy2.getModel().setAnimationSpeed(10f);
		enemy2.setPosition(mapX + 1700, mapY + 1160);
		enemy2.setBehaviour(0);
		this.entityBuffer.add(enemy2);
		
		// Third enemy
		IEnemy enemy3 = new Enemy(player);
		enemy3.setName("enemy3");
		enemy3.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy3.setBBWidth(enemy3.getBBWidth() * 0.75f);
		enemy3.getModel().setAnimationSpeed(10f);
		enemy3.setPosition(mapX + 79 * this.engine.getTileSize(), mapY + 50 * this.engine.getTileSize());
		enemy3.setSpeed(5);
		enemy3.setBehaviour(1);
		this.entityBuffer.add(enemy3);
		
		// Boss
		Boss boss = new Boss(player, this.entityBuffer, this.mixer);
		boss.setName("boss");
		boss.loadAnimationAndAdapt("./assets/textures/boss2.png", 2, 2);
		boss.setBBWidth(boss.getBBWidth() * 0.75f);
		boss.getModel().setAnimationSpeed(10f);
		boss.setPosition(mapX + 97 * this.engine.getTileSize(), mapY + 9 * this.engine.getTileSize());
		this.entityBuffer.add(boss);
		
		// Double jump powerup
		DoubleJump powerup = new DoubleJump();
		powerup.setPosition((-this.worldSizeX / 2 + 49) * this.engine.getTileSize(), (-this.worldSizeY / 2 + 18 + 5) * this.engine.getTileSize());
		this.entityBuffer.add(powerup);
		
		// Coins
		for (int i = 0; i < 10; i++) {
			Coin coin = new Coin();
			coin.setPosition((-this.worldSizeX / 2)     * this.engine.getTileSize() + ((31 + (i % 2)) * this.engine.getTileSize()),
							 (-this.worldSizeY / 2 + 5) * this.engine.getTileSize() + ((27 - (i / 2)) * this.engine.getTileSize()));
			this.entityBuffer.add(coin);
		}

		Coin coin = new Coin();
		coin.setPosition(mapX + 63 * this.engine.getTileSize(), mapY + 37 * this.engine.getTileSize());
		this.entityBuffer.add(coin);
		
		Coin coin1 = new Coin();
		coin1.setPosition(mapX + 63 * this.engine.getTileSize(), mapY + 45 * this.engine.getTileSize());
		this.entityBuffer.add(coin1);
		
		Coin coin2 = new Coin();
		coin2.setPosition(mapX + 67 * this.engine.getTileSize(), mapY + 41 * this.engine.getTileSize());
		this.entityBuffer.add(coin2);
	
		Coin coin3 = new Coin();
		coin3.setPosition(mapX + 93 * this.engine.getTileSize(), mapY + 55 * this.engine.getTileSize());
		this.entityBuffer.add(coin3);
		
		Coin coin4 = new Coin();
		coin4.setPosition(mapX + 91 * this.engine.getTileSize(), mapY + 48 * this.engine.getTileSize());
		this.entityBuffer.add(coin4);
		
		Coin coin5 = new Coin();
		coin5.setPosition(mapX + 91 * this.engine.getTileSize(), mapY + 42 * this.engine.getTileSize());
		this.entityBuffer.add(coin5);
		
		Coin coin6 = new Coin();
		coin6.setPosition(mapX + 91 * this.engine.getTileSize(), mapY + 36 * this.engine.getTileSize());
		this.entityBuffer.add(coin6);
		
		Coin coin7 = new Coin();
		coin7.setPosition(mapX + 90 * this.engine.getTileSize(), mapY + 30 * this.engine.getTileSize());
		this.entityBuffer.add(coin7);
		
		Coin coin8 = new Coin();
		coin8.setPosition(mapX + 89 * this.engine.getTileSize(), mapY + 24 * this.engine.getTileSize());
		this.entityBuffer.add(coin8);
		
		Coin coin9 = new Coin();
		coin9.setPosition(mapX + 88 * this.engine.getTileSize(), mapY + 18 * this.engine.getTileSize());
		this.entityBuffer.add(coin9);
		
		Coin coin10 = new Coin();
		coin10.setPosition(mapX + 86 * this.engine.getTileSize(), mapY + 14 * this.engine.getTileSize());
		this.entityBuffer.add(coin10);
		
		Coin coin11 = new Coin();
		coin11.setPosition(mapX + 97 * this.engine.getTileSize(), mapY + 15 * this.engine.getTileSize());
		this.entityBuffer.add(coin11);		
	}
	
	// method for loading the starting tiles that compose the world
	private void loadStartingTiles() {
		// application position of the map (so everything can be in relation to this
		int mapX = -this.worldSizeX / 2;
		int mapY = -this.worldSizeY / 2;
		
		// create a background structure, this will not have hitboxes and will function as a background for the foreground tiles
		IStructure background = new Structure();
		background.loadStructure("./assets/world/adventure pack/background.str", 0);
		background.applyStructure(mapX, mapY, this.background, null);
		
		// additional structures to add to the existing foreground and background areas for ease of use
		IStructure jumpPower = new Structure();
		jumpPower.loadStructure("./assets/world/adventure pack/jumpPower.str", 0);
		jumpPower.applyStructure(mapX + 31, mapY + 25, this.world, null);
		jumpPower.applyStructure(mapX + 32, mapY + 25, this.world, null);
		
		IStructure tree = new Structure();
		tree.loadStructure("./assets/world/adventure pack/trees.str", this.engine.getTileSize());
		tree.applyStructure(mapX + 3, mapY + 32, this.world, this.worldHitboxes);
		
		IStructure treeBackground = new Structure();
		treeBackground.loadStructure("./assets/world/adventure pack/treesB.str", 0);
		treeBackground.applyStructure(mapX + 3, mapY + 32, this.background, null);	
		
		IStructure platformTree = new Structure();
		platformTree.loadStructure("./assets/world/adventure pack/platformTree.str", this.engine.getTileSize());
		platformTree.applyStructure(mapX + 54, mapY + 36, this.world, this.worldHitboxes);
		
		IStructure platformTreeB = new Structure();
		platformTreeB.loadStructure("./assets/world/adventure pack/platformTreeB.str", 0);
		platformTreeB.applyStructure(mapX + 54, mapY + 36, this.background, null);
		
		// create a foreground structure, this will contain the tiles that can be collided with, and will function as the interactive part of the map
		IStructure map = new Structure();
		map.loadStructure("./assets/world/adventure pack/map.str", this.engine.getTileSize());
		map.applyStructure(mapX, mapY, this.world, this.worldHitboxes);
	}
	
	// method to trigger a win
	private void executeWin() {
		// check the current time
		double current = System.nanoTime() / 1000000000L;
		// check the time passed since the player won
		double delta = current - this.winTimer;
		// if the win triggered
		if (delta != 0) {
			// truncate the time to its integer value
			int displayNumber = (int)delta;
			// display the time left before the game closes
			this.engine.getUI().setWinTimer(10 - displayNumber);
			
			// if 10s passed since the player won
			if (delta >= 10) {
				// close the window
				glfwSetWindowShouldClose(this.window, true);
			}
		}
	}
	
	// method for finding an entity in the entityBuffer by name
	private IEntity findByName(String name, List<IEntity> entityBuffer) {
		for (int i = 0; i < entityBuffer.size(); i++) {
			if (entityBuffer.get(i).getName() == name) {
				return(entityBuffer.get(i));
			}
		}
		return(null);
	}
}
