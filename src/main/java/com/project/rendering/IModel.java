package com.project.rendering;

import java.util.List;

public interface IModel {
	public void render(Camera camera, boolean debug);
	
	public void setPosition(float x, float y);
	public void setRotation(float rotation);
	public void setScale(float scale);
	
	public float getX();
	public float getY();
	public float getRotation();
	public float getScale();
	public float getScaleMul();
	
	public void loadTextureAndAdapt(String filename);
	public void loadAnimationAndAdapt(String filename, int steps, int animations);
	
	public void scaleHorizontal(float percentage);
	
	public void updateAnimation(boolean direction);
	
	public void setCurrentAnimation(int animation);
	
	public void setAnimationSpeed(float animationSpeed);
	
	public List<Float> calculateBoundingBox();
	
	public List<Integer> getFrames();
	
	public void setOpacity(float value);
	public float getOpacity();
}