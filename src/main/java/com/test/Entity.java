package com.test;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class Entity {
	public Model model;
	protected String name;
	
	protected float mass;
	protected boolean canCollide;
	
	protected float forceX;
	protected float forceY;
	
	protected float accelerationX;
	protected float accelerationY;
	
	protected float velocityX;
	protected float velocityY;
	
	protected float airFriction;
	
	protected float gravity;
	
	protected float newPositionX;
	protected float newPositionY;
	
	protected boolean airborne;
	protected boolean stuck;
	
	protected boolean facingRight;
	
	protected boolean hitbox;
	

	public Entity() {
		this.model = new Model();
		this.canCollide = true;
		this.mass = 50.0f;
		this.forceX = 0;
		this.forceY = 0;
		
		this.accelerationX = 0;
		this.accelerationY = 0;
		
		this.velocityX = 0;
		this.velocityY = 0;
		
		this.airFriction = 5f;
		
		this.gravity = 1.2f;
		
		this.newPositionX = 0;
		this.newPositionY = 0;
		
		this.airborne = false;
		this.stuck = false;
		
		this.facingRight = true;
		
		this.hitbox = false;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return(this.name);
	}
	
	public void checkCollision(List<Entity> entityBuffer, List<Hitbox> worldHitboxes) {
		if (this.canCollide) {
			
			this.newPositionX = this.model.getX();
			this.newPositionY = this.model.getY();
			
			List<Vector4f> entityBB = this.model.calculateBoundingBox(this.hitbox);
			
			int contacts = 0;
			
			this.airborne = true;
			this.stuck = false;
			
			
			for (int i = 0; i < worldHitboxes.size(); i++) {				
				Vector2f objectBB0 = new Vector2f(worldHitboxes.get(i).getX0(), worldHitboxes.get(i).getY0());
				Vector2f objectBB2 = new Vector2f(worldHitboxes.get(i).getX2(), worldHitboxes.get(i).getY2());		
				
				if (((entityBB.get(0).x >= objectBB2.x && entityBB.get(0).x <  objectBB0.x) ||
					( entityBB.get(2).x >= objectBB2.x && entityBB.get(2).x <  objectBB0.x) ||
					( entityBB.get(2).x <= objectBB2.x && entityBB.get(0).x >= objectBB0.x)) &&
					((entityBB.get(0).y >= objectBB2.y && entityBB.get(0).y <  objectBB0.y) ||
					( entityBB.get(2).y >= objectBB2.y && entityBB.get(2).y <  objectBB0.y) ||
					( entityBB.get(2).y <= objectBB2.y && entityBB.get(0).y >= objectBB0.y))) {

					contacts++;
					
					List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox(this.hitbox);
					
					if ((prevEntityBB.get(0).x >= objectBB2.x && prevEntityBB.get(0).x < objectBB0.x) ||
						(prevEntityBB.get(2).x >= objectBB2.x && prevEntityBB.get(2).x < objectBB0.x) ||
						(prevEntityBB.get(2).x <= objectBB2.x && prevEntityBB.get(0).x >= objectBB0.x)) {
						if (prevEntityBB.get(2).y >= objectBB0.y) { // COMING FROM UP
							this.newPositionY = objectBB0.y + Math.abs(this.model.getY() - entityBB.get(2).y) + 0.1f;
							this.airborne = false;
						}
						else if (prevEntityBB.get(0).y <= objectBB2.y) { // COMING FROM DOWN
							this.newPositionY = objectBB2.y - Math.abs(entityBB.get(2).y - this.newPositionY) - 0.1f;
							this.stuck = true;				
						}
						this.velocityY = 0;
					}
					
					else if ((prevEntityBB.get(0).y >= objectBB2.y && prevEntityBB.get(0).y < objectBB0.y) ||
							 (prevEntityBB.get(2).y >= objectBB2.y && prevEntityBB.get(2).y < objectBB0.y) ||
							 (prevEntityBB.get(2).y <= objectBB2.y && prevEntityBB.get(0).y >= objectBB0.y)) {
						
						if (prevEntityBB.get(0).x <= objectBB2.x) { // COMING FROM LEFT
							this.newPositionX = objectBB2.x - Math.abs(this.model.getX() - entityBB.get(2).x) - 0.001f;
						}
						else if (prevEntityBB.get(2).x >= objectBB0.x) { // COMING FROM RIGHT
							this.newPositionX = objectBB0.x + Math.abs(this.model.getX() - entityBB.get(2).x) + 0.001f;
						}
						this.velocityX = 0;
					}
					
					else {
						this.newPositionX = this.model.getPrevX();
						this.newPositionY = this.model.getPrevY();
					}
				}
				
				if (objectBB2.y >= entityBB.get(0).y && objectBB2.y <= entityBB.get(0).y + 0.01f) {
					this.stuck = true;
				}
			}
			
			
			for (int i = 0; i < entityBuffer.size(); i++) {
				
				if (this != entityBuffer.get(i) && entityBuffer.get(i).canCollide()) {
					List<Vector4f> objectBB = entityBuffer.get(i).model.calculateBoundingBox(entityBuffer.get(i).getHitbox());
					
					if (((entityBB.get(0).x >= objectBB.get(2).x && entityBB.get(0).x <  objectBB.get(0).x) ||
						( entityBB.get(2).x >= objectBB.get(2).x && entityBB.get(2).x <  objectBB.get(0).x) ||
						( entityBB.get(2).x <= objectBB.get(2).x && entityBB.get(0).x >= objectBB.get(0).x)) &&
						((entityBB.get(0).y >= objectBB.get(2).y && entityBB.get(0).y <  objectBB.get(0).y) ||
						( entityBB.get(2).y >= objectBB.get(2).y && entityBB.get(2).y <  objectBB.get(0).y) ||
						( entityBB.get(2).y <= objectBB.get(2).y && entityBB.get(0).y >= objectBB.get(0).y))) {

						contacts++;
						
						List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox(this.hitbox);
						
						
						if ((prevEntityBB.get(0).x >= objectBB.get(2).x && prevEntityBB.get(0).x < objectBB.get(0).x) ||
							(prevEntityBB.get(2).x >= objectBB.get(2).x && prevEntityBB.get(2).x < objectBB.get(0).x) ||
							(prevEntityBB.get(2).x <= objectBB.get(2).x && prevEntityBB.get(0).x >= objectBB.get(0).x)) {
							if (prevEntityBB.get(2).y >= objectBB.get(0).y) { // COMING FROM UP
								this.newPositionY = objectBB.get(0).y + Math.abs(this.newPositionY - entityBB.get(2).y) + 0.001f;
								this.airborne = false;
							}
							else if (prevEntityBB.get(0).y <= objectBB.get(2).y) { // COMING FROM DOWN
								this.newPositionY = objectBB.get(2).y - Math.abs(entityBB.get(2).y - this.newPositionY) - 0.001f;
								this.stuck = true;				
							}
							this.velocityY = 0;
						}
						
						else if ((prevEntityBB.get(0).y >= objectBB.get(2).y && prevEntityBB.get(0).y < objectBB.get(0).y) ||
								 (prevEntityBB.get(2).y >= objectBB.get(2).y && prevEntityBB.get(2).y < objectBB.get(0).y) ||
								 (prevEntityBB.get(2).y <= objectBB.get(2).y && prevEntityBB.get(0).y >= objectBB.get(0).y)) {
							
							if (prevEntityBB.get(0).x <= objectBB.get(2).x) { // COMING FROM LEFT
								this.newPositionX = objectBB.get(2).x - Math.abs(this.model.getX() - entityBB.get(2).x) - 0.001f;
							}
							else if (prevEntityBB.get(2).x >= objectBB.get(0).x) { // COMING FROM RIGHT
								this.newPositionX = objectBB.get(0).x + Math.abs(this.model.getX() - entityBB.get(2).x) + 0.001f;
							}
							this.velocityX = 0;
						}
						
						else {
							this.newPositionX = this.model.getPrevX();
							this.newPositionY = this.model.getPrevY();
						}
					}
					
					if (objectBB.get(2).y >= entityBB.get(0).y && objectBB.get(2).y <= entityBB.get(0).y + 0.01f) {
						this.stuck = true;
					}
				}
			}
			
			if (contacts == 0) {
				this.newPositionX = this.model.getX();
				this.newPositionY = this.model.getY();
			}
		}
	}
	
	public void setCollision(boolean flag) {
		this.canCollide = flag;
	}
	
	public boolean canCollide() {
		return(this.canCollide);
	}
	
	public void setForce(float x, float y) {
		this.forceX = x;
		this.forceY = y;
	}
	
	public void setForcePolar(float r, float teta) {
		this.forceX = r * (float)Math.cos(teta);
		this.forceY = r * (float)Math.sin(teta);
	}
	
	public void applyForce(float x, float y) {
		this.forceX += x;
		this.forceY += y;
		
		if (this.forceX > 0) {
			this.facingRight = true;
		} else if (this.forceX < 0) {
			this.facingRight = false;
		}
	}
	
	public void applyForcePolar(float r, float teta) {
		this.forceX += r * (float)Math.cos(teta);
		this.forceY += r * (float)Math.sin(teta);
		
		if (this.forceX > 0) {
			this.facingRight = true;
		} else if (this.forceX < 0) {
			this.facingRight = false;
		}
	}
	
	public void calculatePosition() {
		float totalForceX = this.forceX + (this.airFriction * (-this.velocityX));
		float totalForceY = this.forceY + (this.airFriction * (-this.velocityY)) - (this.mass * this.gravity);
		
		this.accelerationX = totalForceX / this.mass;
		this.accelerationY = totalForceY / this.mass;
		
		this.velocityX += accelerationX;
		this.velocityY += accelerationY;

		float newPositionX = this.model.getX() + this.velocityX;
		float newPositionY = this.model.getY() + this.velocityY;

		this.model.setPosition(newPositionX, newPositionY);
		
		this.forceX = 0;
		this.forceY = 0;
	}
	
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	
	public void applyNewPosition() {
		this.model.setPosition(this.newPositionX, this.newPositionY);
	}
	
	public void setNewPosition(float x, float y) {
		this.newPositionX = x;
		this.newPositionY = y;
	}
	
	public void setVelocity(float x, float y) {
		this.velocityX = x;
		this.velocityY = y;
	}
	
	public float getVelocityX() {
		return(this.velocityX);
	}
	
	public float getVelocityY() {
		return(this.velocityY);
	}
	
	public void updateAnimation() {
		this.model.updateAnimation(this.facingRight);
	}
	
	public void setOrientation(boolean direction) {
		this.facingRight = direction;
	}
	
	public boolean isFacingRight() {
		return(this.facingRight);
	}

	public void setHitbox(boolean flag) {
		this.hitbox = flag;
	}
	
	public boolean getHitbox() {
		return(this.hitbox);
	}
	
	public boolean isAirborne() {
		return(this.airborne);
	}
	
}
