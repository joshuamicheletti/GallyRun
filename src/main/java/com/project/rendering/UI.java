package com.project.rendering;

import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.project.entities.Player;

// class for rendering UI elements
public class UI {
	
	
	private float width; // width of the window
	private float height; // height of the window
	
	// UI elements
	private Model hpBar; // hp bar container
	private Model currentHP; // hp bar
	private Model doubleJump; // icon for the double jump powerup
	private Model coin; // coin icon
	private Model coinCounter; // counter of collected coins
	private Model winCounter;
	private Model winScreen;
	
	private int winTimer;
	
	
	// Constructor
	public UI(float width, float height) {
		// window width and height
		this.width = width;
		this.height = height;
		
		// create the models and load the textures accordingly
		this.hpBar = new Model();
		this.hpBar.loadTextureAndAdapt("./assets/textures/hpUI.png");
		this.hpBar.setScale(1.5f);
		
		this.currentHP = new Model();
		this.currentHP.loadTextureAndAdapt("./assets/textures/hp.png");
		this.currentHP.setScale(1.5f);
		
		this.doubleJump = new Model();
		this.doubleJump.loadTextureAndAdapt("./assets/textures/doubleJumpUI.png");
		this.doubleJump.setScale(0.25f);
		
		this.coinCounter = new Model();
		this.coinCounter.loadAnimationAndAdapt("./assets/textures/fontV.png", 1, 11);
		this.coinCounter.setScale(0.10f);
		
		this.coin = new Model();
		this.coin.loadTextureAndAdapt("./assets/textures/coinIcon.png");
		this.coin.setScale(0.15f);
		
		this.winCounter = new Model();
		this.winCounter.loadAnimationAndAdapt("./assets/textures/fontV.png", 1, 11);
		this.winCounter.setScale(0.5f);
		
		this.winScreen = new Model();
		this.winScreen.loadTextureAndAdapt("./assets/textures/win.png");
		this.winScreen.setScale(4f);
		
		this.winTimer = 0;
	}
	
	
	// UI rendering method
	public void renderUI(Camera camera, Player player) {
		// store the camera position
		Vector3f cameraPosition = camera.getPosition();
		// place the camera at the center
		camera.setPosition(new Vector3f(0, 0, 0));
		
		// get the dimensions of the hp bar container
		List<Float> boundingBox = this.hpBar.calculateBoundingBox();
		
		// calculate the width and height of the hp bar container
		float objectWidth = boundingBox.get(0);
		float objectHeight = boundingBox.get(1);

		// get the player HP
		float hp = player.getHP();
		
		// calculate the offset to apply to the current hp bar to adapt do the new dimensions of the bar, which depend on the player HP
		float offset = map((float)hp, 0, 200, 59, 190);
		
		// place the hp bar relative to the window dimensions
		this.hpBar.setPosition(-this.width / 2 + objectWidth / 2 + 20, this.height / 2 - objectHeight / 2 - 20);
		// place the current HP bar relative to the window dimensions
		this.currentHP.setPosition(-this.width / 2 + 20 + offset, this.height / 2 - 49);
		// scale the hp bar horizontally depending on the player HP
		this.currentHP.scaleHorizontal(hp / 200);
		
		// render the HP bar container
		this.hpBar.render(camera, false);
		// render the HP bar
		this.currentHP.render(camera, false);
		
		// place the coin icon relative to the window dimensions
		this.coin.setPosition(this.width / 2 - 30, this.height / 2 - 30);
		// render the coin icon
		this.coin.render(camera, false);
		
		// get the current coins collected by the player
		int coins = player.getCoins();
		
		// calculate the first and second digit of the coins counter
		int firstDigit;
		int secondDigit;
		
		// if the player has less than 10 coins
		if (coins < 10) {
			// the first digit is the coins collected by the player
			firstDigit = coins;
			// and the second digit is 0
			secondDigit = 0;
		} else { // otherwise if the player has more than 10 coins (double digits)
			// the first digit is the rest of the division by 10 of the coins
			firstDigit = coins % 10;
			// the second digit is the division of the coins by 10 and then truncated to the first unit
			secondDigit = (int)coins / (int)10;
		}
		
		// place the second digit of the coin counter relative to the window dimensions
		this.coinCounter.setPosition(this.width / 2 - 100, this.height / 2 - 28);
		// switch to the frame in the spritesheet corresponding to the selected digit
		this.coinCounter.setCurrentAnimation(secondDigit);
		// update the sprite
		this.coinCounter.updateAnimation(true);
		// render it
		this.coinCounter.render(camera, false);
		
		// place the first digit of the coin counter relative to the window dimensions
		this.coinCounter.setPosition(this.width / 2 - 80, this.height / 2 - 28);
		// switch to the frame in the spritesheet corresponding to the selected digit
		this.coinCounter.setCurrentAnimation(firstDigit);
		// update the sprite
		this.coinCounter.updateAnimation(true);
		// render it
		this.coinCounter.render(camera, false);
		
		// place the "x" character relative to the window dimensions
		this.coinCounter.setPosition(this.width / 2 - 60, this.height / 2 - 30);
		// switch to the frame in the spritesheet corresponding to the "x" character
		this.coinCounter.setCurrentAnimation(10);
		// update the sprite
		this.coinCounter.updateAnimation(true);
		// render it
		this.coinCounter.render(camera, false);
		
		// check if the player has the powerup of double jump
		if (player.canDoubleJump()) {
			// position the double jump icon relative to the window dimensions
			this.doubleJump.setPosition(this.width / 2 - 40, -this.height / 2 + 40);
			// render it
			this.doubleJump.render(camera, false);
		}
		
		if (this.winTimer != 0) {
			this.winCounter.setPosition(0, -220);
			this.winCounter.setCurrentAnimation(this.winTimer);
			this.winCounter.updateAnimation(true);
			this.winCounter.render(camera, false);
			this.winScreen.render(camera, false);
		}
		
		// restore the original camera position
		camera.setPosition(cameraPosition);
	}
	
	// width setter (for when the window is resized)
	public void setWidth(float width) {
		this.width = width;
	}
	
	// height setter (for when the window is resized)
	public void setHeight(float height) {
		this.height = height;
	}
	
	// map function that interpolates a value from one range to another
	private float map(float x, float in_min, float in_max, float out_min, float out_max) {
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	public void setWinTimer(int number) {
		this.winTimer = number;
	}
}
