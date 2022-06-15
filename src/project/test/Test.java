package project.test;

import static org.junit.Assert.*;

import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import project.entities.*;
import project.rendering.Engine;
import project.rendering.IEngine;
import project.rendering.IStructure;
import project.rendering.Structure;
import project.sound.IMixer;
import project.sound.Mixer;

// Class for testing the various features of the game
public class Test {
	// main entities used for testing
	private IEntity entity;
	private IEnemy enemy;
	private IPlayer player;
	
	// a hitbox to work as a floor to not let the entities fall through the world
	private Hitbox floor;
	
	// an entityBuffer to calculate physics and render
	private List<IEntity> entityBuffer;
	// a list of hitboxes to calculate collisions on
	private List<Hitbox> hitboxList;
	// two arrays to represent the tiles of the world and the background
	private int[][] world;
	private int[][] background;
	// an engine object to display the test results
	private IEngine engine;
	// a mixer object to test the songs being played
	private IMixer mixer;
	
	// reference to the GLFW and OpenAL objects to create and destroy them in every test
	private long window;
	private long audioContext;
	private long audioDevice;
	
	// parameter to set the speed of the simulation
	// increasing this number will allow the tester to see what is actually happening in every test
	// if you want to see what is happening, the recommended value is 32 (ms of delay between every frame)
	private int wait = 0;  // fast
//	private int wait = 32; // normal speed
	
	
	// method to setup the environment for testing
	private void setup() {
		// initialize glfw
		glfwInit();
		// create a window to display the tests
		this.window = glfwCreateWindow(1280, 720, "Test", 0, 0);
		// create OpenGL capabilities on the window
		glfwMakeContextCurrent(this.window);
		GL.createCapabilities();
		
		// initialize the audio device to test songs and sound effects
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		
		if (defaultDeviceName == null) { // if there isn't any available audio device
			System.out.println("No audio device found"); // notify it
		} else { // otherwise
			audioDevice = alcOpenDevice(defaultDeviceName); // open the default audio device and store it
			
			int[] attributes = {0};
			audioContext = alcCreateContext(audioDevice, attributes); // create an OpenAL audio context
			
			alcMakeContextCurrent(audioContext); // make the context current
			
			ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice); // create OpenAL audio capabilities for the device
			ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
			
			if (!alCapabilities.OpenAL10) { // check that the right version of OpenAL is supported by the audio device
				assert false : "Audio library not supported";
			}
		}
		
		// create an engine to show the test simulation
		this.engine = new Engine(this.window);
		this.engine.setDebug(true);
		this.engine.loadTiles("./assets/world/adventure pack/Assets.png", 25, 25);
		
