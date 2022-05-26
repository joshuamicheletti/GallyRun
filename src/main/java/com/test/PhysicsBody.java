package com.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class PhysicsBody {
	// mass of the entity (used for physics)
	protected float mass;
	protected boolean canCollide;
	
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
	}
	
	public void checkCollision(List<Entity> entityBuffer, List<Hitbox> worldHitboxes) {
		// if this entity can collide with other entities and hitboxes
		if (this.canCollide) {
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
			Collections.sort(worldHitboxes, new Comparator<Hitbox>() {
				public int compare(Hitbox first, Hitbox second) {
					
					float dist1 = Math.abs(positionX - first.getCenterX());
					float dist2 = Math.abs(positionX - second.getCenterX());
					
					return(dist1 == dist2 ? 0 : dist1 < dist2 ? -1 : 1);
				}
			});

			// scroll through all the hitboxes (ordered by distance to the entity)
			for (int i = 0; i < worldHitboxes.size(); i++) {
				// get the edge vertices of the bounding box of the current hitbox
				Vector2f objectBB0 = new Vector2f(worldHitboxes.get(i).getX0(), worldHitboxes.get(i).getY0());
				Vector2f objectBB2 = new Vector2f(worldHitboxes.get(i).getX2(), worldHitboxes.get(i).getY2());
				
				// if the entity collides with the current hitbox (meaning that its edge vertices are inside the range of the
				// hitbox edge vertices
				if (entityBBPoint0X > objectBB2.x && // LEFT
					entityBBPoint2X < objectBB0.x && // RIGHT
					entityBBPoint2Y < objectBB0.y && // TOP
					entityBBPoint0Y > objectBB2.y) { // BOTTOM
					
					// calculate the bounding box of the entity in the frame before the collision
					float prevEntityBBPoint0X = this.previousPositionX + this.bbW / 2;
					float prevEntityBBPoint0Y = this.previousPositionY + this.bbH / 2;
					float prevEntityBBPoint2X = this.previousPositionX - this.bbW / 2;
					float prevEntityBBPoint2Y = this.previousPositionY - this.bbH / 2;
					// this is necessary to be able to tell from what direction the entity hit the hitbox from
					
					if (prevEntityBBPoint0X < objectBB2.x) { // LEFT
						this.newPositionX = objectBB2.x - (this.bbW / 2) - 0.1f; // place the entity to the left of the hitbox
						this.velocityX = 0;									     // negate the horizontal velocity
						this.hitLeft(worldHitboxes.get(i));
					} else if (prevEntityBBPoint2X > objectBB0.x) { // RIGHT
						this.newPositionX = objectBB0.x + (this.bbW / 2) + 0.1f; // place the entity to the right of the hitbox
						this.velocityX = 0;									     // negate the horizontal velocity
						this.hitRight(worldHitboxes.get(i));
					} else if (prevEntityBBPoint2Y > objectBB0.y) { // TOP
						this.newPositionY = objectBB0.y + (this.bbH / 2) + 0.1f; // place the entity to the top of the hitbox
						this.velocityY = 0;									     // negate the vertical velocity
						this.hitTop(worldHitboxes.get(i));
					} else if (prevEntityBBPoint0Y < objectBB2.y) { // BOTTOM
						this.newPositionY = objectBB2.y - (this.bbH / 2) - 0.1f; // place the entity to the bottom of the hitbox
						this.velocityY = 0;									     // negate the vertical velocity
						this.hitBottom(worldHitboxes.get(i));
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
				
			for (int i = 0; i < entityBuffer.size(); i++) {				
				if (entityBuffer.get(i) != this && entityBuffer.get(i).canCollide) {
					Vector2f objectBB0 = new Vector2f(entityBuffer.get(i).getX() + entityBuffer.get(i).getBBWidth() / 2,
													  entityBuffer.get(i).getY() + entityBuffer.get(i).getBBHeight() / 2);
					
					Vector2f objectBB2 = new Vector2f(entityBuffer.get(i).getX() - entityBuffer.get(i).getBBWidth() / 2,
							  						  entityBuffer.get(i).getY() - entityBuffer.get(i).getBBHeight() / 2);
					
					if (entityBBPoint0X > objectBB2.x && // LEFT
						entityBBPoint2X < objectBB0.x && // RIGHT
						entityBBPoint2Y < objectBB0.y && // TOP
						entityBBPoint0Y > objectBB2.y) { // BOTTOM

						if (this.collision(entityBuffer.get(i))) {
							float prevEntityBBPoint0X = this.previousPositionX + this.bbW / 2;
							float prevEntityBBPoint0Y = this.previousPositionY + this.bbH / 2;
							float prevEntityBBPoint2X = this.previousPositionX - this.bbW / 2;
							float prevEntityBBPoint2Y = this.previousPositionY - this.bbH / 2;
							
							if (prevEntityBBPoint0X < objectBB2.x) { // LEFT
								this.newPositionX = objectBB2.x - (this.bbW / 2) - 0.1f;
								this.velocityX = 0;
								this.hitLeft(entityBuffer.get(i));
							} else if (prevEntityBBPoint2X > objectBB0.x) { // RIGHT
								this.newPositionX = objectBB0.x + (this.bbW / 2) + 0.1f;
								this.velocityX = 0;
								this.hitRight(entityBuffer.get(i));
							} else if (prevEntityBBPoint2Y > objectBB0.y) { // TOP
								this.newPositionY = objectBB0.y + (this.bbH / 2) + 0.1f;
								this.velocityY = 0;
								this.hitTop(entityBuffer.get(i));
							} else if (prevEntityBBPoint0Y < objectBB2.y) { // BOTTOM
								this.newPositionY = objectBB2.y - (this.bbH / 2) - 0.1f;
								this.velocityY = 0;
								this.hitBottom(entityBuffer.get(i));
							}
							
							this.positionX = this.newPositionX;
							this.positionY = this.newPositionY;
							
							this.previousPositionX = this.newPositionX;
							this.previousPositionY = this.newPositionY;
							
							entityBBPoint0X = this.positionX + this.bbW / 2;
							entityBBPoint0Y = this.positionY + this.bbH / 2;
							entityBBPoint2X = this.positionX - this.bbW / 2;
							entityBBPoint2Y = this.positionY - this.bbH / 2;
						}	
					}
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
	
	public void hitTop(Object target) {
		System.out.println("Collision from top");
	}
	public void hitBottom(Object target) {
		System.out.println("Collision from bottom");
	}
	public void hitLeft(Object target) {
		System.out.println("Collision from left");
	}
	public void hitRight(Object target) {
		System.out.println("Collision from right");
	}
	
	public boolean collision(Object target) {
		System.out.println("Collided with " + target);
		return(true);
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
}
