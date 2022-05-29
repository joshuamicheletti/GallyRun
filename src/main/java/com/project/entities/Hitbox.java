package com.project.entities;

public class Hitbox extends PhysicsBody{
	
	private boolean specialJump;
	
	public Hitbox() {
		super();
		this.specialJump = false;
	}
	
	public void setSpecialJump(boolean jump) {
		this.specialJump = jump;
	}
	public boolean getSpecialJump() {
		return(this.specialJump);
	}
}
