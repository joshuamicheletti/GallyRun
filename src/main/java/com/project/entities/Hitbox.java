package com.project.entities;

// class to implement a Hitbox (similar to PhysicsBody but just for collision and special attributes)
public class Hitbox extends PhysicsBody{
	// special attribute of the hitbox
	private boolean specialJump;
	
	// Constructor
	public Hitbox() {
		super(); // construct a PhysicsBody
		this.specialJump = false; // set the special attribute to false
	}
	
	// setters and getters for the special attribute
	public void setSpecialJump(boolean jump) {
		this.specialJump = jump;
	}
	public boolean getSpecialJump() {
		return(this.specialJump);
	}
}