		// initialize the world tiles (20 x 20)
		this.world = new int[20][20];
		this.background = new int[20][20];
		
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				this.world[i][j] = -1;
				this.background[i][j] = -1;
			}
		}
		
		// initialize the main entities and hitbox and load them in the respective buffers
		this.entity = new Entity();
		this.entity.setName("test entity");
		this.entity.loadTextureAndAdapt("./assets/textures/coinIcon.png");
		this.entity.setPosition(0, 0);
		this.entity.setCollision(false);
		
		this.player = new Player();
		this.player.setName("test player");
		this.player.loadAnimationAndAdapt("./assets/textures/gally5.png", 3, 10);
		this.player.setPosition(100, 100);
		this.player.getAllert().loadAnimationAndAdapt("./assets/textures/allert.png", 2, 3);
		
		this.enemy = new Enemy(this.player);
		this.enemy.setName("test enemy");
		this.enemy.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		this.enemy.getModel().setCurrentAnimation(1);
		this.enemy.setPosition(-100, 100);
		
		this.entityBuffer = new LinkedList<IEntity>();
		
		this.entityBuffer.add(this.entity);
		this.entityBuffer.add(this.player);
		this.entityBuffer.add(this.enemy);
		
		this.floor = new Hitbox();
		
		this.floor.setBBHeight(50);
		this.floor.setBBWidth(500);
		this.floor.setPosition(0, -100);
		
		this.hitboxList = new LinkedList<Hitbox>();
		this.hitboxList.add(floor);
		
		// initialize the mixer and load the songs
		this.mixer = new Mixer();
		this.mixer.uploadSong("./assets/sounds/normalTheme.ogg", true); // normal theme
		this.mixer.uploadSong("./assets/sounds/winTheme.ogg", true);    // winning theme
		this.mixer.uploadSong("./assets/sounds/bossTheme2.ogg", true);  // boss theme
	}
	
	// method for drawing the scene
	private void draw() {
		// update the animation of all the entities
		for (IEntity e : this.entityBuffer) {
			if (e != null) {
				e.updateAnimation();
			}
		}
		// render the entities and the world
		this.engine.render(entityBuffer, this.world, this.background);
		
		// add a delay to the rendering
		try {
			Thread.sleep(this.wait); // the number in parenthesis is the number of ms added to render each frame
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
	
	// method for closing the environment of the test
	private void shutdown() {		
		// destroy the audio context and device
		alcDestroyContext(this.audioContext);
		alcCloseDevice(this.audioDevice);
		// terminate the window
		glfwTerminate();
	}
	
	// method for calculating forces and collisions of entities
	private void calculatePhysics() {
		// cycle through the entity buffer
		for (int i = 0; i < this.entityBuffer.size(); i++) {
			// remove any entity that is set to be removed
			if (this.entityBuffer.get(i).isToRemove()) {
				this.entityBuffer.remove(this.entityBuffer.get(i));
				i--;
			} else {
				// calculate the position and collisions of every entity
				this.entityBuffer.get(i).calculatePosition();
				this.entityBuffer.get(i).checkCollision(new LinkedList<IPhysicsBody>(this.entityBuffer), false);
				this.entityBuffer.get(i).checkCollision(new LinkedList<IPhysicsBody>(this.hitboxList), true);
			}
		}
	}
	
	// method for calculating the distance between 2 entities
	private float calculateDistance(IEntity e1, IEntity e2) {
		return((float)Math.sqrt((Math.pow(e1.getX() - e2.getX(), 2) + (Math.pow(e1.getY() - e2.getY(), 2)))));
	}
	
	// test to check if the animations load properly and play accordingly
	@org.junit.Test
	public void animationTest() {
		setup();
		
		this.entityBuffer.remove(this.entity);
		this.entityBuffer.remove(this.enemy);
		
		// create a list containing the amount of frames per animation from the player
		List<Integer> testList = new LinkedList<Integer>();
		testList.addAll(Arrays.asList(1, 3, 1, 1, 1, 3, 2, 2, 1, 2));
		// check that the animation gets loaded properly by checking if all the frames for every animation get recognized
		assertTrue(this.player.getModel().getFrames().equals(testList));
		this.player.updateAnimation();
		
		// run a small animation to check that the animation and animation changes work as intended
		for (int i = 0; i < 150; i++) {
			this.player.applyForce(-20, 0);
			calculatePhysics();
			
			if (i == 100) {
				this.player.jump();
			}
			
			draw();
			
			// depending on the state of the player, check if the current animation matches the situation
			if (this.player.isAirborne()) {
				if (this.player.getVelocityY() > 5) { // rising
					assertTrue(this.player.getModel().getCurrentAnimation() == 2);
				} else if (this.player.getVelocityY() < -5) { // falling
					assertTrue(this.player.getModel().getCurrentAnimation() == 4);
				} else { // stationary in mid air
					assertTrue(this.player.getModel().getCurrentAnimation() == 3);
				}
			} else {
				if (this.player.getVelocityX() > 0.02 || this.player.getVelocityX() < -0.02) { // running
					assertTrue(this.player.getModel().getCurrentAnimation() == 1);
				} else if (this.player.getVelocityX() <= 0.02 && this.player.getVelocityX() >= -0.02) { // standing still
					assertTrue(this.player.getModel().getCurrentAnimation() == 0);
				}
			}
		}
		
		shutdown();
	}
	
	// test to check that the entities respond accordingly to the forces applied to them
	@org.junit.Test
	public void forceTest() {
		setup();
		
		this.entityBuffer.remove(this.enemy);
		this.entityBuffer.remove(this.player);
		
		draw();
		// apply a force to the entity
		this.entity.applyForce(1000, 2000);
		// calculate its new position
		this.entity.calculatePosition();
		draw();
		float positionX = this.entity.getX();
		float positionY = this.entity.getY();
		float velocityX = this.entity.getVelocityX();
		float velocityY = this.entity.getVelocityY();
		// the coordinates of the entity should have increased (moved to the top right)
		assertTrue(positionX > 0 && positionY > 0);
		assertTrue(velocityX > 0 && velocityY > 0);
		// recalculate the position without applying any force
		this.entity.calculatePosition();
		draw();
		// inertia should keep the object moving in the same trajectory
		assertTrue(this.entity.getX() > positionX && this.entity.getY() > positionY);
		// while air friction should reduce the entity's velocity
		assertTrue(this.entity.getVelocityX() < velocityX && this.entity.getVelocityY() < velocityY);
		// reset the vertical velocity to 0
		this.entity.setVelocity(this.entity.getVelocityX(), 0);
		// place the entity at the center again
		this.entity.setPosition(0, 0);
		// calculate the position twice to make gravity affect the entity and move it vertically
		this.entity.calculatePosition();
		draw();
		this.entity.calculatePosition();
		draw();
		// the object should be lower due to gravity
		assertTrue(this.entity.getY() < 0);
		
		shutdown();
	}
	
	// test that the collisions between entities and hitboxes work as intended
	@org.junit.Test
	public void collisionTest() {	
		setup();
		this.entityBuffer.remove(this.entity);
		
		// create a hitbox for the floor to make the entities not fall through
		Hitbox floor = new Hitbox();
		floor.setBBWidth(1000);
		floor.setBBHeight(50);
		floor.setPosition(0, 0);
		
		// make a 60 frames simulation of a player and an enemy colliding
		for (int i = 0; i < 60; i++) {
			// move the entities
			this.player.applyForce(-50, 0);
			this.enemy.applyForce(50, 0);
			calculatePhysics();
			this.draw();
		}
		
		// check that the player didn't go to the other side
		assertTrue(this.player.getX() > 0);
		// check that the enemy didn't go to the other side
		assertTrue(this.enemy.getX() < 0);
		// check that the player is to the right of the enemy
		assertTrue(this.enemy.getX() < this.player.getX());
		// check that the player is above the floor
		assertTrue(this.player.getY() > this.floor.getY());
		// check that the enemy is above the floor
		assertTrue(this.enemy.getY() > this.floor.getY());
		// check that the player lost hp
		assertTrue(this.player.getHP() < 200);
		
		shutdown();
	}
	
	// test that the 2 different implemented behaviors of the enemies work as intended
	@org.junit.Test
	public void enemyBehavior() {
		setup();
		
		this.entityBuffer.remove(this.entity);
		
		this.player.setPosition(0, 200);
		this.player.setGravity(0);
		
		// set the enemy's behavior to 0 (move left and right)
		this.enemy.setPosition(0, 0);
		this.enemy.setBehaviour(0);
		
		// create a new enemy with behavior 1 (follow the player)
		IEnemy enemy2 = new Enemy(this.player);
		enemy2.loadAnimationAndAdapt("./assets/textures/enemy.png", 2, 2);
		enemy2.setPosition(0, 0);
		enemy2.setBehaviour(1);
		
		this.entityBuffer.add(enemy2);
		
		// create 2 vertical walls to limit the movement of the enemies
		Hitbox leftWall = new Hitbox();
		leftWall.setBBHeight(200);
		leftWall.setBBWidth(50);
		leftWall.setPosition(-150, 0);
		
		Hitbox rightWall = new Hitbox();
		rightWall.setBBHeight(200);
		rightWall.setBBWidth(50);
		rightWall.setPosition(150, 0);
		
		this.hitboxList.add(rightWall);
		this.hitboxList.add(leftWall);
		
		for (int i = 0; i < 240; i++) {
			// control, calculate the position and check for collisions for the enemies
			// this is done without the method "calculatePhysics" to prevent collisions between enemies
			this.enemy.control();
			this.enemy.calculatePosition();
			this.enemy.checkCollision(new LinkedList<IPhysicsBody>(this.hitboxList), true);
			enemy2.control();
			enemy2.calculatePosition();
			enemy2.checkCollision(new LinkedList<IPhysicsBody>(this.hitboxList), true);

			
			if (i == 60) { // at frame 60
				// check that the enemy2 is still more or less at the position of the player (center)
				assertTrue(enemy2.getX() <= 10 && enemy2.getX() >= -10);
				// move the player to the left
				this.player.setPosition(-200, 200);
			} else if (i == 120) { // at frame 120
				// check that the enemy2 is on the left side of the screen (following the player)
				assertTrue(enemy2.getX() < 0 && enemy2.getX() > leftWall.getX());
				// move the player to the right
				this.player.setPosition(200, 200);
			}
			
			if (i == 20) { // at frame 20
				// check that the enemy is moving to the left
				assertTrue(this.enemy.getVelocityX() < 0);
			} else if (i == 60) { // at frame 60
				// check that the enemy is moving to the right
				assertTrue(this.enemy.getVelocityX() > 0);
			} else if (i == 150) { // at frame 150
				// check that the enemy is moving to the left
				assertTrue(this.enemy.getVelocityX() < 0);
			}
			
			draw();
		}
		// check that the enemy2 is on the right side of the screen (following the player)
		assertTrue(enemy2.getX() > 0 && enemy2.getX() < rightWall.getX());
		// check that the enemy is confined within the 2 walls
		assertTrue(enemy.getX() > leftWall.getX() && enemy.getX() < rightWall.getX());
		
		shutdown();
	}
	
	// test to check the functionality of collectibles such as coins, powerups and portals
	@org.junit.Test
	public void collectibleTest() {
		setup();
		this.entityBuffer.remove(this.enemy);
		this.entityBuffer.remove(this.entity);
		this.player.setPosition(300, 200);
		
		// create 3 new collectibles: a coin, a doublejump powerup and a portal
		Coin coin = new Coin();
		coin.setPosition(200, 30);
		this.entityBuffer.add(coin);
		
		DoubleJump powerup = new DoubleJump();
		powerup.setPosition(-200, 30);
		this.entityBuffer.add(powerup);
		
		Portal portal = new Portal();
		portal.setPosition(0, 30);
		portal.getModel().setCurrentAnimation(0);
		this.entityBuffer.add(portal);
		
		calculatePhysics();
		draw();
		
		// check that the player doesn't have any coins yet
		assertTrue(this.player.getCoins() == 0);
		// and that the coin has not been collected yet
		assertTrue(!coin.isToRemove());
		
		// move the player on top of the coin
		this.player.setPosition(200, 30);
		calculatePhysics();
		draw();
		
		// check that the player collected the coin
		assertTrue(this.player.getCoins() == 1);
		// and that the coin has been collected
		assertTrue(coin.isToRemove());
		
		// check that the player can't double jump yet
		assertTrue(this.player.canDoubleJump() == false);
		// and that the powerup has not been collected yet
		assertTrue(!powerup.isToRemove());
		
		// move the player on top of the powerup
		this.player.setPosition(-200, 30);
		calculatePhysics();
		draw();
		
		// check that the player can now double jump
		assertTrue(this.player.canDoubleJump());
		// and that the powerup has been collected
		assertTrue(powerup.isToRemove());
		
		// check that the portal hasn't detected a win yet
		assertTrue(portal.getWin() == false);
		// and that the player alert isn't showing anything yet
		assertTrue(this.player.getAllert().getCurrentAnimation() == 0);
		
		// move the player on top of the portal
		this.player.setPosition(0, 30);
		calculatePhysics();
		draw();
		
		// check that even though the player is colliding with the portal, the portal didn't detect a win yet
		assertTrue(portal.getWin() == false);
		// and check that the portal notified the player through its alert
		assertTrue(this.player.getAllert().getCurrentAnimation() == 2);
		
		// add 50 coins to the player
		for (int i = 0; i < 50; i++) {
			this.player.addCoin();
		}
		// check that the player now has 51 coins in total
		assertTrue(this.player.getCoins() == 51);
		
		calculatePhysics();
		draw();
		
		// check that now the portal detected a win, since the player collided with it with 50 coins or more
		assertTrue(portal.getWin());
	
		shutdown();	
	}
	
	// function to test the behavior of a player hitting an enemy
	@org.junit.Test
	public void attackTest() {
		setup();
		this.entityBuffer.remove(this.entity);
		this.player.setPosition(0, 400);
		this.enemy.setPosition(0, 100);
		
		// check that the enemy is alive
		assertTrue(!this.enemy.isToRemove());
		
		// run a small simulation
		for (int i = 0; i < 60; i++) {
			calculatePhysics();
			draw();
		}
		
		// check that the enemy has been removed from the entity buffer due to it being hit by the player
		assertFalse(this.entityBuffer.contains(this.enemy));
		// check that the player didn't take damage in the process
		assertTrue(this.player.getHP() == 200);
	
		shutdown();
	}
	
	// function to test all the functionalities of the boss
	@org.junit.Test
	public void testBoss() {
		setup();
		this.entityBuffer.remove(this.enemy);
		this.entityBuffer.remove(this.entity);
		this.player.setPosition(-500, 100);
		this.floor.setBBWidth(4000);
		this.floor.setPosition(0, -300);
		
		// create 2 walls to limit the movement of the boss
		Hitbox leftWall = new Hitbox();
		leftWall.setBBWidth(50);
		leftWall.setBBHeight(200);
		leftWall.setPosition(-600, 0);
		this.hitboxList.add(leftWall);
		
		Hitbox rightWall = new Hitbox();
		rightWall.setBBWidth(50);
		rightWall.setBBHeight(200);
		rightWall.setPosition(600, 0);
		this.hitboxList.add(rightWall);
		
		// create a boss to test
		Boss boss = new Boss(this.player, this.entityBuffer, this.mixer);
		boss.loadAnimationAndAdapt("./assets/textures/boss2.png", 2, 2);
		boss.setPosition(400, 200);
		this.entityBuffer.add(boss);
		
		// check that the boss is not triggered to begin with
		assertFalse(boss.isTriggered());
		
		boolean checkTrigger = false;
		float force = 30;
		
		// run a simulation
		for (int i = 0; i < 900; i++) {
			calculatePhysics();
			
			// control the player
			this.player.applyForce(force, 0);
			this.player.calculateState();
			
			// control the boss
			if (this.entityBuffer.contains(boss)) {
				boss.control();
			}
			
			draw();
			
			// slow down the simulation to match the invincible times of the boss after it gets hit
			if (i > 600) {
				try {
					Thread.sleep(32 - this.wait >= 0 ? 32 - this.wait : 0); // the number in parenthesis is the number of ms added to render each frame
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}
			
			// if the player is close enough to the boss
			if (calculateDistance(this.player, boss) <= 450 && !checkTrigger) {
				// check that the boss is now triggered
				assertTrue(boss.isTriggered());
				// check that the boss theme is now playing
				assertTrue(this.mixer.playingSong() == 2);
				checkTrigger = true;
			}
			
			if (i == 120) { // at frame 120
				// check that the player took damage from the boss
				assertTrue(this.player.getHP() == 200 - boss.getDamage());
				// move the player so the boss will follow it
				this.player.setPosition(600, 300);
				force = 0;
				this.player.setGravity(0);
			}
			
			if (i == 180) { // at frame 180
				// check that the boss actually followed the player by being on the right side of the screen
				assertTrue(boss.getX() > 0 && boss.getX() < rightWall.getX());
				// move the player to the left side
				this.player.setPosition(-600, 300);
			}
			
			if (i == 600) { // at frame 600
				// check that the boss moved to the left side to follow the player
				assertTrue(boss.getX() < 0 && boss.getX() > leftWall.getX());
				// move the player on top of the boss to hit it
				this.player.setPosition(boss.getX(), 300);
				this.player.setGravity(1.2f);
			}
			
			if (i == 660) { // at frame 660
				// check that the behavior of the boss changed after being hit and now is moving to the right
				assertTrue(boss.getVelocityX() > 0);
				// check that the boss didn't die from that hit
				assertTrue(this.entityBuffer.contains(boss));
				// place the player on top of the boss to hit it again
				this.player.setPosition(boss.getX(), 300);
				boss.setForce(0, 0);
				boss.setVelocity(0, 0);
			}
			
			if (i == 780) { // at frame 780
				// check that the boss didn't die yet
				assertTrue(this.entityBuffer.contains(boss));
				// check that the boss is still moving left and right, and now is moving left
				assertTrue(boss.getVelocityX() < 0);
				// place the player on top of the boss to hit it again
				this.player.setPosition(boss.getX(), 300);
				boss.setForce(0, 0);
				boss.setVelocity(0, 0);
			}
			
			if (i == 840) { // at frame 840
				// check that the boss death spawned new entities in the buffer (coins and portal)
				assertTrue(this.entityBuffer.size() > 2);
				// check that the boss died after being hit 3 times
				assertFalse(this.entityBuffer.contains(boss));
				// check that the victory song is now playing
				assertTrue(this.mixer.playingSong() == 1);
			}
		}
		
		shutdown();
	}

	// function to test the rendering of tiles and the creation and application of structures
	@org.junit.Test
	public void tilesTest() {
		setup();
		this.entityBuffer.remove(this.entity);
		this.entityBuffer.remove(this.enemy);
		this.player.setPosition(0, 0);
		this.floor.setPosition(0, -200);
		
		// create 2 new structures, 1 for the front of a tree
		IStructure tree = new Structure();
		tree.loadStructure("./assets/world/adventure pack/tree.str", this.engine.getTileSize());
		tree.applyStructure(-6, -3, world, hitboxList);
		// and one for the back of a tree
		IStructure treeBackground = new Structure();
		treeBackground.loadStructure("./assets/world/adventure pack/treeB.str", this.engine.getTileSize());
		treeBackground.applyStructure(-6, -3, background, hitboxList);
		
		// check that the application of the structures automatically generated hitboxes to match their geometries
		assertTrue(this.hitboxList.size() > 1);
		// check that the tiles where the tree was applied changed
		assertTrue(this.world[20 / 2 - 6][20 / 2 + 3] != -1);
		
		// run a test where the player moves against the tree structure
		for (int i = 0; i < 60; i++) {
			this.player.applyForce(-30, 0);
			calculatePhysics();
			draw();
		}
		// check that it didn't go through it but was blocked by its hitboxes
		assertTrue(this.player.getX() > -2 * this.engine.getTileSize());
		
		shutdown();
	}

}
