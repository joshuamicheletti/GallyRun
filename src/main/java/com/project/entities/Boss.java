package com.project.entities;

import java.util.List;

import com.project.sound.IMixer;

// class to implement a Boss enemy
public class Boss extends Enemy {
	private int health; // health of the boss
	private List<IEntity> entityBuffer; // reference to the entity buffer (to add entities when the boss dies)
	private boolean damaged; // flag to keep track of the damaged state
	private double damagedTimer; // timer to keep track of the duration of the damaged state
	private boolean triggered; // flag to track whether the boss is activated or not
	private IMixer mixer; // reference to the mixer, to play the boss song the moment it gets triggered
	
	// Constructor
	public Boss(IPlayer player, List<IEntity> entityBuffer, IMixer mixer) {
		// initialize the object as an enemy
		super(player);
		this.setScale(1.5f); // adjust the scale
		this.mixer = mixer; // store the mixer reference
		this.entityBuffer = entityBuffer; // store the entityBuffer reference
		this.health = 3; // initialize the boss HP to 3 (meaning it takes 3 hits to kill)
		this.damaged = false; // initialize the damaged state to false
		this.damagedTimer = System.nanoTime() / 1000000000L; // initialize the damaged timer
		this.behaviour = 1; // initially, the boss will have behavior 1, meaning it will follow the player
		this.triggered = false; // initially the boss isn't active
		this.damage = 50; // initialize the boss damage (amount of damage inflicted to the player)
	}
	
	// override of the hit method of Enemy, method to determine the behavior of the Boss when hit by the player
	@Override
	public void hit() {
		if (!this.damaged) { // if the boss isn't in a damaged state
			this.health--; // decrease the boss HP
			this.model.setOpacity(0.7f); // set the opacity to 70% to display that it's in a damaged state
			this.damagedTimer = System.nanoTime() / (double)1000000000L; // take the current time
			this.damaged = true; // set the boss into a damaged state
		}
	}
	
	// method for checking if the boss is triggered
	public boolean isTriggered() {
		return(this.triggered);
	}
	
	// override of the control method of Enemy, method to decide the behavior of the boss and what to do next
	@Override
	public void control() {
		if (this.health <= 0) { // if the died
			this.toRemove = true; // set this entity as to be removed
			
			// drop the loot from the boss
			for (int i = 0; i < 28; i++) { // create 28 coins
				Coin coin = new Coin();
				coin.setGravity(1); // these coins have gravity
				coin.setPosition(this.getX(), this.getY());
				coin.applyForcePolar(1200, 3.14f / 2f + ((float)Math.random() - 0.5f) * (3.14f / 2f)); // apply a force to make the coins
																									   // spread in the air
				this.entityBuffer.add(coin); // add the coins to the entityBuffer
			}
			
			// add a portal to the entity buffer
			Portal portal = new Portal();
			portal.setPosition(this.model.getX(), this.model.getY() + 40);
			this.entityBuffer.add(0, portal);
			
			// play the winning song
			this.mixer.playSong(1);
			// exit this method
			return;	
		} else if (this.health == 2) { // if the boss has 2 hp left
			this.speed = 30; // increase the speed to 30
			this.behaviour = 0; // make the boss run left and right
		} else if (this.health == 1) { // if the boss has 1 hp left
			this.speed = 50; // increase the speed to 50
			this.behaviour = 0; // make the boss run left and right
		}
		
		// if the boss is in a damaged state
		if (this.damaged) {
			double currentTime = System.nanoTime() / (double)1000000000L; // take the current time
			
			double delta = currentTime - damagedTimer; // calculate the time passed since the boss got hit
			
			if (delta >= 2) { // if 2 seconds passed
				this.damaged = false; // disable the damaged state
				this.model.setOpacity(1); // restore the model's opacity to 100%
			}
		}
		
		// keep track of the distance to the player
		float distanceToPlayer = 0;
		// if the boss isn't already triggered
		if (!this.triggered) {
			// calculate the distance from the boss to the player
			distanceToPlayer = (float)Math.sqrt(Math.pow(this.player.getX() - this.getX(), 2) + Math.pow(this.player.getY() - this.getY(), 2));
			// if the distance is less than 500 units
			if (distanceToPlayer <= 500) {
				this.triggered = true; // the boss triggers
				this.mixer.playSong(2); // the boss music starts playing
			}
		}
		
		// if the boss is triggered
		if (this.triggered) {
			// move according to the behavior (same as enemy behavior)
			if (this.behaviour == 0) {
				if (this.movingRight) {
					this.applyForce(this.speed, 0f);
				} else {
					this.applyForce(-this.speed, 0f);
				}
			} else if (this.behaviour == 1) {
				if (this.getX() < this.player.getX()) {
					this.applyForce(this.speed, 0f);
				} else {
					this.applyForce(-this.speed, 0f);
				}
			}	
		}
	}
}
