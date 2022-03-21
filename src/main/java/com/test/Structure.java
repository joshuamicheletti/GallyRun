package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Structure {
	private int[][] tiles;
	
	private List<Hitbox> hitboxes;
	
	private int tileSize;
	
	public Structure() {
		this.tiles = null;
		this.hitboxes = new ArrayList<Hitbox>();
	}
	
	public int[][] getTiles() {
		return(this.tiles);
	}
	
	public List<Hitbox> getHitboxes() {
		return(this.hitboxes);
	}
	
	
	
	public void loadStructure(String file) {
		try {
			File input = new File(file);
			
			Scanner reader = new Scanner(input);
			
			int rows = 0;
			int columns = 0;
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				
				String[] values = line.split(" ");
				
				List<String> listValues = new LinkedList<String>(Arrays.asList(values));;
				 
				for (int i = 0; i < listValues.size(); i++) {
					if (!isNumeric(listValues.get(i))) {
						listValues.remove(i);
						i--;
					}
				}
				
				columns = listValues.size();
				rows++;
			}
			reader.close();
			
			reader = new Scanner(input);
			
			this.tiles = new int[columns][rows];
			
			int currentRow = 0;
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				
				String[] values = line.split(" ");
				
				List<String> listValues = new LinkedList<String>(Arrays.asList(values));;
				 
				for (int i = 0; i < listValues.size(); i++) {
					if (!isNumeric(listValues.get(i))) {
						listValues.remove(i);
						i--;
					}
					this.tiles[i][(rows - 1) - currentRow] = Integer.parseInt(listValues.get(i));
				}
		
				currentRow++;
			}
			reader.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found: " + e);
		}
	}
	
	public void loadStructureWithHitbox(String file, int size) {
		this.tileSize = size;
		
		try {
			File input = new File(file);
			
			Scanner reader = new Scanner(input);
			
			int rows = 0;
			int columns = 0;
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				
				String[] values = line.split(" ");
				
				List<String> listValues = new LinkedList<String>(Arrays.asList(values));;
				 
				for (int i = 0; i < listValues.size(); i++) {
					if (!isNumeric(listValues.get(i))) {
						listValues.remove(i);
						i--;
					}
				}
				
				columns = listValues.size();
				rows++;
			}
			
			reader.close();
			
			reader = new Scanner(input);
			
			this.tiles = new int[columns][rows];
			
			int currentRow = 0;
			
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				
				String[] values = line.split(" ");
				
				List<String> listValues = new LinkedList<String>(Arrays.asList(values));;
				 
				for (int i = 0; i < listValues.size(); i++) {
					if (!isNumeric(listValues.get(i))) {
						listValues.remove(i);
						i--;
					}
					this.tiles[i][(rows - 1) - currentRow] = Integer.parseInt(listValues.get(i));
				}
				
				boolean newHitbox = false;
				
				int consecutive = 0;
				
				int starting = 0;
				
				Hitbox hitbox = null;
				
//				System.out.println("structure.length: " + structure.length);
				
				for (int i = 0; i < this.tiles.length; i++) {
					
					if (this.tiles[i][(rows - 1) - currentRow] != -1) {
//						System.out.println("Value: " + this.tiles[i][(rows - 1) - currentRow]);
						consecutive++;
//						System.out.println("Consecutive: " + consecutive);
						
						if (newHitbox == false) {
							newHitbox = true;
							starting = i;
						}
						
						hitbox = new Hitbox(size * (consecutive + starting) - size / 2,
											size * (rows - currentRow) - size / 2,
											size * starting - size / 2,
											size * (rows - currentRow - 1) - size / 2);
						
					}
					
					if (this.tiles[i][(rows - 1) - currentRow] == -1 || i == this.tiles.length - 1) {
						newHitbox = false;
						
						if (consecutive != 0) {
							this.hitboxes.add(hitbox);
//							System.out.println("Store Hitbox");
//							System.out.println("0(" + hitboxes.get(hitboxes.size() - 1).getX0() + ", " + hitboxes.get(hitboxes.size() - 1).getY0() + ")");
//							System.out.println("2(" + hitboxes.get(hitboxes.size() - 1).getX2() + ", " + hitboxes.get(hitboxes.size() - 1).getY2() + ")");
						}
						
						consecutive = 0;
						starting = 0;
					}
				}
		
				currentRow++;
			}
			
			reader.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found: " + e);
		}
	}
	
	
	public void applyStructure(int x, int y, int[][] world) {
		for (int i = x; i < this.tiles.length + x && i < world.length; i++) {
			for (int j = y; j < this.tiles[0].length + y && i < world[0].length; j++) {
				world[world.length / 2 + i][world[0].length / 2 + j] = this.tiles[i - x][j - y];
			}
		}
	}
	
	public void applyStructureWithHitbox(int x, int y, int[][] world, List<Hitbox> worldHitboxes) {
		for (int i = x; i < this.tiles.length + x && i < world.length; i++) {
			for (int j = y; j < this.tiles[0].length + y && i < world[0].length; j++) {
				world[world.length / 2 + i][world[0].length / 2 + j] = this.tiles[i - x][j - y];
			}
		}
		
		for (int i = 0; i < this.hitboxes.size(); i++) {
			Hitbox current = this.hitboxes.get(i);
			current.setX0(current.getX0() + (this.tileSize * x));
			current.setY0(current.getY0() + (this.tileSize * y));
			current.setX2(current.getX2() + (this.tileSize * x));
			current.setY2(current.getY2() + (this.tileSize * y));
//			System.out.println("SHIFTED");
//			System.out.println("0(" + current.getX0() + ", " + current.getY0() + ")");
//			System.out.println("2(" + current.getX2() + ", " + current.getY2() + ")");
			worldHitboxes.add(current);
		}
	}
	
	
	
	public static boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}
	
	
}
