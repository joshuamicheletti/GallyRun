package com.test;

public class Coin extends Collectible {
	private Sound sfx;
	
	public Coin() {
		this.model.loadAnimationAndAdapt("./assets/textures/coin2.png", 8, 1);
		this.model.setScale(0.25f);
		this.model.setAnimationSpeed(10f);
		this.sfx = new Sound("./assets/sounds/coin.ogg", false);
		this.sfx.setVolume(0.05f);
	}
	
	public void applyEffect(Player player) {
		player.addCoin();
		this.sfx.play();
		this.setToRemove();
	}
}
