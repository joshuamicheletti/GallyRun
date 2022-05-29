package com.project.entities;

public class Portal extends Collectible {
	private double spawnTimer;
	private boolean spawned;
	private boolean win;
	
	public Portal() {
		this.loadAnimationAndAdapt("./assets/textures/portal.png", 8, 3);
		this.spawnTimer = System.nanoTime() / 1000000000L;
		this.spawned = false;
		this.model.setCurrentAnimation(1);
		this.setScale(2f);
		this.model.setAnimationSpeed(4f);
		this.win = false;
	}
	
	public void applyEffect(Player player) {
		if (player.getCoins() < 50) {
			System.out.println("Portal Interaction, coins: " + player.getCoins());
			player.allert.setCurrentAnimation(2);
		} else {
			this.win = true;
		}
	}
	
	public void updateAnimation() {
		double currentTime = System.nanoTime() / 1000000000L;
		
		double delta = currentTime - this.spawnTimer;
		
		if (delta >= 2 && !this.spawned) {
			this.spawned = true;
			this.model.setCurrentAnimation(0);
		}
		
		this.model.updateAnimation(false);
	}
	
	public boolean getWin() {
		return(this.win);
	}
	
	
}
