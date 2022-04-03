package com.test;

import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class UI {
	
	private float width;
	private float height;
	private Model hpBar;
	private Model currentHP;
	private Model doubleJump;
	private Model coin;
	private Model coinCounter;
	private float counter;
	
	public UI(float width, float height) {
		this.width = width;
		this.height = height;
		
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
//		this.coinCounter.loadTextureAndAdapt("./assets/textures/font.png");
//		this.coinCounter.setAnimationSpeed(10f);
		this.coinCounter.setScale(0.10f);
		
		this.coin = new Model();
		this.coin.loadTextureAndAdapt("./assets/textures/coinIcon.png");
		this.coin.setScale(0.15f);
		
		this.counter = 0;
	}
	
	public void renderUI(Camera camera, Player player) {
		Vector3f cameraPosition = camera.getPosition();
		camera.setPosition(new Vector3f(0, 0, 0));
		
		List<Vector4f> boundingBox = this.hpBar.calculateBoundingBox(false);
		
		float objectWidth = Math.abs(this.hpBar.getX() - boundingBox.get(2).x) * 2;
		float objectHeight = Math.abs(this.hpBar.getY() - boundingBox.get(0).y) * 2;
	
		this.counter += 0.01;
		float hp;
		
//		hp = (float)(Math.sin(this.counter) + 1) / 2;
		hp = player.getHP();
		
//		float offset = map(hp, 0, 1, 59, 190);
		float offset = map((float)hp, 0, 200, 59, 190);
		
		this.hpBar.setPosition(-this.width / 2 + objectWidth / 2 + 20, this.height / 2 - objectHeight / 2 - 20);
		this.currentHP.setPosition(-this.width / 2 + 20 + offset, this.height / 2 - 49);
		this.currentHP.scaleHorizontal(hp / 200);
		
		this.hpBar.render(camera, false);
		this.currentHP.render(camera, false);
		
		this.coin.setPosition(this.width / 2 - 30, this.height / 2 - 30);
		this.coin.render(camera, false);
		
		int coins = player.getCoins();
		
		int firstDigit;
		int secondDigit;
		
		if (coins < 10) {
			firstDigit = coins;
			secondDigit = 0;
		} else {
			firstDigit = coins % 10;
			secondDigit = (int)coins / (int)10;
		}
		
		
		this.coinCounter.setPosition(this.width / 2 - 100, this.height / 2 - 28);
		this.coinCounter.setCurrentAnimation(secondDigit);
		this.coinCounter.updateAnimation(true);
		this.coinCounter.render(camera, false);
		
		this.coinCounter.setPosition(this.width / 2 - 80, this.height / 2 - 28);
		this.coinCounter.setCurrentAnimation(firstDigit);
		this.coinCounter.updateAnimation(true);
		this.coinCounter.render(camera, false);
		
		this.coinCounter.setPosition(this.width / 2 - 60, this.height / 2 - 30);
		this.coinCounter.setCurrentAnimation(10);
		this.coinCounter.updateAnimation(true);
		this.coinCounter.render(camera, false);
		
		
		if (player.canDoubleJump()) {
			this.doubleJump.setPosition(this.width / 2 - 40, -this.height / 2 + 40);
			this.doubleJump.render(camera, false);
		}
		
		camera.setPosition(cameraPosition);
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	private float map(float x, float in_min, float in_max, float out_min, float out_max) {
		  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
