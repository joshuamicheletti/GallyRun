package com.test;

public class Entity {
	public Model model;
	
	public Entity(Camera camera) {
		this.model = new Model(camera);
	}
}
