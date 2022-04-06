package com.test;

public class Coin extends Collectible {
	public Coin() {
		this.model.loadAnimationAndAdapt("./assets/textures/coin2.png", 8, 1);
		this.model.setScale(0.25f);
		this.model.setAnimationSpeed(10f);
	}
	
	public void applyEffect(Player player) {
		player.addCoin();
	}
}
