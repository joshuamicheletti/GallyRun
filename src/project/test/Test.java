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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import project.entities.*;
import project.rendering.Engine;
import project.rendering.IEngine;

public class Test {
	
	private IEntity entity;
	private IEnemy enemy;
	private IPlayer player;
	
	private Hitbox floor;
	
	private List<IEntity> entityBuffer;
	private List<Hitbox> hitboxList;
	private IEngine engine;
	
	private long window;
	
	private void setup() {
		glfwInit();
		window = glfwCreateWindow(1280, 720, "Test", 0, 0);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		
		if (defaultDeviceName == null) { // if there isn't any available audio device
			System.out.println("No audio device found"); // notify it
		} else { // otherwise
			long audioDevice = alcOpenDevice(defaultDeviceName); // open the default audio device and store it
			
			int[] attributes = {0};
			long audioContext = alcCreateContext(audioDevice, attributes); // create an OpenAL audio context
			
			alcMakeContextCurrent(audioContext); // make the context current
			
			ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice); // create OpenAL audio capabilities for the device
			ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
			
			if (!alCapabilities.OpenAL10) { // check that the right version of OpenAL is supported by the audio device
				assert false : "Audio library not supported";
			}
		}
		
		// create a mockup engine to show the test simulation
		this.engine = new Engine(this.window);
		
		this.entity = new Entity();
		this.entity.setName("test entity");
		this.entity.loadTextureAndAdapt("./assets/textures/coinIcon.png");
		this.entity.setPosition(0, 0);
		this.entity.setCollision(false);
		
		this.player = new Player();
		this.player.setName("test player");
		this.player.loadAnimationAndAdapt("./assets/textures/gally5.png", 3, 10);
		this.player.setPosition(100, 100);
		
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
	}
	
	private void draw() {
		glClear(GL_COLOR_BUFFER_BIT);
		
		for (IEntity e : this.entityBuffer) {
			if (e != null) {
				e.updateAnimation();
				e.getModel().render(this.engine.getCamera(), true);
			}
		}
		
		glfwSwapBuffers(this.window);
		
		try {
			Thread.sleep(32); // the number in parentesis is the number of ms added to render each frame
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}
	
	private void shutdown() {		
		// terminate the window
		glfwTerminate();
	}
	
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
			this.player.calculatePosition();
			this.player.checkCollision(new LinkedList<IPhysicsBody>(this.hitboxList), false);
			
			if (i == 100) {
				this.player.jump();
			}
			
			draw();
		}
		
		shutdown();
	}
	
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
			// calculate the position and check the collisions for the player
			this.player.calculatePosition();
			this.player.checkCollision(new LinkedList<IPhysicsBody>(this.entityBuffer), false);
			this.player.checkCollision(new LinkedList<IPhysicsBody>(this.hitboxList), true);
			// calculate the position and check the collisions for the enemy
			this.enemy.calculatePosition();
			this.enemy.checkCollision(new LinkedList<IPhysicsBody>(this.entityBuffer), false);
			this.enemy.checkCollision(new LinkedList<IPhysicsBody>(this.hitboxList), true);
	
			this.draw();
		}
		
		// check that the player didn't go to the other side
		assertTrue(this.player.getX() > 0);
		// check that the player is to the right of the enemy
		assertTrue(this.enemy.getX() < this.player.getX());
		// check that the player is above the floor
		assertTrue(this.player.getY() > this.floor.getY());
		// check that the player lost hp
		assertTrue(this.player.getHP() < 200);
		
		shutdown();
	}
	
	@org.junit.Test
	public void enemyBehavior() {
		setup();
		
		this.entityBuffer.remove(this.entity);
		
		this.player.setPosition(0, 200);
		
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
				assertTrue(enemy.getVelocityX() < 0);
			} else if (i == 60) { // at frame 60
				// check that the enemy is moving to the right
				assertTrue(enemy.getVelocityX() > 0);
			} else if (i == 150) { // at frame 150
				// check that the enemy is moving to the left
				assertTrue(enemy.getVelocityX() < 0);
			}
			
			draw();
		}
		// check that the enemy2 is on the right side of the screen (following the player)
		assertTrue(enemy2.getX() > 0 && enemy2.getX() < rightWall.getX());
		// check that the enemy is confined within the 2 walls
		assertTrue(enemy.getX() > leftWall.getX() && enemy.getX() < rightWall.getX());
		
		shutdown();
	}
}