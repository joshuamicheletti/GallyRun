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
		
		this.speed = 3f;
		
		this.pressedB = false;
	}
	
	
	public void pollEvents(long window) {
		glfwPollEvents();
		
		Vector3f cameraMovement = new Vector3f(0, 0, 0);
		
		float playerX = this.player.model.getX();
		float playerY = this.player.model.getY();
		
		float rotation = 0;
		
		if (glfwGetKey(window, GLFW_KEY_A) == GL_TRUE) {
			playerX -= this.speed;
		} else if (glfwGetKey(window, GLFW_KEY_D) == GL_TRUE) {
			playerX += this.speed;
		}
		
		if (glfwGetKey(window, GLFW_KEY_W) == GL_TRUE) {
			playerY += this.speed;
		} else if (glfwGetKey(window, GLFW_KEY_S) == GL_TRUE) {
			playerY -= this.speed;
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
		
		this.player.model.setPosition(playerX, playerY);
		this.player.model.setRotation(this.player.model.getRotation() + rotation);
		
		this.camera.move(cameraMovement);
	}
}
