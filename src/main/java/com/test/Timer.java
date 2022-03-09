package com.test;

public class Timer {
	
	private double frameCap;
	private double time;
	private double unprocessed;
	private double frameTime;
	private int frames;
	
	public Timer() {
		this.frameCap = 1.0 / 60.0;
		
		this.time = this.getTime();
		this.unprocessed = 0;
		this.frameTime = 0;
		this.frames = 0;
	}
	
	
	public double getTime() {
		return((double)System.nanoTime()) / (double)1000000000L;
	}

	
	public void setFramerate(double fps) {
		if (fps == 0) {
			this.frameCap = 0;
		} else {
			this.frameCap = 1.0 / fps;
		}
	}
	
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
	
	public void fps() {
		if (this.frameTime >= 1.0) {
			this.frameTime = 0;
			System.out.println("FPS: " + frames);
			this.frames = 0;
		}
		
		this.frames++;
	}
}
