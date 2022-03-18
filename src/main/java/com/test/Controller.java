package com.test;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import org.joml.Vector3f;

public class Controller {
	private Camera camera;
	private Entity player;
	private Engine engine;
	
	private float speed;
	
	private boolean pressedB;
	private boolean pressedSPACE;
	
	private double idleTime;
	private double sleepTime;
	
	
	public Controller(Camera camera, Entity player, Engine engine) {
		this.camera = camera;
		this.player = player;
		this.engine = engine;
		
		this.speed = 20f;
		
		this.pressedB = false;
		this.pressedSPACE = false;
		
		this.idleTime = System.nanoTime() / (double)1000000000L;
		this.sleepTime = System.nanoTime() / (double)1000000000L;
//		System.out.println("Time: " + this.time);
	}
	
	
	public void pollEvents(long window) {
		glfwPollEvents();
		
//		System.out.println("Delta: " + delta);
//		System.out.println("Time Now: " + timeNow);
//		System.out.println("Time: " + this.time);
		
//		this.time = timeNow;
		
		
		
//		System.nanoTime()) / (double)1000000000L
		
		Vector3f cameraMovement = new Vector3f(0, 0, 0);
		
//		float playerX = this.player.model.getX();
//		float playerY = this.player.model.getY();
		
		float playerX = 0;
		float playerY = 0;
		
		float distance = 0;
		float direction = 0;
		
		float rotation = 0;
		
		boolean A = false;
		boolean D = false;
		boolean W = false;
		boolean S = false;
		
		if (glfwGetKey(window, GLFW_KEY_A) == GL_TRUE) {
//			playerX -= this.speed;
			A = true;
		} else if (glfwGetKey(window, GLFW_KEY_D) == GL_TRUE) {
			D = true;
//			playerX += this.speed;
		}
		
		if (glfwGetKey(window, GLFW_KEY_W) == GL_TRUE) {
			W = true; 
//			playerY += this.speed;
		} else if (glfwGetKey(window, GLFW_KEY_S) == GL_TRUE) {
//			playerY -= this.speed;
			S = true;
		}
		
		if (glfwGetKey(window, GLFW_KEY_E) == GL_TRUE) {
			rotation += 0.03f;
		} else if (glfwGetKey(window, GLFW_KEY_Q) == GL_TRUE) {
			rotation -= 0.03f;
		}
		
		if (glfwGetKey(window, GLFW_KEY_LEFT) == GL_TRUE) {
			cameraMovement.x += this.speed;
		} else if (glfwGetKey(window, GLFW_KEY_RIGHT) == GL_TRUE) {
			cameraMovement.x -= this.speed;
		}
		
		if (glfwGetKey(window, GLFW_KEY_UP) == GL_TRUE) {
			cameraMovement.y -= this.speed;
		} else if (glfwGetKey(window, GLFW_KEY_DOWN) == GL_TRUE) {
			cameraMovement.y += this.speed;
		}
		
		if (glfwGetKey(window, GLFW_KEY_B) == GL_TRUE && !this.pressedB) {
			this.pressedB = true;
			
			if (this.engine.getDebug()) {
				this.engine.setDebug(false);
			} else {
				this.engine.setDebug(true);
			}
				
		} else if (glfwGetKey(window, GLFW_KEY_B) != GL_TRUE) {
			this.pressedB = false;
		}
		
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GL_TRUE) {
			glfwSetWindowShouldClose(window, true);
		}
		
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GL_TRUE && !this.pressedSPACE) {
			this.pressedSPACE = true;
			
//			if (this.player.canJump()) {
				this.player.setVelocity(this.player.getVelocityX(), 0);
				this.player.applyForce(0, 1400);
//			}
		} else if (glfwGetKey(window, GLFW_KEY_SPACE) != GL_TRUE) {
			this.pressedSPACE = false;
		}
		
		
		if (W && D) {
			direction = (float)Math.PI / 4f;
			distance = speed;
		} else if (W && A) {
			direction = (float)Math.PI * 3f / 4f;
			distance = speed;
		} else if (S && A) {
			direction = (float)Math.PI * 5f / 4f;
			distance = speed;
		} else if (S && D) {
			direction = (float)Math.PI * 7f / 4f;
			distance = speed;
		} else if (D) {
			direction = 0;
			distance = speed;
		} else if (W) {
			direction = (float)Math.PI / 2f;
			distance = speed;
		} else if (A) {
			direction = (float)Math.PI;
			distance = speed;
		} else if (S) {
			direction = (float)Math.PI * 3f / 2f;
			distance = speed;
		}
		
		
		this.player.applyForcePolar(distance, direction);
		this.player.model.setRotation(this.player.model.getRotation() + rotation);
		this.camera.move(cameraMovement);
		
		if (!A && !D && !this.player.isAirborne()) {
			double timeNow = System.nanoTime() / (double)1000000000L;
			
			double delta = timeNow - this.idleTime;
			double deltaSleep = timeNow - this.sleepTime;
			
			if (delta >= 5.0) {
				this.player.setIdle(true);
				if (delta >= 9.0) {
					this.player.setIdle(false);
					this.idleTime = System.nanoTime() / (double)1000000000L;
				}
			}
			
			if (deltaSleep >= 30.0) {
				this.player.setGoingToSleep(true);
				
				if (deltaSleep >= 31.0) {
					this.player.setSleep(true);
//					this.sleepTime = System.nanoTime() / (double)1000000000L;
				}
			}
		} else {
			this.idleTime = System.nanoTime() / (double)1000000000L;
			this.sleepTime = System.nanoTime() / (double)1000000000L;
			this.player.setIdle(false);
			this.player.setGoingToSleep(false);
			this.player.setSleep(false);
		}
		
		
	}
}
