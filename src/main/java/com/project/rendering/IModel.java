package com.project.rendering;

import java.util.List;

public interface IModel {
	// method to render the model
	public void render(ICamera camera, boolean debug);
	// setters for the translation, rotation and scale matrices
	public void setPosition(float x, float y);
	public void setRotation(float rotation);
	public void setScale(float scale);
	// getters for the translation, rotation and scale values
	public float getX();
	public float getY();
	public float getRotation();
	public float getScale();
	public float getScaleMul();
	// methods to load a texture / animation and adapt the model to its aspect ratio
	public void loadTextureAndAdapt(String filename);
	public void loadAnimationAndAdapt(String filename, int steps, int animations);
	// method to scale the model horizontally
	public void scaleHorizontal(float percentage);
	// method to update the animation of the model according to its own internal set animation
	public void updateAnimation(boolean direction);
	// method to set the current animation to be displayed
	public void setCurrentAnimation(int animation);
	// method to set the animation speed of the model (fps)
	public void setAnimationSpeed(float animationSpeed);
	// method for calculating the bounding box of the model
	public List<Float> calculateBoundingBox();
	// method to get the amount of frames in each animation
	public List<Integer> getFrames();
	// getters and setters for the opacity of the model
	public void setOpacity(float value);
	public float getOpacity();
}