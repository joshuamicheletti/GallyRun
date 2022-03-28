package com.test;

public class Collectible extends Entity{
	public Collectible() {
		this.model.setScale(0.25f);
		this.setGravity(0);
		this.model.setAnimationSpeed(10f);
	}
	
	public void applyEffect(Player player) {
		System.out.println("Apply an effect to the player");
	}
}
