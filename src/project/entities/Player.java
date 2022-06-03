package project.entities;

import project.rendering.IModel;
import project.rendering.Model;
import project.sound.ISound;
import project.sound.Sound;

// class to implement a player object that will act as the playable character
public class Player extends Entity implements IPlayer{
	// model object for the small effects next to the player (ZZZ, 50 coins needed)
	public IModel allert;
	// sound effect for when the player gets hit
	private ISound hitSfx;
	// sound effect for when the player hits an enemy
	private ISound attackSfx;
	// sound effect for when the player jumps
	private ISound jumpSfx;
	// sound effect for when the player super jumps
	private ISound superJumpSfx;
	
	// flags to keep track of the animation state of the player
	private boolean idle;
	private boolean sleeping;
	private boolean goingToSleep;
	private boolean crouching;
	
	// counter of collected coins
	private int coins;
	// flag to keep track of the damaged state of the player when it gets hit
	private boolean damaged;
	// timer to keep track of the duration of the damaged state
	private double damagedTimer;
	
	// counter of current HP
	private int hp;
	
	// counter of available jumps
	private int additionalJumps;
	// flag to keep track of whether or not the player has the ability to double jump
	private boolean canDoubleJump;
	
	// Constructor
	public Player() {
		// initialize the allert object
		this.allert = new Model();
		
		// initialize the animation state variables to false
		this.idle = false;
		this.sleeping = false;
		this.goingToSleep = false;
		this.crouching = false;
		this.airborne = false;
		
		// initialize the coins to 0
		this.coins = 0;
		
		// initialize the additional jumps counter to 0 (we don't have the ability to double jump yet)
		this.additionalJumps = 0;
		this.canDoubleJump = false;
		
		// initialize the HP to 200;
		this.hp = 200;
		// initialize the damaged state to false
		this.damaged = false;
		
		// adjust the scale
		this.setScale(0.5f);
		
		// load all the sound effects
		this.hitSfx = new Sound("./assets/sounds/hit.ogg", false);
		this.attackSfx = new Sound("./assets/sounds/attack.ogg", false);
		this.jumpSfx = new Sound("./assets/sounds/jump.ogg", false);
		this.superJumpSfx = new Sound("./assets/sounds/superJump.ogg", false);
	}
	
