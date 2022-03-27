package com.test;

public class Player extends Entity{
	public Model allert;
	
	private boolean idle;
	private boolean sleeping;
	private boolean goingToSleep;
	
	public Player() {
		this.allert = new Model();
		
		this.idle = false;
		this.sleeping = false;
		this.goingToSleep = false;
	}
	
	public boolean canJump() {
		return(!this.airborne && !this.stuck);
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
					this.model.setCurrentAnimation(0);
				}
		
			} else if (this.model.getFrames().size() >= 1) {
				this.model.setAnimationSpeed(3f);
				this.model.setCurrentAnimation(1);
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
}
