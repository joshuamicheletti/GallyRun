package project.entities;

import java.util.List;

import project.rendering.IModel;
import project.rendering.Model;

// class for any entity in the game (player, enemies, collectibles)
public class Entity extends PhysicsBody implements IEntity {
	// model object to store the sprite of the entity
	public IModel model;
	// name of the entity
	protected String name;
	
	// flag to check when the entity can do a super jump
	protected boolean ableToSuperJump;
	// flag to keep track of whether the entity is to be removed or not
	protected boolean toRemove;
	
	// Constructor
	public Entity() {
		super();
		// initialize the model object for the sprite
		this.model = new Model();
		
		// set the state flags
		this.airborne = true;
		this.ableToSuperJump = false;
		this.toRemove = false;
	}
	
	// setters and getters for the entity name
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return(this.name);
	}
	
	// override of the behaviours in case of collisions
	@Override
	protected void hitTop(Object target) {
		if (target instanceof Hitbox) {		// if the object that the entity collided with from the top is a hitbox
			Hitbox h = (Hitbox)target;
			
			if (h.getSpecialJump()) {       // if the hitbox enables a super jump, mark it as available
				this.ableToSuperJump = true;
			}
		}
	}
	@Override
	protected boolean collision(Object target) {
		if (target instanceof Collectible) { // if the object that the entity collided with is a collectible, don't fix the entity's
			return(false);					 // position (goes through the collectible)
		}
		return(true);
	}
	// override the checkCollision method to keep track of the states where the entity is airborne or able to superjump
	@Override
	public void checkCollision(List<IPhysicsBody> bodies, boolean sort) {
		// the entity not able to superjump until proven that it can
		this.ableToSuperJump = false;
		
		super.checkCollision(bodies, sort);
	}
	// override the calculatePosition method to update the position of the model for rendering
	@Override
	public void calculatePosition() {
		super.calculatePosition();
		this.model.setPosition(this.positionX, this.positionY);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		this.model.setPosition(x, y);
	}
	
	// wrapper for the updateAnimation method of the model
	public void updateAnimation() {
		this.model.updateAnimation(this.facingRight);
	}
	
	// getter for the ability to super jump
	public boolean canSuperJump() {
		return(this.ableToSuperJump);
	}
	
	// wrapper for the loadTextureAndAdapt method in model to update the Bounding Box accordingly
	public void loadTextureAndAdapt(String filename) {
		this.model.loadTextureAndAdapt(filename);
		this.initializeBBSize();
	}
	
	// wrapper for the loadAnimationAndAdapt method in model to update the Bounding Box accordingly
	public void loadAnimationAndAdapt(String filename, int steps, int animations) {
		this.model.loadAnimationAndAdapt(filename, steps, animations);
		this.initializeBBSize();
	}

	
	// method to get the model of the entity
	public IModel getModel() {
		return(this.model);
	}
	
	// method for initializing the bounding box depending on the model size
	private void initializeBBSize() {
		List<Float> bbSize = this.model.calculateBoundingBox();

		this.bbW = bbSize.get(0);
		this.bbH = bbSize.get(1);
	}
	
	// wrapper for the setScale method in model and updating the Bounding Box accordingly
	public void setScale(float scale) {
		this.model.setScale(scale);
		
		this.bbW *= scale;
		this.bbH *= scale;
	}
	
	// getters and setters for the remove
	public boolean isToRemove() {
		return(this.toRemove);
	}
	public void setToRemove() {
		this.toRemove = true;
	}
}
