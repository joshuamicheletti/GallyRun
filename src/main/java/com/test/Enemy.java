package com.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Enemy extends Entity {
	
	protected boolean movingRight;
	protected Player player;
	protected int damage;
	protected int behaviour;
	protected float speed;
	
	public Enemy(Player player) {
		this.movingRight = false;
		this.player = player;
		this.damage = 25;
		this.behaviour = 0;
		this.speed = 10f;
	}
	
	public void control() {
		if (behaviour == 0) {
			if (this.movingRight) {
				this.applyForce(this.speed, 0f);
			} else {
				this.applyForce(-this.speed, 0f);
			}
		} else if (behaviour == 1) {
			if (this.model.getX() < this.player.model.getX()) {
				this.applyForce(this.speed, 0f);
			} else {
				this.applyForce(-this.speed, 0f);
			}
		}	
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
						this.newPositionX = objectBB2.x - (sizeX / 2) - 0.5f;
						this.velocityX = 0;
						
						if (this.movingRight == true) {
							this.movingRight = false;
						}
					} else if (prevEntityBB.get(2).x > objectBB0.x) { // RIGHT
						this.newPositionX = objectBB0.x + (sizeX / 2) + 0.5f;
						this.velocityX = 0;
						
						if (this.movingRight == false) {
							this.movingRight = true;
						}
					} else if (prevEntityBB.get(2).y > objectBB0.y) { // TOP
						if (worldHitboxes.get(i).getSpecialJump()) {
							this.ableToSuperJump = true;
						}
						this.newPositionY = objectBB0.y + (sizeY / 2) + 0.5f;
						this.velocityY = 0;
						this.airborne = false;
					} else if (prevEntityBB.get(0).y < objectBB2.y) { // BOTTOM
						this.newPositionY = objectBB2.y - (sizeY / 2) - 0.5f;
						this.velocityY = 0;
					}
					
					this.model.rollbackPosition(this.newPositionX, this.newPositionY);
					
					entityBB = this.model.calculateBoundingBox(this.hitbox);
				}
			}
			
			
			for (int i = 0; i < entityBuffer.size(); i++) {				
				if (entityBuffer.get(i) != this && entityBuffer.get(i).canCollide && !(entityBuffer.get(i) instanceof Collectible)) {
					
					List<Vector4f> objectBB = entityBuffer.get(i).model.calculateBoundingBox(false);
					
					if (entityBB.get(0).x > objectBB.get(2).x && // LEFT
						entityBB.get(2).x < objectBB.get(0).x && // RIGHT
						entityBB.get(2).y < objectBB.get(0).y && // TOP
						entityBB.get(0).y > objectBB.get(2).y) { // BOTTOM
						
						List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox(this.hitbox);
						
						if (prevEntityBB.get(0).x < objectBB.get(2).x) { // LEFT
							this.newPositionX = objectBB.get(2).x - (sizeX / 2) - 0.1f;
							this.velocityX = 0;
							
							if (entityBuffer.get(i) instanceof Player) {
								Player player = (Player)entityBuffer.get(i);
								player.doDamage(this.damage);
							}
						} else if (prevEntityBB.get(2).x > objectBB.get(0).x) { // RIGHT
							this.newPositionX = objectBB.get(0).x + (sizeX / 2) + 0.1f;
							this.velocityX = 0;
							
							if (entityBuffer.get(i) instanceof Player) {
								Player player = (Player)entityBuffer.get(i);
								player.doDamage(this.damage);
							}
						} else if (prevEntityBB.get(2).y > objectBB.get(0).y) { // TOP
							this.newPositionY = objectBB.get(0).y + (sizeY / 2) + 0.1f;
							this.velocityY = 0;
							this.airborne = false;
							
							if (entityBuffer.get(i) instanceof Player) {
								Player player = (Player)entityBuffer.get(i);
								player.doDamage(this.damage);
							}
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

	public void updateAnimation() {
		if (this.airborne && this.model.getFrames().size() >= 2) {			
			float threshold = 5f;
			
			if (super.velocityY > threshold && this.model.getFrames().size() >= 3) {
				this.model.setCurrentAnimation(2);
			} else if (this.velocityY >= -threshold && this.velocityY <= threshold  && this.model.getFrames().size() >= 4) {
				this.model.setCurrentAnimation(3);
			} else if (this.velocityY < -threshold  && this.model.getFrames().size() >= 5) {
				this.model.setCurrentAnimation(4);
			}
		} else {			
			float threshold = 0.02f;
	
			if (this.velocityX < threshold && this.velocityX > -threshold) {
				this.model.setCurrentAnimation(0);
			} else if (this.model.getFrames().size() >= 1) {
				this.model.setAnimationSpeed(3f);
				this.model.setCurrentAnimation(1);
			}
		}
		
		this.model.updateAnimation(this.facingRight);
	}
	
	
	public int getDamage() {
		return(this.damage);
	}
	
	public void setBehaviour(int behaviour) {
		this.behaviour = behaviour;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
