package com.test;

import java.util.List;

import org.joml.Vector4f;

public class Entity {
	public Model model;
	private String name;
	
	private float mass;
	private boolean canCollide;
	
	private float forceX;
	private float forceY;
	
	private float accelerationX;
	private float accelerationY;
	
	private float velocityX;
	private float velocityY;
	
	private float maxForceX;
	private float maxForceY;
	
	private float airFriction;
	
	public Entity() {
		this.model = new Model();
		this.canCollide = true;
		this.mass = 50.0f;
		this.forceX = 0;
		this.forceY = 0;
		
		this.accelerationX = 0;
		this.accelerationY = 0;
		
		this.velocityX = 0;
		this.velocityY = 0;
		
		this.maxForceX = 3.0f;
		this.maxForceY = 3.0f;
		
		this.airFriction = 5f;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return(this.name);
	}
	
	public void checkCollision(List<Entity> entityBuffer) {
		List<Vector4f> entityBB = this.model.calculateBoundingBox();
		
		int contacts = 0;
		
//		System.out.println(this.name + ": (" + entityBB.get(0).x + ", " + entityBB.get(0).y + ")");
		
		for (int i = 0; i < entityBuffer.size(); i++) {
			
			if (this != entityBuffer.get(i) && entityBuffer.get(i).canCollide()) {
				List<Vector4f> objectBB = entityBuffer.get(i).model.calculateBoundingBox();
				
//				System.out.println("Calculating " + this.name + " vs " + entityBuffer.get(i).getName());
				
				if (((entityBB.get(0).x >= objectBB.get(2).x && entityBB.get(0).x < objectBB.get(0).x) ||
					( entityBB.get(2).x >= objectBB.get(2).x && entityBB.get(2).x < objectBB.get(0).x)) &&
					((entityBB.get(0).y >= objectBB.get(2).y && entityBB.get(0).y < objectBB.get(0).y) ||
					( entityBB.get(2).y >= objectBB.get(2).y && entityBB.get(2).y < objectBB.get(0).y))) {
					
					contacts++;
					
					if (contacts <= 1) {
						List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox();
						
						if ((prevEntityBB.get(0).x >= objectBB.get(2).x && prevEntityBB.get(0).x < objectBB.get(0).x) ||
							(prevEntityBB.get(2).x >= objectBB.get(2).x && prevEntityBB.get(2).x < objectBB.get(0).x)) {
							this.model.rollbackPosition(this.model.getX(), this.model.getPrevY());
						}
						
						else if ((prevEntityBB.get(0).y >= objectBB.get(2).y && prevEntityBB.get(0).y < objectBB.get(0).y) ||
								 (prevEntityBB.get(2).y >= objectBB.get(2).y && prevEntityBB.get(2).y < objectBB.get(0).y)) {
							this.model.rollbackPosition(this.model.getPrevX(), this.model.getY());
						}
						
						else {
							this.model.rollbackPosition(this.model.getPrevX(), this.model.getPrevY());
						}
					} else {
						this.model.rollbackPosition(this.model.getPrevX(), this.model.getPrevY());
					}
					
					
					
					entityBB = this.model.calculateBoundingBox();
					
					
					
//					System.out.println("Calculating Collision for: " + this.name);
					System.out.println(this.name + " collision with " + entityBuffer.get(i).getName());
					
					if (this.name == "player") {
						System.out.println("Contacts: " + contacts);
					}
//					System.out.println("Rolling back to: (" + this.model.getPrevX() + ", " + this.model.getPrevY() + ")");
					
//					this.model.setPosition(this.model.getPrevX(), this.model.getPrevY());
				}
//				System.out.println(this.name + ": " + objectBB.x + " " + objectBB.y);
			}
		}
	}
	
	public void setCollision(boolean flag) {
		this.canCollide = flag;
	}
	
	public boolean canCollide() {
		return(this.canCollide);
	}
	
