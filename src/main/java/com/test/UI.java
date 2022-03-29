package com.test;

import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class UI {
	
	private float width;
	private float height;
	private Model hpBar;
	private Model currentHP;
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
		
		this.counter = 0;
		
		
		
//		this.hpBar.setScale(1 / 256f);
	}
	
	public void renderUI(Camera camera) {
		Vector3f cameraPosition = camera.getPosition();
		camera.setPosition(new Vector3f(0, 0, 0));
		
		List<Vector4f> boundingBox = this.hpBar.calculateBoundingBox(false);
		
		float objectWidth = Math.abs(this.hpBar.getX() - boundingBox.get(2).x) * 2;
		float objectHeight = Math.abs(this.hpBar.getY() - boundingBox.get(0).y) * 2;
	
		this.counter += 0.01;
		float hp;
		
		hp = (float)(Math.sin(this.counter) + 1) / 2;
		
		float offset = map(hp, 0, 1, 59, 190);
		
		this.hpBar.setPosition(-this.width / 2 + objectWidth / 2 + 20, this.height / 2 - objectHeight / 2 - 20);
		this.currentHP.setPosition(-this.width / 2 + 20 + offset, this.height / 2 - 49);
		
		this.currentHP.scaleHorizontal(hp);
		
		this.hpBar.render(camera, false);
		this.currentHP.render(camera, false);
		
		camera.setPosition(cameraPosition);
	}
	
	public void updateUI() {
		
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
