package project.main;

import project.game.Game;

// Main class
public class Main {
	// main method
	public static void main(String[] args) {
		// create a new game object
		Game game = new Game();
		// run the game loop
		game.loop();
	}
}