	public void applyForce(float x, float y) {
//		this.forceX += x;
//		
//		if (this.forceX > this.maxForceX) {
//			this.forceX = this.maxForceX;
//		}
//		else if (this.forceX < -this.maxForceX) {
//			this.forceX = -this.maxForceX;
//		}
//		
//		this.forceY += y;
//		
//		if (this.forceY > this.maxForceY) {
//			this.forceY = this.maxForceY;
//		}
//		else if (this.forceY < -this.maxForceY) {
//			this.forceY = -this.maxForceY;
//		}
		
		this.forceX = x;
		this.forceY = y;
	}
	
	public void applyForcePolar(float r, float teta) {
		this.forceX = r * (float)Math.cos(teta);
		this.forceY = r * (float)Math.sin(teta);
	}
	
	public void calculatePosition() {
		// RETHINK ABOUT THIS
	
		
//		if (Math.abs(this.forceX) <= this.airFriction) {
//			this.forceX = 0;
//		}
//		else {
//			if (this.forceX > 0) {
//				this.forceX -= this.airFriction;
//			} else if (this.forceX < 0) {
//				this.forceX += this.airFriction;
//			}
//		}
//		
//		if (Math.abs(this.forceY) <= this.airFriction) {
//			this.forceY = 0;
//		}
//		else {
//			if (this.forceY > 0) {
//				this.forceY -= this.airFriction;
//			} else if (this.forceY < 0) {
//				this.forceY += this.airFriction;
//			}
//		}
//		
//		System.out.println(this.name + " force: (" + this.forceX + ", " + this.forceY + ")");
//		
//		this.accelerationX += this.forceX / this.mass;
//		this.accelerationY += this.forceY / this.mass;
//		
//		if (accelerationX > 0) {
//			accelerationX -= this.airFriction;
//		}
//		else if (accelerationX < 0) {
//			accelerationX += this.airFriction;
//		}
//		
//		if (accelerationY > 0) {
//			accelerationY -= this.airFriction;
//		}
//		else if (accelerationY < 0) {
//			accelerationY += this.airFriction;
//		}
		
//		float totalForceX = this.forceX + (this.accelerationX * this.mass);
//		float totalForceY = this.forceY + (this.accelerationY * this.mass);
		
//		float totalForceX = this.forceX + (this.airFriction * (-this.velocityX));
//		float totalForceY = this.forceY + (this.airFriction * (-this.velocityY));
		
//		float totalForceX = this.accelerationX * this.mass + this.forceX + (this.airFriction * ((-this.accelerationX) * this.mass));
		
		float totalForceX = this.forceX + (this.airFriction * (-this.velocityX));
		float totalForceY = this.forceY + (this.airFriction * (-this.velocityY));
		
		this.accelerationX = totalForceX / this.mass;
		this.accelerationY = totalForceY / this.mass;

		
//		this.accelerationX += this.airFriction * (-this.accelerationX);
//		this.accelerationY += this.airFriction * (-this.accelerationY);
		
		
//		if (this.accelerationX > 0) {
//			this.accelerationX -= this.airFriction * (-this.accelerationX);
//		} else if (this.accelerationY < 0) {
//			this.accelerationX += this.airFriction * this.accelerationX;
//		}
		
		this.velocityX += accelerationX;
		this.velocityY += accelerationY;
		
//		System.out.println(this.name + " applied force: (" + this.forceX + ", " + this.forceY + ")");
//		System.out.println(this.name + " force: (" + totalForceX + ", " + totalForceX + ")");
//		System.out.println(this.name + " acceleration: (" + this.accelerationX + ", " + this.accelerationY + ")");
//		System.out.println(this.name + " velocity: (" + this.velocityX + ", " + this.velocityY + ")");
		
		float newPositionX = this.model.getX() + this.velocityX;
		float newPositionY = this.model.getY() + this.velocityY;
		
//		System.out.println(this.name + ": (" + newPositionX + ", " + newPositionY + ")");
		
		this.model.setPosition(newPositionX, newPositionY);
		
//		this.model.setPosition(this.model.getX() + this.velocityX, this.model.getY() + this.velocityY);
	}

}