	// Override methods
	@Override
	public void calculatePosition() {
		super.calculatePosition();
		
		// adjust the position of the allert to be aligned with the position of the player, depending whether it's facing right or left
		if (this.facingRight) {
			this.allert.setPosition(this.positionX + this.model.getScaleMul() / 2 + this.model.getScaleMul() / 20,
									this.positionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		} else {
			this.allert.setPosition(this.positionX - this.model.getScaleMul() / 2 - this.model.getScaleMul() / 20,
							 		this.positionY + this.model.getScaleMul() / 2 - this.model.getScaleMul() / 10);
		}
	}
	@Override
	public void updateAnimation() {
		if (this.airborne) { // if the player is airborne	
			float threshold = 5f;
			// set the animation to display depending on the vertical velocity of the player
			if (super.velocityY > threshold && this.model.getFrames().size() >= 3) { // going up
				this.model.setCurrentAnimation(2);
			} else if (this.velocityY >= -threshold && this.velocityY <= threshold  && this.model.getFrames().size() >= 4) { // stationary
				this.model.setCurrentAnimation(3);
			} else if (this.velocityY < -threshold  && this.model.getFrames().size() >= 5) { // going down
				this.model.setCurrentAnimation(4);
			}
		} else { 			 // if the player is not airborne		
			float threshold = 0.02f;
			// adjust the animation depending on the horizontal speed
			if (this.velocityX < threshold && this.velocityX > -threshold) { // not moving
				if (this.idle) { // if the player is in idle
					this.model.setAnimationSpeed(1f);  // adjust the animation speed
					this.model.setCurrentAnimation(5); // set the correct animation
				} else if (this.goingToSleep) { // if the player is going to sleep
					this.model.setAnimationSpeed(1f);  // adjust the animation speed
					this.model.setCurrentAnimation(6); // set the correct animation
				} else if (this.sleeping) { // if the player is sleeping
					this.model.setAnimationSpeed(1f);  // adjust the animation speed
					this.model.setCurrentAnimation(7); // set the correct animation
				} else { // if it's not in any of the previous states
					if (this.crouching) { // if the player is crouching
						this.model.setCurrentAnimation(8); // set the correct animation
					} else { // otherwise
						this.model.setCurrentAnimation(0); // set the default animation
					}
				}
			} else if (this.model.getFrames().size() >= 1) { // moving
				this.model.setAnimationSpeed(3f); // adjust the animation speed
				if (this.crouching) { // if the player is crouching
					this.model.setCurrentAnimation(9); // set the correct animation
				} else { // otherwise
					this.model.setCurrentAnimation(1); // set the correct animation (running)
				}
			}
		}
		
		this.model.updateAnimation(this.facingRight); // update the model with the correct animation
		this.allert.updateAnimation(true); // update the allert model with the correct animation
	}
	@Override
	public void setScale(float scale) {
		super.setScale(scale);
		// scale the allert with the player to match
		this.allert.setScale(scale / 2.5f);
	}
	@Override
	public boolean collision(Object target) {
		if (target instanceof Collectible) { // if the collision happened with a collectible
			Collectible collectible = (Collectible)target; // downcast Object to Collectible
			collectible.applyEffect(this); // apply the effect of the collectible
			return(false); // don't make the player stop on collision with a collectible
		}
		
		return(true); // make the player stop on collision with the object
	}
	@Override
	public void hitTop(Object target) {
		if (target instanceof Enemy) { // if the player is hitting an enemy from the top
			Enemy enemy = (Enemy)target; // downcast the Object to Enemy
			enemy.hit(); // apply the hit to the enemy
			this.unconditionalJump(); // make the player jump
		} else { // if the collision isn't with an enemy
			super.hitTop(target);
			this.refreshJump(); // re-enable the jump, as it's resting on something
		}
	}
	@Override
	public void hitLeft(Object target) {
		if (target instanceof Enemy) { // if the player is colliding with an enemy from the side
			Enemy enemy = (Enemy)target; // downcast the Object to Enemy
			this.takeDamage(enemy.getDamage()); // take the damage that the enemy deals
		}
	}
	@Override
	public void hitRight(Object target) {
		if (target instanceof Enemy) {
			Enemy enemy = (Enemy)target;
			this.takeDamage(enemy.getDamage());
		}
	}
	
	// methods for setting the animation state of the player (normal, idling, going to sleep, sleeping or crouching)
	public void setIdle(boolean idle) {
		this.idle = idle;
		this.sleeping = false;
		this.goingToSleep = false;
	}
	public void setSleep(boolean sleep) {
		this.sleeping = sleep;
		this.idle = false;
		this.goingToSleep = false;
		
		if (this.sleeping) { // adjust the allert to display "ZZ" when the player is sleeping
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
	public void setCrouching(boolean crouching) {
		this.crouching = crouching;
	}
	
	// method for calculating the state of the player, to check whether it's in a hit state or not
	public void calculateState() {
		if (this.damaged) { // if the player is in a damaged state
			double currentTime = System.nanoTime() / (double)1000000000L; // take the current time
			
			double delta = currentTime - damagedTimer; // calculate the time passed since the player got hit
			
			if (delta >= 1) { // if more than 1s passed
				this.damaged = false; // disable the damaged state
				this.model.setOpacity(1); // reset the player opacity to 1
			}
		}
	}
	
	// getter for the state of the player (damaged or not)
	public boolean getDamagedState() {
		return(this.damaged);
	}
	
	// getter for the hp of the player
	public int getHP() {
		return(this.hp);
	}
	
	// method for taking damage (reduces hp and puts the player in a damaged state. if your hp falls below 0, you die and respawn)
	public void takeDamage(int damage) {
		if (!this.damaged) { // if the player isn't in a damaged state
			this.hp -= damage; // reduce the hp by the amount of damage taken
			this.damaged = true; // enable the damaged state
			this.damagedTimer = System.nanoTime() / (double)1000000000L; // take the time when the player got hit
			this.model.setOpacity(0.7f); // reduce the opacity of the player
			
			this.hitSfx.play(); // play the sound effect of getting hit
			
			if (this.hp <= 0) { // if the hp of the player falls below 0
				this.setPosition(-2476, -64); // reset the position of the player to the beginning of the map
				this.hp = 200; // refill the player's hp bar
			}
		}
	}
	
	// method for adding coins to the player
	public void addCoin() {
		this.coins++;
	}
	// getter for coins
	public int getCoins() {
		return(this.coins);
	}
	
	// method to enable the ability to double jump
	public void enableDoubleJump() {
		this.canDoubleJump = true;
	}
	
	// method to refresh the ability to jump
	public void refreshJump() {
		if (this.canDoubleJump) {
			this.additionalJumps = 1;
		}
	}
	
	// method to check whether or not the player can jump (isn't airborne and has jumps available)
	public boolean canJump() {
		return(!this.airborne || additionalJumps != 0);
	}
	
	// method to make the player jump
	public void jump() {
		// play the jump sound effect
		this.jumpSfx.playRaw();
		// nullify the vertical velocity of the player
		this.setVelocity(this.getVelocityX(), 0);
		
		// if the player is airborne
		if (this.airborne) {
			this.additionalJumps--; // spend an additional jump
			
			if (this.additionalJumps < 0) {
				this.additionalJumps = 0;
			}
			
		}
		
		this.applyForce(0, 1400); // apply a vertical force to represent the jump
	}

	// method to jump after killing an enemy, it doesn't care whether the player can jump or not, and doesn't spend an additional jump
	private void unconditionalJump() {
		this.attackSfx.playRaw(); // play the attack sound effect (this method only happens when hitting an enemy)
		this.setVelocity(this.getVelocityX(), 0); // nullify the vertical velocity
		
		this.applyForce(0, 1400); // apply a vertical force to simulate the jump
	}
	
	// method to perform a super jump
	public void superJump() {
		this.superJumpSfx.playRaw(); // play the super jump sound effect
		this.applyForce(0, 4000); // apply a strong vertical force
	}
	
	// method to check if the player can double jump
	public boolean canDoubleJump() {
		return(this.canDoubleJump);
	}
	
	// method to get the allert model
	public IModel getAllert() {
		return(this.allert);
	}
	
	// method to check if the player is sleeping
	public boolean isSleeping() {
		return(this.sleeping);
	}
}
