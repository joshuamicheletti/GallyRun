package com.test;

public class Coin extends Collectible {
	public Coin() {
		this.model.loadAnimationAndAdapt("./assets/textures/coin2.png", 8, 1);
	}
	
	public void applyEffect(Player player) {
		player.addCoin();
	}
}
