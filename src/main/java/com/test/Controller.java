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
	
	
	public Controller(Camera camera, Entity player, Engine engine) {
		this.camera = camera;
		this.player = player;
		this.engine = engine;
		
		this.speed = 50f;
		
		this.pressedB = false;
	}
	
	
	public void pollEvents(long window) {
		glfwPollEvents();
		
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
		
		
//		this.player.model.setPosition(playerX, playerY);
		this.player.applyForcePolar(distance, direction);
		this.player.model.setRotation(this.player.model.getRotation() + rotation);
		
		this.camera.move(cameraMovement);
	}
}
