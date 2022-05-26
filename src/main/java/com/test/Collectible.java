package com.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;


public class Collectible extends Entity {
	public Collectible() {
		this.model.setScale(0.25f);
		this.setGravity(0);
		this.model.setAnimationSpeed(10f);
	}
	
	
//	public void checkCollision(List<Entity> entityBuffer, List<Hitbox> worldHitboxes) {
//		if (this.canCollide) {
//			this.newPositionX = this.model.getX();
//			this.newPositionY = this.model.getY();	
//		
//			List<Vector4f> entityBB = this.model.calculateBoundingBox();
//			
//			float sizeX = Math.abs(entityBB.get(0).x - entityBB.get(2).x);
//			float sizeY = Math.abs(entityBB.get(0).y - entityBB.get(2).y);
//			
//			this.airborne = true;
//			
//			Collections.sort(worldHitboxes, new Comparator<Hitbox>() {
//				public int compare(Hitbox first, Hitbox second) {
//					
//					float dist1 = Math.abs(model.getX() - first.getCenterX());
//					float dist2 = Math.abs(model.getX() - second.getCenterX());
//					
//					return(dist1 == dist2 ? 0 : dist1 < dist2 ? -1 : 1);
//				}
//			});
//			
//			this.ableToSuperJump = false;
//			
//			for (int i = 0; i < worldHitboxes.size(); i++) {				
//				Vector2f objectBB0 = new Vector2f(worldHitboxes.get(i).getX0(), worldHitboxes.get(i).getY0());
//				Vector2f objectBB2 = new Vector2f(worldHitboxes.get(i).getX2(), worldHitboxes.get(i).getY2());
//				
//				if (entityBB.get(0).x > objectBB2.x && // LEFT
//					entityBB.get(2).x < objectBB0.x && // RIGHT
//					entityBB.get(2).y < objectBB0.y && // TOP
//					entityBB.get(0).y > objectBB2.y) { // BOTTOM
//
//					List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox();
//					
//					if (prevEntityBB.get(0).x < objectBB2.x) { // LEFT
//						this.newPositionX = objectBB2.x - (sizeX / 2) - 0.1f;
//						this.velocityX = 0;
//					} else if (prevEntityBB.get(2).x > objectBB0.x) { // RIGHT
//						this.newPositionX = objectBB0.x + (sizeX / 2) + 0.1f;
//						this.velocityX = 0;
//					} else if (prevEntityBB.get(2).y > objectBB0.y) { // TOP
//						if (worldHitboxes.get(i).getSpecialJump()) {
//							this.ableToSuperJump = true;
//						}
//						this.newPositionY = objectBB0.y + (sizeY / 2) + 0.1f;
//						this.velocityY = 0;
//						this.airborne = false;
//					} else if (prevEntityBB.get(0).y < objectBB2.y) { // BOTTOM
//						this.newPositionY = objectBB2.y - (sizeY / 2) - 0.1f;
//						this.velocityY = 0;
//					}
//					
//					this.model.rollbackPosition(this.newPositionX, this.newPositionY);
//					
//					entityBB = this.model.calculateBoundingBox();
//				}
//			}
//				
//			for (int i = 0; i < entityBuffer.size(); i++) {				
//				if (entityBuffer.get(i) != this && entityBuffer.get(i).canCollide && !(entityBuffer.get(i) instanceof Collectible || entityBuffer.get(i) instanceof Enemy)) {
//					
//					List<Vector4f> objectBB = entityBuffer.get(i).model.calculateBoundingBox();
//					
//					if (entityBB.get(0).x > objectBB.get(2).x && // LEFT
//						entityBB.get(2).x < objectBB.get(0).x && // RIGHT
//						entityBB.get(2).y < objectBB.get(0).y && // TOP
//						entityBB.get(0).y > objectBB.get(2).y) { // BOTTOM
//
//						List<Vector4f> prevEntityBB = this.model.calculatePrevBoundingBox();
//						
//						if (prevEntityBB.get(0).x < objectBB.get(2).x) { // LEFT
//							this.newPositionX = objectBB.get(2).x - (sizeX / 2) - 0.1f;
//							this.velocityX = 0;
//						} else if (prevEntityBB.get(2).x > objectBB.get(0).x) { // RIGHT
//							this.newPositionX = objectBB.get(0).x + (sizeX / 2) + 0.1f;
//							this.velocityX = 0;
//						} else if (prevEntityBB.get(2).y > objectBB.get(0).y) { // TOP
//							this.newPositionY = objectBB.get(0).y + (sizeY / 2) + 0.1f;
//							this.velocityY = 0;
//							this.airborne = false;
//						} else if (prevEntityBB.get(0).y < objectBB.get(2).y) { // BOTTOM
//							this.newPositionY = objectBB.get(2).y - (sizeY / 2) - 0.1f;
//							this.velocityY = 0;
//						}
//						
//						this.model.rollbackPosition(this.newPositionX, this.newPositionY);
//						
//						entityBB = this.model.calculateBoundingBox();
//					}
//				}
//			}
//		}
//	}
	
	public void applyEffect(Player player) {
		System.out.println("Apply an effect to the player");
		this.setToRemove();
	}
}
