package com.project.entities;

public interface IEntity extends IPhysicsBody {
	
	// setters and getters for the name
	public void setName(String name);
	public String getName();
	
	// wrapper for the updateAnimation method in Model
	public void updateAnimation();
	// wrapper for the loadTextureAndAdapt method in Model (and updating the Bounding Box accordingly)
	public void loadTextureAndAdapt(String filename);
	// wrapper for the loadAnimationAndAdapt method in Model (and updating the Bounding Box accordingly)
	public void loadAnimationAndAdapt(String filename, int steps, int animations);
	// wrapper for the setScale method in Model (and updating the Bounding Box accordingly)
	public void setScale(float scale);
	
	// getter for the ableToSuperJump variable (to check whether the entity can superjump or not)
	public boolean canSuperJump();
	
	// setters and getters for the toRemove variable (to check whether the entity needs to be removed or not)
	public boolean isToRemove();
	public void setToRemove();
}
