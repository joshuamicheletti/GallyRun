package com.test;

public class DoubleJump extends Collectible {
	public DoubleJump() {
		this.model.loadAnimationAndAdapt("./assets/textures/doubleJump.png", 6, 1);
		this.model.setScale(0.3f);
	}
	
	public void applyEffect(Player player) {
		player.enableDoubleJump();
	}
}
