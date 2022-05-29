package com.project.entities;

import com.project.sound.Sound;

public class Coin extends Collectible {
	private Sound sfx;
	
	public Coin() {
		this.loadAnimationAndAdapt("./assets/textures/coin2.png", 8, 1);
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
