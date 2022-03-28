package com.test;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	
	protected boolean canForcePosX;
	protected boolean canForcePosY;
	protected boolean canForceNegX;
	protected boolean canForceNegY;
	
	protected boolean ableToSuperJump;
	

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
		
		this.canForcePosX = true;
		this.canForcePosY = true;
		this.canForceNegX = true;
		this.canForceNegY = true;
		
		this.ableToSuperJump = false;
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
			
			float sizeX = Math.abs(entityBB.get(0).x - entityBB.get(2).x);
			float sizeY = Math.abs(entityBB.get(0).y - entityBB.get(2).y);
			
			this.airborne = true;
			this.stuck = false;	
			
			Collections.sort(worldHitboxes, new Comparator<Hitbox>() {
				public int compare(Hitbox first, Hitbox second) {
					
					float dist1 = Math.abs(model.getX() - first.getCenterX());
					float dist2 = Math.abs(model.getX() - second.getCenterX());
					
					return(dist1 == dist2 ? 0 : dist1 < dist2 ? -1 : 1);
				}
			});
			
			this.ableToSuperJump = false;
			
			for (int i = 0; i < worldHitboxes.size(); i++) {				
				Vector2f objectBB0 = new Vector2f(worldHitboxes.get(i).getX0(), worldHitboxes.get(i).getY0());
				Vector2f objectBB2 = new Vector2f(worldHitboxes.get(i).getX2(), worldHitboxes.get(i).getY2());
				
				if (entityBB.get(0).x > objectBB2.x && // LEFT
					entityBB.get(2).x < objectBB0.x && // RIGHT
					entityBB.get(2).y < objectBB0.y && // TOP
					entityBB.get(0).y > objectBB2.y) { // BOTTOM

					List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox(this.hitbox);
					
					if (prevEntityBB.get(0).x < objectBB2.x) { // LEFT
						this.newPositionX = objectBB2.x - (sizeX / 2) - 0.1f;
						this.velocityX = 0;
					} else if (prevEntityBB.get(2).x > objectBB0.x) { // RIGHT
						this.newPositionX = objectBB0.x + (sizeX / 2) + 0.1f;
						this.velocityX = 0;
					} else if (prevEntityBB.get(2).y > objectBB0.y) { // TOP
						if (worldHitboxes.get(i).getSpecialJump()) {
							this.ableToSuperJump = true;
						}
						this.newPositionY = objectBB0.y + (sizeY / 2) + 0.1f;
						this.velocityY = 0;
						this.airborne = false;
					} else if (prevEntityBB.get(0).y < objectBB2.y) { // BOTTOM
						this.newPositionY = objectBB2.y - (sizeY / 2) - 0.1f;
						this.velocityY = 0;
					}
					
					this.model.rollbackPosition(this.newPositionX, this.newPositionY);
					
					entityBB = this.model.calculateBoundingBox(this.hitbox);
				}
			}
			
			
			for (int i = 0; i < entityBuffer.size(); i++) {				
				if (entityBuffer.get(i) != this && entityBuffer.get(i).canCollide) {
					
					List<Vector4f> objectBB = entityBuffer.get(i).model.calculateBoundingBox(false);
					
					if (entityBB.get(0).x > objectBB.get(2).x && // LEFT
						entityBB.get(2).x < objectBB.get(0).x && // RIGHT
						entityBB.get(2).y < objectBB.get(0).y && // TOP
						entityBB.get(0).y > objectBB.get(2).y) { // BOTTOM

						if (this instanceof Player && entityBuffer.get(i) instanceof Coin) {
							entityBuffer.remove(i);
							i--;
						} else {
							List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox(this.hitbox);
							
							if (prevEntityBB.get(0).x < objectBB.get(2).x) { // LEFT
								this.newPositionX = objectBB.get(2).x - (sizeX / 2) - 0.1f;
								this.velocityX = 0;
							} else if (prevEntityBB.get(2).x > objectBB.get(0).x) { // RIGHT
								this.newPositionX = objectBB.get(0).x + (sizeX / 2) + 0.1f;
								this.velocityX = 0;
							} else if (prevEntityBB.get(2).y > objectBB.get(0).y) { // TOP
								this.newPositionY = objectBB.get(0).y + (sizeY / 2) + 0.1f;
								this.velocityY = 0;
								this.airborne = false;
							} else if (prevEntityBB.get(0).y < objectBB.get(2).y) { // BOTTOM
								this.newPositionY = objectBB.get(2).y - (sizeY / 2) - 0.1f;
								this.velocityY = 0;
							}
							
							this.model.rollbackPosition(this.newPositionX, this.newPositionY);
							
							entityBB = this.model.calculateBoundingBox(this.hitbox);
						}
					}
				}
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
		if (x >= 0) {
			if (this.canForcePosX) {
				this.forceX += x;
				this.canForceNegX = true;
			}
		} else {
			if (this.canForceNegX) {
				this.forceX += x;
				this.canForcePosX = true;			}
		}
		
//		if (y >= 0) {
//			if (this.canForcePosY) {
//				this.forceY += y;
//				this.canForceNegY = true;
//			}
//		} else {
//			if (this.canForceNegY) {
//				this.forceY += y;
//				this.canForcePosY = true;
//			}
//		}
		
//		this.forceX += x;
		this.forceY += y;
		
		if (this.forceX > 0) {
			this.facingRight = true;
		} else if (this.forceX < 0) {
			this.facingRight = false;
		}
	}
	
	public void applyForcePolar(float r, float teta) {
		
		float x = r * (float)Math.cos(teta);
		float y = r * (float)Math.sin(teta);
		
		if (x >= 0) {
			if (this.canForcePosX) {
				this.forceX += x;
				this.canForceNegX = true;
			}
		} else {
			if (this.canForceNegX) {
				this.forceX += x;
				this.canForcePosX = true;
			}
		}
		
//		if (y >= 0) {
//			if (this.canForcePosY) {
//				this.forceY += y;
//				this.canForceNegY = true;
//			}
//		} else {
//			if (this.canForceNegY) {
//				this.forceY += y;
//				this.canForcePosY = true;
//			}
//		}
		
//		this.forceX += r * (float)Math.cos(teta);
		this.forceY += r * (float)Math.sin(teta);
		
//		if (this.forceX >= 0) {
//			this.facingRight = true;
//		} else if (this.forceX < 0) {
//			this.facingRight = false;
//		}
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
		
		if (this instanceof Player) {
			Player player = (Player)this;
			
			if (this.facingRight) {
				player.allert.setPosition(this.newPositionX + this.model.getScaleMul() / 2 + this.model.getScaleMul() / 20,
										  this.newPositionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
			} else {
				player.allert.setPosition(this.newPositionX - this.model.getScaleMul() / 2 - this.model.getScaleMul() / 20,
										  this.newPositionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
			}
		}
		
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
	
	public boolean canSuperJump() {
		return(this.ableToSuperJump);
	}
}
