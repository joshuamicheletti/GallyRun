package project.entities;

import project.sound.ISound;
import project.sound.Sound;

// class to implement a double jump power up collectible
public class DoubleJump extends Collectible {
	// sound effect to play when the item is collected
	ISound sfx;
	
	// Constructor
	public DoubleJump() {
		this.setScale(0.3f); // increase the size of the collectible slightly
		this.loadAnimationAndAdapt("./assets/textures/doubleJump.png", 6, 1); // load the sprite sheet of the double jump
		this.sfx = new Sound("./assets/sounds/powerup.ogg", false); // load the sound effect for when it gets collected
	}
	
	// method for applying an effect to the player when it's collected
	@Override
	public void applyEffect(IPlayer player) {
		player.enableDoubleJump(); // give the player the ability to double jump
		this.sfx.play(); // play the sound effect
		this.setToRemove(); // set this entity to be removed
	}
}
