package com.test;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;


public class Engine {
	
	private long window;
	
	public Camera camera;
	
	private boolean canRender;
	
	private boolean debug;
	
	public Engine(long window) {
		
		this.window = window;
		
		glfwMakeContextCurrent(this.window);
		
		GL.createCapabilities();
		
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		this.camera = new Camera(640, 480);
		
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		glfwSwapInterval(0);
		
		this.canRender = false;
		
		this.debug = false;
	}
	
	
	public double render(List<Entity> entityBuffer) {
		double t1 = System.nanoTime();
		
		glClear(GL_COLOR_BUFFER_BIT);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		
		for (int i = 0; i < entityBuffer.size(); i++) {
			entityBuffer.get(i).model.render(this.camera, this.debug);
		}
		
		glfwSwapBuffers(this.window);
		
		this.canRender = false;
		
		double t2 = System.nanoTime();
		
		return(Math.round(((t2 - t1) / (double)1000000L) * (double)100) / (double)100);
	}
	
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
}
