package com.project.entities;

public interface IPlayer extends IEntity {
	// methods for setting the animation of the player
	public void setIdle(boolean idle);
	public void setSleep(boolean sleep);
	public void setGoingToSleep(boolean goingToSleep);
	public void setCrouching(boolean crouching);
	
	// methods for calculating the damage taken by the player, its HP and the damaged state
	public void calculateState();
	public boolean getDamagedState();
	public int getHP();
	public void takeDamage(int damage);
	
	// methods for handling the collectibles (coins)
	public void addCoin();
	public int getCoins();
	
	// methods for handling the jumping abilities of the player
	public void jump();
	public boolean canJump();
	public void refreshJump();
	public void enableDoubleJump();
	public void superJump();
	public boolean canDoubleJump();
	
}
