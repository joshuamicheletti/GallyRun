package com.test;

import java.util.List;

public class Boss extends Enemy {
	
	private int health;
	private List<Entity> entityBuffer;
	private boolean damaged;
	private double damagedTimer;
	private boolean triggered;
	private Mixer mixer;
	
	public Boss(Player player, List<Entity> entityBuffer, Mixer mixer) {
		super(player);
		
		this.mixer = mixer;
		this.entityBuffer = entityBuffer;
		this.health = 3;
		this.damaged = false;
		this.damagedTimer = System.nanoTime() / 1000000000L;
		this.behaviour = 1;
		this.triggered = false;
		this.damage = 50;
	}
	
	public void doDamage() {
		if (!this.damaged) {
			this.health--;
			this.model.setOpacity(0.7f);
			this.damagedTimer = System.nanoTime() / (double)1000000000L;
			this.damaged = true;
		}
	}
	
	public boolean isTriggered() {
		return(this.triggered);
	}
	
	public void control() {
		if (this.health <= 0) {
			for (int i = 0; i < this.entityBuffer.size(); i++) {
				if (this.entityBuffer.get(i) == this) {
					this.entityBuffer.remove(i);
				}
			}
			for (int i = 0; i < 28; i++) {
				Coin coin = new Coin();
				coin.setGravity(1);
				coin.model.setPosition(this.model.getX(), this.model.getY());
				coin.applyForcePolar(1200, 3.14f / 2f + ((float)Math.random() - 0.5f) * (3.14f / 2f));
				this.entityBuffer.add(coin);
			}
			
			Portal portal = new Portal();
			portal.model.setPosition(this.model.getX(), this.model.getY() + 40);
			this.entityBuffer.add(0, portal);
			
			this.mixer.playSong(1);
			
			return;
			
		} else if (this.health == 2) {
			this.speed = 30;
			this.behaviour = 0;
		} else if (this.health == 1) {
			this.speed = 50;
			this.behaviour = 0;
		}
		
		if (this.damaged) {
			double currentTime = System.nanoTime() / (double)1000000000L;
			
			double delta = currentTime - damagedTimer;
			
			if (delta >= 2) {
				this.damaged = false;
				this.model.setOpacity(1);
			}
		}
		
		float distanceToPlayer = 0;
		
		if (!this.triggered) {
			distanceToPlayer = (float)Math.sqrt(Math.pow(this.player.model.getX() - this.model.getX(), 2) + Math.pow(this.player.model.getY() - this.model.getY(), 2));
			
			if (distanceToPlayer <= 500) {
				this.mixer.playSong(2);
			}
		}
		
		if (distanceToPlayer <= 500 || this.triggered) {
			this.triggered = true;
			
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
	}
}
