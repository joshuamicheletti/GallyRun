package com.project.entities;

import java.util.List;

// interface for a body with physics (collisions, force, position, velocity, acceleration, gravity and friction)
public interface IPhysicsBody {

	// method for checking collisions with other bodies (which can be sorted to make edges interact in a smoother way)
	public void checkCollision(List<IPhysicsBody> bodies, boolean sort);
	// method for setting the position of the body
	public void setPosition(float x, float y);
	// method for calculating the position of the body according to its physics attributes
	public void calculatePosition();
	
	// getters for the position of the body (without recalculating it)
	public float getX();
	public float getY();
	
	// setters for physics parameters to later calculate the position of the body
	public void setGravity(float gravity); 				// gravity
	public void setVelocity(float x, float y);			// velocity	
	public void setForce(float x, float y);				// force (vector with x and y components)
	public void setForcePolar(float r, float teta);		// polar force (polar vector with r and teta components)
	
	// getters for physics parameters (velocity)
	public float getVelocityX();
	public float getVelocityY();
	
	// method for applying a force (x, y) components. this force is added to the force that is already applied to the body
	public void applyForce(float x, float y);
	// method for applying a polar force (r, teta) components. this force is added to the force that is already applied to the body
	public void applyForcePolar(float r, float teta);
	
	// setters for the width and height of the bounding box used for checking collisions
	public void setBBWidth(float width);
	public void setBBHeight(float height);
	
	// getters for the width and height of the bounding box used for checking collisions
	public float getBBWidth();
	public float getBBHeight();
	
	// setters and getters for the collision flag, used to check if a body can collide or not with other bodies
	public void setCollision(boolean collide);
	public boolean canCollide();
	
	// method for checking if the body is airborne (not colliding with anything beneath it)
	public boolean isAirborne();
}
