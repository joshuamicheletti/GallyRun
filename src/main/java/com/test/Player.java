package com.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Player extends Entity{
	public Model allert;
	private Sound hitSfx;
	private Sound attackSfx;
	private Sound jumpSfx;
	private Sound superJumpSfx;
	
	private boolean idle;
	private boolean sleeping;
	private boolean goingToSleep;
	
	private int coins;
	
	private int additionalJumps;
	private boolean canDoubleJump;
	
	private boolean crouching;
	
	private int hp;
	
	private boolean damaged;
	
	private double damagedTimer;
	
	
	public Player() {
		this.allert = new Model();
		
		this.idle = false;
		this.sleeping = false;
		this.goingToSleep = false;
		
		this.coins = 0;
		this.additionalJumps = 0;
		this.canDoubleJump = false;
		this.crouching = false;
		
		this.hp = 200;
		
		this.damaged = false;
		
		this.hitSfx = new Sound("./assets/sounds/hit.ogg", false);
		this.attackSfx = new Sound("./assets/sounds/attack.ogg", false);
		this.jumpSfx = new Sound("./assets/sounds/jump.ogg", false);
		this.superJumpSfx = new Sound("./assets/sounds/superJump.ogg", false);
		this.attackSfx = new Sound("./assets/sounds/attack.ogg", false);
	}
	
	public void updateAnimation() {
		if (this.airborne) {
//			this.model.setAnimationSpeed(3f);
			
			float threshold = 5f;
			
			if (super.velocityY > threshold && this.model.getFrames().size() >= 3) {
				this.model.setCurrentAnimation(2);
			} else if (this.velocityY >= -threshold && this.velocityY <= threshold  && this.model.getFrames().size() >= 4) {
				this.model.setCurrentAnimation(3);
			} else if (this.velocityY < -threshold  && this.model.getFrames().size() >= 5) {
				this.model.setCurrentAnimation(4);
			}
		} else {
//			this.model.setAnimationSpeed(3f);
			
			float threshold = 0.02f;
	
			if (this.velocityX < threshold && this.velocityX > -threshold) {
				if (this.idle) {
					this.model.setAnimationSpeed(1f);
					this.model.setCurrentAnimation(5);
				} else if (this.goingToSleep) {
					this.model.setAnimationSpeed(1f);
					this.model.setCurrentAnimation(6);
				} else if (this.sleeping) {
					this.model.setAnimationSpeed(1f);
					this.model.setCurrentAnimation(7);
				} else {
					if (this.crouching) {
						this.model.setCurrentAnimation(8);
					} else {
						this.model.setCurrentAnimation(0);
					}
				}
		
			} else if (this.model.getFrames().size() >= 1) {
				this.model.setAnimationSpeed(3f);
				if (this.crouching) {
					this.model.setCurrentAnimation(9);
				} else {
					this.model.setCurrentAnimation(1);
				}
			}
		}
		
		this.model.updateAnimation(this.facingRight);
		this.allert.updateAnimation(true);
	}
	
	public void applyNewPosition() {
		this.model.setPosition(this.newPositionX, this.newPositionY);
		
		if (this.facingRight) {
			this.allert.setPosition(this.newPositionX + this.model.getScaleMul() / 2 + this.model.getScaleMul() / 20,
									this.newPositionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		} else {
			this.allert.setPosition(this.newPositionX - this.model.getScaleMul() / 2 - this.model.getScaleMul() / 20,
									this.newPositionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		}
	}
	
	public void rollbackPosition(float x, float y) {
		this.model.rollbackPosition(x, y);
	}
	
	public void setIdle(boolean idle) {
		this.idle = idle;
		this.sleeping = false;
		this.goingToSleep = false;
	}
	
	public void setSleep(boolean sleep) {
		this.sleeping = sleep;
		this.idle = false;
		this.goingToSleep = false;
		
		if (this.sleeping) {
			this.allert.setCurrentAnimation(1);
		} else {
			this.allert.setCurrentAnimation(0);
		}
	}
	
	public void setGoingToSleep(boolean goingToSleep) {
		this.goingToSleep = goingToSleep;
		this.idle = false;
		this.sleeping = false;
	}
	
	public void setScale(float scale) {
		this.model.setScale(scale);
		this.allert.setScale(scale / 2);
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
						this.refreshJump();
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

						if (entityBuffer.get(i) instanceof Collectible) {
							Collectible collectible = (Collectible)entityBuffer.get(i);
							
							collectible.applyEffect(this);
							
							if (!(collectible instanceof Portal)) {
								entityBuffer.remove(i);
								i--;
							}
						}			
						else {
							List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox(this.hitbox);
							
							if (prevEntityBB.get(0).x < objectBB.get(2).x) { // LEFT
								this.newPositionX = objectBB.get(2).x - (sizeX / 2) - 0.5f;
								this.velocityX = 0;
								
								if (entityBuffer.get(i) instanceof Enemy) {
									Enemy enemy = (Enemy)entityBuffer.get(i);
									
									this.doDamage(enemy.getDamage());
								}
								
							} else if (prevEntityBB.get(2).x > objectBB.get(0).x) { // RIGHT
								this.newPositionX = objectBB.get(0).x + (sizeX / 2) + 0.5f;
								this.velocityX = 0;
								if (entityBuffer.get(i) instanceof Enemy) {
									Enemy enemy = (Enemy)entityBuffer.get(i);
									
									this.doDamage(enemy.getDamage());
								}
								
							} else if (prevEntityBB.get(2).y > objectBB.get(0).y) { // TOP
								if (entityBuffer.get(i) instanceof Enemy) {
									if (entityBuffer.get(i) instanceof Boss) {
										Boss boss = (Boss)entityBuffer.get(i);
										boss.doDamage();
									} else {
										entityBuffer.remove(i);
									}
									this.unconditionalJump();
								} else {
									this.newPositionY = objectBB.get(0).y + (sizeY / 2) + 0.5f;
									this.velocityY = 0;
									this.airborne = false;
									this.refreshJump();
								}
								
								
							} else if (prevEntityBB.get(0).y < objectBB.get(2).y) { // BOTTOM
								this.newPositionY = objectBB.get(2).y - (sizeY / 2) - 0.5f;
								this.velocityY = 0;
								
								if (entityBuffer.get(i) instanceof Enemy) {
									Enemy enemy = (Enemy)entityBuffer.get(i);
									
									this.doDamage(enemy.getDamage());
								}
							}
							
							this.model.rollbackPosition(this.newPositionX, this.newPositionY);
							
							entityBB = this.model.calculateBoundingBox(this.hitbox);
						}
					}
				}
			}
		}
	}

	public void calculateState() {
		if (this.damaged) {
			double currentTime = System.nanoTime() / (double)1000000000L;
			
			double delta = currentTime - damagedTimer;
			
			if (delta >= 1) {
				this.damaged = false;
				this.model.setOpacity(1);
			}
		}
	}
	
	public boolean getDamagedState() {
		return(this.damaged);
	}
	
	
	public int getHP() {
		return(this.hp);
	}
	
	public void doDamage(int damage) {
		if (!this.damaged) {
			this.hp -= damage;
			this.damaged = true;
			this.damagedTimer = System.nanoTime() / (double)1000000000L;
			this.model.setOpacity(0.7f);
			
			this.hitSfx.play();
			
//			this.canCollideEntities = false;
			
			if (this.hp <= 0) {
				this.model.setPosition(-2476, -64);
				this.setNewPosition(-2476, -64);
				this.hp = 200;
			}
		}
	}
	
	public void addCoin() {
		this.coins++;
	}
	
	public int getCoins() {
		return(this.coins);
	}
	
	public void enableDoubleJump() {
		this.canDoubleJump = true;
	}
	
	public void refreshJump() {
		if (this.canDoubleJump) {
			this.additionalJumps = 1;
		}
	}
	
	public boolean canJump() {
		return((!this.airborne && !this.stuck) || additionalJumps != 0);
	}
	
	public void jump() {
		this.jumpSfx.playRaw();
		this.setVelocity(this.getVelocityX(), 0);
		
		if (this.airborne) {
			this.applyForce(0, 1400);
			
			this.additionalJumps--;
			
			if (this.additionalJumps < 0) {
				this.additionalJumps = 0;
			}
			
		} else {
			this.applyForce(0, 1400);
		}
		
	}
	
	public void unconditionalJump() {
		this.attackSfx.playRaw();
		this.setVelocity(this.getVelocityX(), 0);
		
		this.applyForce(0, 1400);
	}
	
	public void superJump() {
		this.superJumpSfx.playRaw();
		this.applyForce(0, 4000);
	}
	
	public void setCrouching(boolean flag) {
		this.crouching = flag;
	}
	
	public void facingRight(boolean flag) {
		this.facingRight = flag;
	}
	
	public boolean canDoubleJump() {
		return(this.canDoubleJump);
	}
}
