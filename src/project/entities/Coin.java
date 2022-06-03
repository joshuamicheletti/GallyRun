package project.entities;

import project.sound.ISound;
import project.sound.Sound;

// class to implement a coin
public class Coin extends Collectible {
	// sound effect for when a coin is collected
	private ISound sfx;
	
	// Constructor
	public Coin() {
		this.setScale(0.25f);
		this.loadAnimationAndAdapt("./assets/textures/coin2.png", 8, 1); // load the coin sprite sheet
		this.sfx = new Sound("./assets/sounds/coin.ogg", false); // upload the coin collect sound effect
		this.sfx.setVolume(0.05f); // lower the audio volume
	}
	
	// override of the applyEffect method in Collectible, method for defining the behavior of the collectible when collected
	@Override
	public void applyEffect(IPlayer player) {
		player.addCoin(); // add a coin to the player
		this.sfx.play(); // play the sound effect
		this.setToRemove(); // set this entity to be removed
	}
}
