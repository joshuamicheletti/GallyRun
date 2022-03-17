package com.test;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.joml.Vector4i;

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
	
	private float airFriction;
	
	private float gravity;
	
	private float newPositionX;
	private float newPositionY;
	
	private boolean airborne;
	private boolean stuck;
	
	private boolean facingRight;
	
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
		
		this.airFriction = 5f;
		
		this.gravity = 1.2f;
		
		this.newPositionX = 0;
		this.newPositionY = 0;
		
		this.airborne = false;
		this.stuck = false;
		
		this.facingRight = true;
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
		
		this.airborne = true;
		this.stuck = false;
		
		for (int i = 0; i < entityBuffer.size(); i++) {
			
			if (this != entityBuffer.get(i) && entityBuffer.get(i).canCollide()) {
				List<Vector4f> objectBB = entityBuffer.get(i).model.calculateBoundingBox();
				
				
				if (((entityBB.get(0).x >= objectBB.get(2).x && entityBB.get(0).x <  objectBB.get(0).x) ||
					( entityBB.get(2).x >= objectBB.get(2).x && entityBB.get(2).x <  objectBB.get(0).x) ||
					( entityBB.get(2).x <= objectBB.get(2).x && entityBB.get(0).x >= objectBB.get(0).x)) &&
					((entityBB.get(0).y >= objectBB.get(2).y && entityBB.get(0).y <  objectBB.get(0).y) ||
					( entityBB.get(2).y >= objectBB.get(2).y && entityBB.get(2).y <  objectBB.get(0).y) ||
					( entityBB.get(2).y <= objectBB.get(2).y && entityBB.get(0).y >= objectBB.get(0).y))) {

					contacts++;
					
					List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox();
					
					
					if ((prevEntityBB.get(0).x >= objectBB.get(2).x && prevEntityBB.get(0).x < objectBB.get(0).x) ||
						(prevEntityBB.get(2).x >= objectBB.get(2).x && prevEntityBB.get(2).x < objectBB.get(0).x) ||
						(prevEntityBB.get(2).x <= objectBB.get(2).x && prevEntityBB.get(0).x >= objectBB.get(0).x)) {
						if (prevEntityBB.get(2).y >= objectBB.get(0).y) { // COMING FROM UP
							this.newPositionY = objectBB.get(0).y + Math.abs(this.newPositionY - entityBB.get(2).y) + 0.001f;
							this.airborne = false;
						}
						else if (prevEntityBB.get(0).y <= objectBB.get(2).y) { // COMING FROM DOWN
							this.newPositionY = objectBB.get(2).y - Math.abs(entityBB.get(2).y - this.newPositionY) - 0.001f;
							this.stuck = true;				
						}
						this.velocityY = 0;
					}
					
					else if ((prevEntityBB.get(0).y >= objectBB.get(2).y && prevEntityBB.get(0).y < objectBB.get(0).y) ||
							 (prevEntityBB.get(2).y >= objectBB.get(2).y && prevEntityBB.get(2).y < objectBB.get(0).y) ||
							 (prevEntityBB.get(2).y <= objectBB.get(2).y && prevEntityBB.get(0).y >= objectBB.get(0).y)) {
						
						if (prevEntityBB.get(0).x <= objectBB.get(2).x) { // COMING FROM LEFT
							this.newPositionX = objectBB.get(2).x - Math.abs(this.model.getX() - entityBB.get(2).x) - 0.001f;
						}
						else if (prevEntityBB.get(2).x >= objectBB.get(0).x) { // COMING FROM RIGHT
							this.newPositionX = objectBB.get(0).x + Math.abs(this.model.getX() - entityBB.get(2).x) + 0.001f;
						}
						this.velocityX = 0;
					}
					
					else {
						this.newPositionX = this.model.getPrevX();
						this.newPositionY = this.model.getPrevY();
					}
				}
				
				if (objectBB.get(2).y >= entityBB.get(0).y && objectBB.get(2).y <= entityBB.get(0).y + 0.01f) {
					this.stuck = true;
				}
			}
			
			if (contacts == 0) {
				this.newPositionX = this.model.getX();
				this.newPositionY = this.model.getY();
			}
		}
	}
	
	public void setCollision(boolean flag) {
		this.canCollide = flag;
	}
	
	public boolean canCollide() {
		return(this.canCollide);
	}
	
	public void setForce(float x, float y) {
		this.forceX = x;
		this.forceY = y;
	}
	
	public void setForcePolar(float r, float teta) {
		this.forceX = r * (float)Math.cos(teta);
		this.forceY = r * (float)Math.sin(teta);
	}
	
	public void applyForce(float x, float y) {
		this.forceX += x;
		this.forceY += y;
		
		if (this.forceX > 0) {
			this.facingRight = true;
		} else if (this.forceX < 0) {
			this.facingRight = false;
		}
	}
	
	public void applyForcePolar(float r, float teta) {
		this.forceX += r * (float)Math.cos(teta);
		this.forceY += r * (float)Math.sin(teta);
		
		if (this.forceX > 0) {
			this.facingRight = true;
		} else if (this.forceX < 0) {
			this.facingRight = false;
		}
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
		float totalForceY = this.forceY + (this.airFriction * (-this.velocityY)) - (this.mass * this.gravity);
		
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
		
		this.forceX = 0;
		this.forceY = 0;
		
//		this.model.setPosition(this.model.getX() + this.velocityX, this.model.getY() + this.velocityY);
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
	
	public boolean canJump() {
		return(!this.airborne && !this.stuck);
	}

	public void updateAnimation() {
		if (this.airborne) {
			float threshold = 2f;
			
			if (this.velocityY > threshold && this.model.getFrames().size() >= 3) {
				this.model.setJumping();
			} else if (this.velocityY >= -threshold && this.velocityY <= threshold  && this.model.getFrames().size() >= 4) {
				this.model.setMidAir();
			} else if (this.velocityY < -threshold  && this.model.getFrames().size() >= 5) {
				this.model.setFalling();
			}
		} else {
			
			float threshold = 0.02f;
			
			if (this.velocityX < threshold && this.velocityX > -threshold) {
				this.model.setIdle();
			} else if (this.model.getFrames().size() >= 1){
				this.model.setRunning();
			}
		}
		
		this.model.updateAnimation(this.facingRight);
	}
	
	public void setOrientation(boolean direction) {
		this.facingRight = direction;
	}
	
	public boolean isFacingRight() {
		return(this.facingRight);
	}

}
