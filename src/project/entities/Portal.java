package project.entities;

// class to implement a portal (end of the game)
public class Portal extends Collectible {
	private double spawnTimer; // timer to keep track of when the portal spawned
	private boolean spawned; // flag to keep track of whether the portal already spawned or not
	private boolean win; // flag to keep track of whether the player meets the requirements to win
	
	// Constructor
	public Portal() {
		this.setScale(2f); // adjust the scale
		this.loadAnimationAndAdapt("./assets/textures/portal.png", 8, 3); // load the portal sprite sheet
		this.setBBWidth(this.getBBWidth() * 0.3f);
		this.spawnTimer = System.nanoTime() / 1000000000L; // take the current time
		this.spawned = false; // portal didn't spawn yet
		this.model.setCurrentAnimation(1); // set the proper animation
		this.model.setAnimationSpeed(4f); // set the animation speed to 4 fps
		this.win = false; // player hasn't won yet
	}
	
	// override of the applyEffect method of Collectible
	@Override
	public void applyEffect(IPlayer player) {
		if (player.getCoins() < 50) { // if the player has less than 50 coins
			player.getAllert().setCurrentAnimation(2); // notify the player through its allert that it's missing coins
		} else { // if the player meets the requirements to win
			this.win = true; // the player wins
		}
	}
	// override of the updateAnimation method of Entity
	@Override
	public void updateAnimation() {
		double currentTime = System.nanoTime() / 1000000000L; // take the current time
		double delta = currentTime - this.spawnTimer; // calculate the time passed since the portal spawned
		
		if (delta >= 1.8 && !this.spawned) { // if 2s passed and the portal hasn't spawned yet
			this.spawned = true; // set that the portal spawned
			this.model.setCurrentAnimation(0); // set the proper animation
		}
		
		this.model.updateAnimation(false); // update the model animation accordingly
	}
	
	// getter for the win state of the portal
	public boolean getWin() {
		return(this.win);
	}
}
