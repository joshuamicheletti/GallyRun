package com.test;

public class Entity {
	public Model model;
	private String name;
	
	public Entity() {
		this.model = new Model();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return(this.name);
	}
}
