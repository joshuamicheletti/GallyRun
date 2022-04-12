package com.test;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.IntBuffer;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

// Class for rendering models
public class Engine {
	// reference to the window id
	private long window;
	// window width
	private int w;
	// window height
	private int h;
	
	// camera object, used for calculating perspective
	public Camera camera;
	
	// object for rendering UI elements
	public UI ui;
	
	// boolean flag to verify that the engine can render
	private boolean canRender;
	// boolean flag to render bounding boxes of entities and tiles
	private boolean debug;
	
	// model containing the texture of the tileset used for rendering tiles
	private Model tileSet;
	// model containing the texture of the background (skybox)
	private Model sky;
	
	// number of columns in the tileset
	private int tileW;
	// number of rows in the tileset
	private int tileH;
	// size of a tile in px (width and height)
	private int tileSize;
	
	
	
	// Constructor
	public Engine(long window) {
		// get a reference for the window to render in
		this.window = window;
		
		// get the width and height of the window
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		glfwGetWindowSize(window, width, height);
		this.w = width.get(0);
		this.h = height.get(0);
		
		// set the OpenGL context as the window
		glfwMakeContextCurrent(this.window);
		
		// create OpenGL capabilities
		GL.createCapabilities();
		
		// enable rendering of 2D textures
		glEnable(GL_TEXTURE_2D);
		
		// enable alpha blending
		glEnable(GL_BLEND);
		// define the blending function
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		// create a new UI object
		this.ui = new UI(this.w, this.h);
		
		// create a new camera
		this.camera = new Camera(1280, 720);
		
		// set the clear color to grey
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		// disable build in vsync
		glfwSwapInterval(0);
		
		// load the skybox texture
		this.sky = new Model();
		this.sky.loadTextureAndAdapt("./assets/textures/blue.jpg");
		this.sky.setScale(10f);
		
		// initialize flags to false
		this.canRender = false;
		this.debug = false;
		
		// set the tile size (64px x 64px)
		this.tileSize = 64;
	}
	
	// method for loading the tileset into the engine
	public void loadTiles(String texture, int w, int h) {
		// create a new model
		this.tileSet = new Model();
		// load the specified texture (the tileset)
		this.tileSet.loadTileSet(texture);
		
		// store the tilemap columns and rows
		this.tileW = w;
		this.tileH = h;
		
		// adjust the scale to match the tile size (models are scaled by 256 by default)
		this.tileSet.setScale(this.tileSize / 256f);
	}
	
	// method for changing the window size properly, it gets called everytime the window is resized
	public void setWindowSize(int width, int height) {
		// update the width and height reference in the engine
		// without this update, it's possible to see in effect the camera frostum culling of the tiles
		this.w = width;
		this.h = height;
		
		// create a new projection matrix for the camera with the new width and height
		this.camera.setProjection(width, height);
		
		// update the reference for the UI to calculate the position of the UI elements
		this.ui.setWidth(this.w);
		this.ui.setHeight(this.h);
		
		// update the OpenGL framebuffer size to render to the new window
		glViewport(0, 0, width, height);
	}
	
	
	
	// method for rendering
	public double render(List<Entity> entityBuffer, int[][] world, int[][] background) {
		// check the time before rendering
		double t1 = System.nanoTime();
		
		// specify what part of the framebuffer to clear at every frame and clear it
		glClear(GL_COLOR_BUFFER_BIT);
		
		
		// fake load to test rendering times
		try {
			Thread.sleep(0); // the number in parentesis is the number of ms added to render each frame
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		
		// ----------------- SKYBOX --------------------
		
		// store the current camera position
		Vector3f cameraPosition = camera.getPosition();
		// place the camera at the center
		camera.setPosition(new Vector3f(0, 0, 0));
		// render the skybox
		this.sky.render(camera, false);
		// restore the previous camera position
		camera.setPosition(cameraPosition);
		// this process allows the sky to remain at the center of the camera, no matter how it moves
		
		
		// ------------------ TILES --------------------
		
		// render the background tiles
		this.renderTiles(background);
		// render the foreground tiles
		this.renderTiles(world);
		
		
		// ------------------ ENTITIES -----------------
		
		// variable to store the index of the player in the entityBuffer
		int playerIndex = -1;
		
		// render all the entities
		for (int i = 0; i < entityBuffer.size(); i++) {
			// render the model of the specified entity
			entityBuffer.get(i).model.render(this.camera, this.debug);
			
			// if the current entity is the player, render the allert box as well
			if (entityBuffer.get(i) instanceof Player) {
				// cast the Entity to Player
				Player player = (Player)entityBuffer.get(i);
				// render the allert box
				player.allert.render(this.camera, this.debug);
				// store the position of the player in the entityBuffer
				playerIndex = i;
			}
		}
		
		
		// ------------------ UI ----------------------
		
		// render the UI elements
		this.ui.renderUI(this.camera, (Player)entityBuffer.get(playerIndex));
		
		// swap buffers for the next render
		glfwSwapBuffers(this.window);
		
		// now that it rendered once, it waits for the next opportunity to render again
		this.canRender = false;
		
		//  check the current time
		double t2 = System.nanoTime();
		
		// return the elapsed time for rendering
		return(Math.round(((t2 - t1) / (double)1000000L) * (double)100) / (double)100);
	}

	// method for rendering tiles
	private void renderTiles(int[][] world) {
		// scroll through all the tiles
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				// if it's a valid tile to render
				if (world[i][j] >= 0) {
					// get the position of the tile in the world in pixels
					int positionX = i * this.tileSize - (this.tileSize * (world.length / 2));
					int positionY = j * this.tileSize - (this.tileSize * (world[0].length / 2));
				
					// check if the current tile is visible by the camera
					if (positionX - this.tileSize / 2 >= -this.camera.getPosition().x + this.w / 2 ||
						positionX + this.tileSize / 2 <= -this.camera.getPosition().x - this.w / 2 ||
						positionY - this.tileSize / 2 >= -this.camera.getPosition().y + this.h / 2 ||
						positionY + this.tileSize / 2 <= -this.camera.getPosition().y - this.h / 2) {
					}
					// render only if the tile is within the camera frostum
					else {
						// change the UV to display the correct tile in the tileset
						this.tileSet.changeTileUV(world[i][j], this.tileW, this.tileH);
						// set where to render the tile
						this.tileSet.setPosition(positionX, positionY);
						// render the tile
						this.tileSet.render(this.camera, this.debug);
					}					
				}
			}
		}
	}
	
	
	
	// SETTERS AND GETTERS
	public void enableRender() {
		this.canRender = true;
	}
	
	public boolean canRender() {
		return(this.canRender);
	}
	
	public Camera getCamera() {
		return(this.camera);
	}
	
	public boolean getDebug() {
		return(this.debug);
	}
	
	public void setDebug(boolean flag) {
		this.debug = flag;
	}
	
	public int getTileSize() {
		return(this.tileSize);
	}
	
	public void setTileSize(int size) {
		this.tileSize = size;
	}
}
