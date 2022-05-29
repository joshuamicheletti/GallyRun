package com.project.entities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class PhysicsBody implements IPhysicsBody {
	// mass of the entity (used for physics)
	private float mass;
	private boolean canCollide;
	
	protected float positionX;
	protected float positionY;
	
	protected float newPositionX;
	protected float newPositionY;
	
	protected float previousPositionX;
	protected float previousPositionY;
	
	protected float bbW;
	protected float bbH;
	
	// current force in X and Y components
	protected float forceX;
	protected float forceY;
	
	// current velocity in X and Y components
	protected float velocityX;
	protected float velocityY;
	
	// current acceleration in X and Y components
	protected float accelerationX;
	protected float accelerationY;
	
	// air friction (used for physics)
	protected float airFriction;
	
	// gravity force applied to the entity (used for physics)
	protected float gravity;
	
	protected boolean facingRight;
	
	protected boolean airborne;
	
	public PhysicsBody() {
		this.positionX = 0;
		this.positionY = 0;
		
		this.previousPositionX = 0;
		this.previousPositionY = 0;
		
		this.newPositionX = 0;
		this.newPositionY = 0;
		
		this.bbW = 1;
		this.bbH = 1;
		
		// make collisions available
		this.canCollide = true;
		// set the mass to 50
		this.mass = 50.0f;
		
		// initialize the movement variables (force, velocity, acceleration)
		this.forceX = 0;
		this.forceY = 0;
		
		this.velocityX = 0;
		this.velocityY = 0;
		
		this.accelerationX = 0;
		this.accelerationY = 0;
		
		// set the air friction to 5
		this.airFriction = 5f;
		// gravity to 1.2
		this.gravity = 1.2f;
		// these values are arbitrary, they just fit and give the entities a good feel when moving
		
		this.facingRight = true;
		
		this.airborne = true;
	}
	
	public void checkCollision(List<IPhysicsBody> bodies, boolean sort) {
		// if this entity can collide with other entities and hitboxes
		if (this.canCollide) {
			// the body is airborne until it's proven that it's touching something beneath it
			this.airborne = true;
			
			// store the current position of the entity
			this.newPositionX = this.positionX;
			this.newPositionY = this.positionY;	
		
			// get the position of the vertices of the bounding box of the sprite			
			float entityBBPoint0X = this.positionX + this.bbW / 2;
			float entityBBPoint0Y = this.positionY + this.bbH / 2;
			float entityBBPoint2X = this.positionX - this.bbW / 2;
			float entityBBPoint2Y = this.positionY - this.bbH / 2;
			
			// sort the hitboxes that the entity is gonna collide with based on their distance from the entity
			// this prevents glitches when sliding across multiple hitboxes and getting stuck at the edges
			if (sort) {
				Collections.sort(bodies, new Comparator<IPhysicsBody>() {
					public int compare(IPhysicsBody first, IPhysicsBody second) {
						
						float dist1 = Math.abs(positionX - first.getX());
						float dist2 = Math.abs(positionX - second.getX());
						
						return(dist1 == dist2 ? 0 : dist1 < dist2 ? -1 : 1);
					}
				});
			}

			// scroll through all the hitboxes (ordered by distance to the entity)
			for (int i = 0; i < bodies.size(); i++) {
				// get the edge vertices of the bounding box of the current hitbox
				
				float objectBB0X = bodies.get(i).getX() + bodies.get(i).getBBWidth() / 2;
				float objectBB0Y = bodies.get(i).getY() + bodies.get(i).getBBHeight() / 2;
				float objectBB2X = bodies.get(i).getX() - bodies.get(i).getBBWidth() / 2;
				float objectBB2Y = bodies.get(i).getY() - bodies.get(i).getBBHeight() / 2;
				
				// if the entity collides with the current hitbox (meaning that its edge vertices are inside the range of the
				// hitbox edge vertices
				if (entityBBPoint0X > objectBB2X && // LEFT
					entityBBPoint2X < objectBB0X && // RIGHT
					entityBBPoint2Y < objectBB0Y && // TOP
					entityBBPoint0Y > objectBB2Y) { // BOTTOM
					
					if (this.collision(bodies.get(i))) {
						// calculate the bounding box of the entity in the frame before the collision
						float prevEntityBBPoint0X = this.previousPositionX + this.bbW / 2;
						float prevEntityBBPoint0Y = this.previousPositionY + this.bbH / 2;
						float prevEntityBBPoint2X = this.previousPositionX - this.bbW / 2;
						float prevEntityBBPoint2Y = this.previousPositionY - this.bbH / 2;
						// this is necessary to be able to tell from what direction the entity hit the hitbox from
						
						if (prevEntityBBPoint0X < objectBB2X) { // LEFT
							this.newPositionX = objectBB2X - (this.bbW / 2) - 0.1f; // place the entity to the left of the hitbox
							this.velocityX = 0;									     // negate the horizontal velocity
							this.hitLeft(bodies.get(i));
						} else if (prevEntityBBPoint2X > objectBB0X) { // RIGHT
							this.newPositionX = objectBB0X + (this.bbW / 2) + 0.1f; // place the entity to the right of the hitbox
							this.velocityX = 0;									     // negate the horizontal velocity
							this.hitRight(bodies.get(i));
						} else if (prevEntityBBPoint2Y > objectBB0Y) { // TOP
							this.newPositionY = objectBB0Y + (this.bbH / 2) + 0.1f; // place the entity to the top of the hitbox
							this.velocityY = 0;									     // negate the vertical velocity
							this.airborne = false;
							this.hitTop(bodies.get(i));
						} else if (prevEntityBBPoint0Y < objectBB2Y) { // BOTTOM
							this.newPositionY = objectBB2Y - (this.bbH / 2) - 0.1f; // place the entity to the bottom of the hitbox
							this.velocityY = 0;									     // negate the vertical velocity
							this.hitBottom(bodies.get(i));
						}
					}
					
					// rollback the position of the entity to the new calculated position (free from collisions)
					this.positionX = this.newPositionX;
					this.positionY = this.newPositionY;
					
					// calculate the new bounding box with the updated position for the next hitboxes checks (in case it now collides with
					// another hitbox, or in case it doesn't collide anymore with another hitbox)	
					entityBBPoint0X = this.positionX + this.bbW / 2;
					entityBBPoint0Y = this.positionY + this.bbH / 2;
					entityBBPoint2X = this.positionX - this.bbW / 2;
					entityBBPoint2Y = this.positionY - this.bbH / 2;
				}
			}
		}
	}
	
	public void setForce(float x, float y) {
		this.forceX = x;
		this.forceY = y;
		
		this.updateDirection();
	}
	
	public void setForcePolar(float r, float teta) {
		this.forceX = r * (float)Math.cos(teta);
		this.forceY = r * (float)Math.sin(teta);
		
		this.updateDirection();
	}
	
	public void applyForce(float x, float y) {
		this.forceX += x;
		this.forceY += y;
		
		this.updateDirection();
	}
	
	public void applyForcePolar(float r, float teta) {
		this.forceX += r * (float)Math.cos(teta);
		this.forceY += r * (float)Math.sin(teta);
		
		this.updateDirection();
	}
	
	private void updateDirection() {
		if (this.forceX > 0) {
			this.facingRight = true;
		} else if (this.forceX < 0) {
			this.facingRight = false;
		}
	}
	
	public void calculatePosition() {
		this.previousPositionX = this.positionX;
		this.previousPositionY = this.positionY;
		
		float totalForceX = this.forceX + (this.airFriction * (-this.velocityX));
		float totalForceY = this.forceY + (this.airFriction * (-this.velocityY)) - (this.mass * this.gravity);
		
		this.accelerationX = totalForceX / this.mass;
		this.accelerationY = totalForceY / this.mass;
		
		this.velocityX += accelerationX;
		this.velocityY += accelerationY;

		this.positionX += this.velocityX;
		this.positionY += this.velocityY;
		
		this.forceX = 0;
		this.forceY = 0;
	}
	
	public void setPosition(float x, float y) {
		this.positionX = x;
		this.positionY = y;
	}
	
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}
	
	
	protected boolean collision(Object target) {
		System.out.println("Collided with " + target);
		return(true);
	}
	protected void hitTop(Object target) {
		System.out.println("Collision from top");
	}
	protected void hitBottom(Object target) {
		System.out.println("Collision from bottom");
	}
	protected void hitLeft(Object target) {
		System.out.println("Collision from left");
	}
	protected void hitRight(Object target) {
		System.out.println("Collision from right");
	}
	
	
	
	public void setBBWidth(float width) {
		this.bbW = width;
	}
	public void setBBHeight(float height) {
		this.bbH = height;
	}
	public float getBBWidth() {
		return(this.bbW);
	}
	public float getBBHeight() {
		return(this.bbH);
	}
	
	public float getX() {
		return(this.positionX);
	}
	public float getY() {
		return(this.positionY);
	}
	
	public boolean canCollide() {
		return(this.canCollide);
	}
	
	public void setCollision(boolean collision) {
		this.canCollide = collision;
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
	
	public boolean isAirborne() {
		return(this.airborne);
	}
}
