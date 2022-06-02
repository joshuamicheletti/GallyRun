package com.project.rendering;

// class to implement a timer that keeps track of rendering times
public class Timer {
	
	private double frameCap;
	private double time;
	private double unprocessed;
	private double frameTime;
	private int frames;
	
	// Constructor
	public Timer() {
		this.frameCap = 1.0 / 60.0; // initialize the target framerate to 60 fps
		this.time = this.getTime(); // get the current time
		// set the variables for calculating the FPS to 0
		this.unprocessed = 0; 
		this.frameTime = 0;
		this.frames = 0;
	}
	
	// method for getting the current time
	public double getTime() {
		return((double)System.nanoTime()) / (double)1000000000L;
	}

	// method for setting the target FPS
	public void setFramerate(double fps) {
		if (fps == 0) {
			this.frameCap = 0;
		} else {
			this.frameCap = 1.0 / fps;
		}
	}
	
	// method for checking if enough time passed between 2 frames
	public boolean elapsed() {
		boolean answer = false;
		
		double time_2 = this.getTime();
		
		double passed = time_2 - this.time;
		
		this.unprocessed += passed;
		
		this.frameTime += passed;
		
		this.time = time_2;
		
		if (this.unprocessed >= this.frameCap) {
			answer = true;
			this.unprocessed -= this.frameCap;
		} else {
			answer = false;
		}
		
		return(answer);
	}
	
	// method for counting and printing FPS
	public void fps(double time) {
		if (this.frameTime >= 1.0) {
			this.frameTime = 0;
			System.out.println("FPS: " + frames + " | Frame ms: " + time + " | RFPS: " + Math.round((((double)1 / (time / (double)1000)) * (double)100))/ (double)100);
			this.frames = 0;
		}
		
		this.frames++;
	}
}
