package project.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import project.entities.IPlayer;
import project.rendering.IEngine;

// class that takes user inputs and controls the window and the player
public class Controller {
	// player object
	private IPlayer player;
	// engine object
	private IEngine engine;
	
	// movement speed for the player
	private float speed;
	
	// boolean variables to keep track of what buttons have been pressed
	private boolean pressedB;
	private boolean pressedSPACE;
	
	// timers to keep track of the elapsed time for idling and sleeping
	private double idleTime;
	private double sleepTime;
	
	// Constructor
	public Controller(IPlayer player, IEngine engine) {
		// get a reference for the player
		this.player = player;
		// get a reference for the engine
		this.engine = engine;
		
		// default movement speed of the player
		this.speed = 20f;
		
		// boolean variables for keeping track if the buttons B
		this.pressedB = false;
		// and SPACEBAR have been pressed
		this.pressedSPACE = false;
		
		// initialize the timers to the current time
		this.idleTime = System.nanoTime() / (double)1000000000L;
		this.sleepTime = System.nanoTime() / (double)1000000000L;
	}
	
	// method for polling events from the input devices 
	public void pollEvents(long window) {
		// poll the events from the window
		glfwPollEvents();
	
		// polar coordinates to apply a force to the player corresponding to the inputs
		float distance = 0;
		float direction = 0;
		
		// boolean variables to keep track of which keys are beeing pressed
		boolean A = false;
		boolean D = false;
		boolean S = false;
		
		// if the user is pressing A
		if (glfwGetKey(window, GLFW_KEY_A) == GL_TRUE) {
			A = true;
		}
		// if the user is pressing D
		else if (glfwGetKey(window, GLFW_KEY_D) == GL_TRUE) {
			D = true;
		}
		
		// if the user is pressing S
		if (glfwGetKey(window, GLFW_KEY_S) == GL_TRUE) {
			S = true;
		}
		
		// check if the user pressed B, works like a switch
		if (glfwGetKey(window, GLFW_KEY_B) == GL_TRUE && !this.pressedB) {
			// keep track of the button being pressed
			this.pressedB = true;
			
			// switch the debug state of the engine (show bounding boxes)
			if (this.engine.getDebug()) {
				this.engine.setDebug(false);
			} else {
				this.engine.setDebug(true);
			}
				
		}
		// if the B key is released
		else if (glfwGetKey(window, GLFW_KEY_B) != GL_TRUE) {
			// keep track of the button being released
			this.pressedB = false;
		}
		
		// if the user pressed the key ESCAPE
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE) {
			// close the window
			glfwSetWindowShouldClose(window, true);
		}
		
		// check if the user pressed SPACEBAR, works like a switch
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GL_TRUE && !this.pressedSPACE) {
			// keep track of the button being pressed
			this.pressedSPACE = true;
			
			// if the player can jump
			if (this.player.canJump()) {
				// if the player can super jump
				if (this.player.canSuperJump()) {
					// superjump
					this.player.superJump();
				} else {
					// otherwise do a normal jump
					this.player.jump();
				}	
			}
			
		}
		// if the SPACEBAR key is released
		else if (glfwGetKey(window, GLFW_KEY_SPACE) != GL_TRUE) {
			// keep track of the button being released
			this.pressedSPACE = false;
		}
		
		// calculate the direction to apply the movement force
		if (S && A) { // bottom left
			// set the angle of the force
												  //    _
			 									  //  /   \
			direction = (float)Math.PI * 5f / 4f; // |  o  |  
												  //  \/_ /
			distance = speed; // set the strength of the force
		}
		else if (S && D) { // bottom right
												  //    _
												  //  /   \
			direction = (float)Math.PI * 7f / 4f; // |  o  |
												  //  \ _\/
			distance = speed; // set the strength of the force
		} 
		else if (D) { // right
			  									  //    _
												  //  /   \
			direction = 0; 						  // |  o--|
												  //  \ _ /
			distance = speed; // set the strength of the force
		}
		else if (A) { // left
												  //	_
												  //  /   \
			direction = (float)Math.PI;			  // |--o  |
												  //  \ _ /
			distance = speed; // set the strength of the force
		}
		else if (S) { // down
												  //	_
												  //  /   \
			direction = (float)Math.PI * 3f / 2f; // |  o  |
												  //  \ | /
			distance = speed; // set the strength of the force
		}
		
		
		// check if the player should be crouching or not
		if (S) {
			this.player.setCrouching(true);
		} else {
			this.player.setCrouching(false);
		}
		
		// apply the force to the player
		this.player.applyForcePolar(distance, direction);
		
		
		// check if the player should go into idle and sleeping animations
		if (!A && !D && !S && !this.player.isAirborne()) {
			// check the current time
			double timeNow = System.nanoTime() / (double)1000000000L;
			// calculate the time elapsed for idling
			double delta = timeNow - this.idleTime;
			// calculate the time elapsed for sleeping
			double deltaSleep = timeNow - this.sleepTime;
			
			// if 5s passed without player inputs
			if (delta >= 5.0 && !player.isSleeping()) {
				// enable idling
				this.player.setIdle(true);
				// if 9s passed
				if (delta >= 8.5) {
					// disable idling
					this.player.setIdle(false);
					// restart the timer
					this.idleTime = System.nanoTime() / (double)1000000000L;
				}
			}
			
			// if 30s passed without player inputs
			if (deltaSleep >= 30.0 && !player.isSleeping()) {
				// enable the state of going to sleep
				this.player.setGoingToSleep(true);
				// if 32s passed without player inputs
				if (deltaSleep >= 31.8) {
					// enable sleeping
					this.player.setSleep(true);
				}
			}
		}
		// if the player moved or is falling
		else {
			// reset all the timers
			this.idleTime = System.nanoTime() / (double)1000000000L;
			this.sleepTime = System.nanoTime() / (double)1000000000L;
			// disable all the idling and sleeping states
			this.player.setIdle(false);
			this.player.setGoingToSleep(false);
			this.player.setSleep(false);
		}
	}
}
