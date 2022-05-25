package com.test;

import java.util.List;

import org.joml.Vector4f;

// class for any entity in the game (player, enemies, collectibles)
public class Entity extends PhysicsBody {
	// model object to store the sprite of the entity
	public Model model;
	// name of the entity
	protected String name;
		
	// status of the sprite
	// airborne (not touching the ground)
	protected boolean airborne;
	// facing right (or left)
	protected boolean facingRight;
	
	// flag to check when the entity can do a super jump
	protected boolean ableToSuperJump;
	
	// Constructor
	public Entity() {
		super();
		// initialize the model object for the sprite
		this.model = new Model();
		
		// set the state flags
		this.airborne = false;
		
		this.facingRight = true;
		
		this.ableToSuperJump = false;
	}
	
	// setters and getters for the entity name
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return(this.name);
	}
	
	public void hitTop(PhysicsBody p, Hitbox h) {
		if (h.getSpecialJump()) {		  // if the hitbox enables a super jump, mark it as available		  
			this.ableToSuperJump = true;
		}
		
		this.airborne = false;
	}
	
	public void checkCollision(List<Entity> entityBuffer, List<Hitbox> worldHitboxes) {
		this.airborne = true;
		this.ableToSuperJump = false;
		
		super.checkCollision(entityBuffer, worldHitboxes);
	}

	public void setCollision(boolean flag) {
		this.canCollide = flag;
	}
	
	public boolean canCollide() {
		return(this.canCollide);
	}

	public void calculatePosition() {
		super.calculatePosition();
		this.model.setPosition(this.positionX, this.positionY);
	}
	
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}

	
	public void applyNewPosition() {
		this.model.setPosition(this.newPositionX, this.newPositionY);
	}
	
	public void setNewPosition(float x, float y) {
		this.newPositionX = x;
		this.newPositionY = y;
	}
	
	public void setVelocity(float x, float y) {
		this.velocityX = x;
		this.velocityY = y;
	}
	
	public float getVelocityX() {
		return(this.velocityX);
	}
	
	public float getVelocityY() {
		return(this.velocityY);
	}
	
	public void updateAnimation() {
		this.model.updateAnimation(this.facingRight);
	}
	
	public void setOrientation(boolean direction) {
		this.facingRight = direction;
	}
	
	public boolean isFacingRight() {
		return(this.facingRight);
	}
	
	public boolean isAirborne() {
		return(this.airborne);
	}
	
	public boolean canSuperJump() {
		return(this.ableToSuperJump);
	}
	
	public void loadTextureAndAdapt(String filename) {
		this.model.loadTextureAndAdapt(filename);
		
		this.initializeBBSize();
	}
	
	public void loadAnimationAndAdapt(String filename, int steps, int animations) {
		this.model.loadAnimationAndAdapt(filename, steps, animations);
		
		this.initializeBBSize();
	}
	
	public void initializeBBSize() {
		List<Vector4f> bbPoints = this.model.calculateBoundingBox();
		
		this.bbW = (bbPoints.get(0).x - this.positionX) * 2;
		this.bbH = (bbPoints.get(0).y - this.positionY) * 2;
	}
	
	public void setScale(float scale) {
		this.model.setScale(scale);
		
		this.bbW *= scale;
		this.bbH *= scale;
	}
}
