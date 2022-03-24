package com.test;

public class Hitbox {
	private float x0;
	private float y0;
	private float x2;
	private float y2;
	
	private float centerX;
	private float centerY;
	
	private float width;
	private float height;
	
	public Hitbox(float x0, float y0, float x2, float y2) {
		this.x0 = x0;
		this.y0 = y0;
		this.x2 = x2;
		this.y2 = y2;
		
		this.calculateCenter();
		this.calculateSize();
	}
	
	public void setX0(float x0) {
		this.x0 = x0;
		this.calculateCenter();
		this.calculateSize();
	}
	public void setY0(float y0) {
		this.y0 = y0;
		this.calculateCenter();
		this.calculateSize();
	}
	public void setX2(float x2) {
		this.x2 = x2;
		this.calculateCenter();
		this.calculateSize();
	}
	public void setY2(float y2) {
		this.y2 = y2;
		this.calculateCenter();
		this.calculateSize();
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
	public float getCenterX() {
		return(this.centerX);
	}
	public float getCenterY() {
		return(this.centerY);
	}
	public float getWidth() {
		return(this.width);
	}
	public float getHeight() {
		return(this.height);
	}
	
	
	private void calculateCenter() {
		this.centerX = (this.x0 + this.x2) / 2;
		this.centerY = (this.y0 + this.y2) / 2;
	}
	private void calculateSize() {
		this.width = Math.abs(x0 - x2);
		this.height = Math.abs(y0 - y2);
	}
}
