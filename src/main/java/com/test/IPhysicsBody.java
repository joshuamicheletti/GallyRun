package com.test;

import java.util.List;

public interface IPhysicsBody {

	public void checkCollision(List<IPhysicsBody> bodies, boolean sort);
	
	public void setPosition(float x, float y);
	
	public void calculatePosition();
	
	public float getX();
	public float getY();
	
	public void setGravity(float gravity);
	
	public void setForce(float x, float y);
	
	public void setForcePolar(float r, float teta);
	
	public void applyForce(float x, float y);
	
	public void applyForcePolar(float r, float teta);
	
	public void setBBHeight(float height);
	
	public void setBBWidth(float width);
	
	public float getBBHeight();
	public float getBBWidth();
	
	public void setCollision(boolean collide);
	
	public boolean canCollide();
	
	public void setVelocity(float x, float y);
	
	public float getVelocityX();
	
	public float getVelocityY();
	
	public boolean isAirborne();
}
