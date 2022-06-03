package project.entities;

// class to implement an Enemy
public class Enemy extends Entity implements IEnemy {
	protected boolean movingRight; // boolean variable to keep track of which direction the enemy should move
	protected IPlayer player; // reference to the player
	protected int damage; // damage dealt by the enemy
	protected int behaviour; // variable to keep track of what behavior the enemy should follow
	protected float speed; // movement speed of the enemy
	
	// Constructor
	public Enemy(IPlayer player) {
		this.movingRight = false; // enemy starts by moving left
		this.player = player; // save the reference to the player
		this.damage = 25; // enemy deals 25 HP of damage per hit
		this.behaviour = 0; // initial behavior = 0 (move left and right)
		this.speed = 10f; // initial movement speed of the enemy
		this.setScale(0.5f); // adjust the scale
	}
	
	// method for taking decisions and applying movement to the enemy depending on the behavior
	public void control() {
		if (this.behaviour == 0) { // moving left and right
			if (this.movingRight) { // if the enemy should be moving right
				this.applyForce( this.speed, 0f); // apply a force to the right
			} else { 				// if the enemy should be moving left
				this.applyForce(-this.speed, 0f); // apply a force to the left
			}
		} else if (this.behaviour == 1) { // following the player
			if (this.getX() < this.player.getX()) { // if the enemy is to the left of the player
				this.applyForce( this.speed, 0f); // move right
			} else { 								// if the enemy is to the right of the player
				this.applyForce(-this.speed, 0f); // move left
			}
		}	
	}

	// override method to define how to update the animation of the enemy
	@Override
	public void updateAnimation() {
		if (this.airborne && this.model.getFrames().size() >= 2) { // if the enemy is airborne and has an airborne animation			
			float threshold = 5f;
			
			if (this.velocityY > threshold && this.model.getFrames().size() >= 3) { // if its rising and has an animation for it
				this.model.setCurrentAnimation(2); // set the appropriate animation
			} else if (this.velocityY >= -threshold && this.velocityY <= threshold  && this.model.getFrames().size() >= 4) { // if it's 
																								// stationary and has an animation for it
				this.model.setCurrentAnimation(3); // set the appropriate animation
			} else if (this.velocityY < -threshold  && this.model.getFrames().size() >= 5) { // if it's falling and has an animation for it
				this.model.setCurrentAnimation(4); // set the appropriate animation
			}
		} else { // if the enemy is on the ground or doesn't have an airborne animation	
			float threshold = 0.02f;
	
			if (this.velocityX < threshold && this.velocityX > -threshold) { // if the enemy is not moving
				this.model.setCurrentAnimation(0); // set the appropriate animation
			} else if (this.model.getFrames().size() >= 1) { // if the enemy is moving and has an animation for it
				this.model.setAnimationSpeed(3f); // set the animation speed to 3 fps
				this.model.setCurrentAnimation(1); // set the appropriate animation
			}
		}
		// update the model's animation taking into account the direction the enemy is facing
		this.model.updateAnimation(this.facingRight);
	}
	// override method of PhysicsBody to define the behavior in case the enemy hits a body from the right
	@Override
	public void hitRight(Object target) {
		if (this.movingRight == false) { // make the enemy move right
			this.movingRight = true;
		}
		
		if (target instanceof IPlayer) { // if the enemy hit a player
			IPlayer player = (IPlayer)target; // downcast the object
			player.takeDamage(this.damage); // apply damage to the player
		}
	}
	// override method of PhysicsBody to define the behavior in case the enemy hits a body from the left
	@Override
	public void hitLeft(Object target) {
		if (this.movingRight == true) { // make the enemy move left
			this.movingRight = false;
		}
		
		if (target instanceof IPlayer) { // if the enemy hit a player
			IPlayer player = (IPlayer)target; // downcast the object
			player.takeDamage(this.damage); // apply damage to the player
		}
	}
	// override method of PhysicsBody to define the behavior in case the enemy hits a body from the top
	@Override
	public void hitTop(Object target) {
		super.hitTop(target); // call the normal behavior
		
		if (target instanceof IPlayer) { // if the enemy hit a player
			IPlayer player = (IPlayer)target; // downcast the object
			player.takeDamage(this.damage); // apply damage to the player
		}
		
	}
	
	// method for when the enemy gets hit by a player
	public void hit() {
		this.setToRemove(); // set the enemy to be removed
	}
	
	// getters and setters
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
