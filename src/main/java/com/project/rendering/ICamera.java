package com.project.rendering;

import org.joml.Matrix4f;

public interface ICamera {
	// methods for handling the camera position
	public void setPosition(float x, float y);
	public float getX();
	public float getY();
	
	// methods for getting and setting the camera's projection matrix
	public Matrix4f getProjection();
	public void setProjection(int width, int height);
}
