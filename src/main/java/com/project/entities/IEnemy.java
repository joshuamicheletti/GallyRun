package com.project.entities;

public interface IEnemy extends IEntity {
	// method for handling the behavior of the enemy and update its state and position
	public void control();
	// method for when the enemy gets hit
	public void hit();
	// getters and setters
	public int getDamage();
	public void setBehaviour(int behaviour);
	public void setSpeed(float speed);
}
