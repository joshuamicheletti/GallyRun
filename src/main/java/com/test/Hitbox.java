package com.test;

public class Hitbox {
	private float x0;
	private float y0;
	private float x2;
	private float y2;
	
	public Hitbox(float x0, float y0, float x2, float y2) {
		this.x0 = x0;
		this.y0 = y0;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public void setX0(float x0) {
		this.x0 = x0;
	}
	public void setY0(float y0) {
		this.y0 = y0;
	}
	public void setX2(float x2) {
		this.x2 = x2;
	}
	public void setY2(float y2) {
		this.y2 = y2;
	}
	
	public float getX0() {
		return(this.x0);
	}
	public float getY0() {
		return(this.y0);
	}
	public float getX2() {
		return(this.x2);
	}
	public float getY2() {
		return(this.y2);
	}
}
