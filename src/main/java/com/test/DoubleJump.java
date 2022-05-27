package com.test;

public class DoubleJump extends Collectible {
	Sound sfx;
	
	public DoubleJump() {
		this.loadAnimationAndAdapt("./assets/textures/doubleJump.png", 6, 1);
		this.setScale(0.3f);
		
		this.sfx = new Sound("./assets/sounds/powerup.ogg", false);
	}
	
	public void applyEffect(Player player) {
		player.enableDoubleJump();
		this.sfx.play();
		this.setToRemove();
	}
}
