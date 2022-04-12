package com.test;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	private Vector3f position;
	private Matrix4f projection;
	
	public Camera(int width, int height) {
		this.position = new Vector3f(0, 0, 0);
		this.projection = new Matrix4f().setOrtho2D(-width / 2, width / 2, -height / 2, height / 2);
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void move(Vector3f position) {
		this.position.add(position);
	}
	
	public Vector3f getPosition() {
		return(this.position);
	}
	
	public Matrix4f getProjection() {
		Matrix4f target = new Matrix4f();
		
		Matrix4f pos = new Matrix4f().setTranslation(this.position);
		
		this.projection.mul(pos, target);
		
		return(target);
	}
	
	public void setProjection(int width, int height) {
		this.projection = new Matrix4f().setOrtho2D(-width / 2, width / 2, -height / 2, height / 2);
	}
}
