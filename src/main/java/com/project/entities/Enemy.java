package com.project.entities;

public class Enemy extends Entity {
	
	protected boolean movingRight;
	protected IPlayer player;
	protected int damage;
	protected int behaviour;
	protected float speed;
	
	public Enemy(IPlayer player) {
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
			if (this.getX() < this.player.getX()) {
				this.applyForce(this.speed, 0f);
			} else {
				this.applyForce(-this.speed, 0f);
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
	
	public void hitRight(Object target) {
		if (this.movingRight == false) {
			this.movingRight = true;
		}
		
		if (target instanceof Player) {
			Player player = (Player)target;
			player.takeDamage(this.damage);
		}
	}
	
	public void hitLeft(Object target) {
		if (this.movingRight == true) {
			this.movingRight = false;
		}
		
		if (target instanceof Player) {
			Player player = (Player)target;
			player.takeDamage(this.damage);
		}
	}
	
	public void hit() {
		this.setToRemove();
	}
}
