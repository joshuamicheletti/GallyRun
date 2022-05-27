package com.test;

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
	
	public void calculatePosition() {
		super.calculatePosition();
		
		if (this.facingRight) {
			this.allert.setPosition(this.positionX + this.model.getScaleMul() / 2 + this.model.getScaleMul() / 20,
									this.positionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		} else {
			this.allert.setPosition(this.positionX - this.model.getScaleMul() / 2 - this.model.getScaleMul() / 20,
							 		this.positionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		}
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
			this.setPosition(this.newPositionX + this.model.getScaleMul() / 2 + this.model.getScaleMul() / 20,
							 this.newPositionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		} else {
			this.setPosition(this.newPositionX - this.model.getScaleMul() / 2 - this.model.getScaleMul() / 20,
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
		super.setScale(scale);
		this.allert.setScale(scale / 2.5f);
	}

	public boolean collision(Object target) {
		if (target instanceof Collectible) {
			Collectible collectible = (Collectible)target;
			System.out.println("Collectible: " + collectible.getName());
			collectible.applyEffect(this);
			return(false);
		}
		
		return(true);
	}
	
	public void hitTop(Object target) {
		if (target instanceof Enemy) {
			Enemy enemy = (Enemy)target;
			enemy.hit();
			this.unconditionalJump();
		} else {
			super.hitTop(target);
			this.refreshJump();
		}
	}
	
	public void hitLeft(Object target) {
		if (target instanceof Enemy) {
			Enemy enemy = (Enemy)target;
			this.takeDamage(enemy.getDamage());
		}
	}
	
	public void hitRight(Object target) {
		if (target instanceof Enemy) {
			Enemy enemy = (Enemy)target;
			this.takeDamage(enemy.getDamage());
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
	
	public void takeDamage(int damage) {
		if (!this.damaged) {
			this.hp -= damage;
			this.damaged = true;
			this.damagedTimer = System.nanoTime() / (double)1000000000L;
			this.model.setOpacity(0.7f);
			
			this.hitSfx.play();
			
//			this.canCollideEntities = false;
			
			if (this.hp <= 0) {
				this.setPosition(-2476, -64);
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
		return(!this.airborne || additionalJumps != 0);
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
