package project.entities;

// abstract class to implement collectibles (coins, powerups, portals)
public abstract class Collectible extends Entity {
	// Constructor
	public Collectible() {
		this.setGravity(0); // disable gravity for collectibles
		this.model.setAnimationSpeed(10f); // set the animation speed to 10 fps
	}
	
	// method to apply an effect to the player when it collects this collectible
	public void applyEffect(IPlayer player) {
		System.out.println("Apply an effect to the player");
		this.setToRemove(); // set the collectible to be removed
	}
}
