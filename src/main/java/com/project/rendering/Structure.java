package com.project.rendering;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.project.entities.Hitbox;

// class that implements a structure object, which is a collection of tiles
public class Structure implements IStructure {
	// matrix for storing the tiles
	private int[][] tiles;
	// matrix for storing the effect of the tiles
	private char[][] effect;
	// list of hitboxes that make the final hitbox of the structure
	private List<Hitbox> hitboxes;
	// variable to store the size of the tiles
	private int tileSize;
	
	// Constructor
	public Structure() {
		this.tiles = null;
		this.effect = null;
		this.hitboxes = new ArrayList<Hitbox>();
	}
	
	// getters for tiles and hitboxes
	public int[][] getTiles() {
		return(this.tiles);
	}
	public List<Hitbox> getHitboxes() {
		return(this.hitboxes);
	}
	
	// method for loading a structure (file containing tiles that make up a structure)
	public void loadStructure(String file, int size) {
		try {
			// open the structure file
			File input = new File(file);
			// scan the lines of the file
			Scanner reader = new Scanner(input);
			// initialize variables to keep track of rows and columns of the structure
			int rows = 0;
			int columns = 0;
			// read every line of the file (to check the amount of rows and columns in the structure
			while (reader.hasNextLine()) {
				// store the current line of the structure
				String line = reader.nextLine();
				// split the string every "tab" to obtain the single numbers of the tiles of the string
				String[] values = line.split("\t");
				// convert the array into a list
				List<String> listValues = new LinkedList<String>(Arrays.asList(values));;
				// scroll through the list and remove any character that isn't a number
				for (int i = 0; i < listValues.size(); i++) {
					String[] components = listValues.get(i).split(" ");

					if (!isNumeric(components[0])) {
						listValues.remove(i);
						i--;
					}
				}
				// keep track of the columns in the structure
				columns = listValues.size();
				// keep track of the rows in the structure
				rows++;
			}
			// close the reader
			reader.close();
			
			// reopen the reader (to go back to the first row of the file
			reader = new Scanner(input);
			// initialize the tiles matrix that will contain the number of the tiles for the structure
			this.tiles = new int[columns][rows];
			// initialize the effects matrix that will contain the effect that each tile has in the structure
			this.effect = new char[columns][rows];
			// variable to keep track of what row we're reading
			int currentRow = 0;
			// read all the lines in the structure file again
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				// split the values of the tiles every "tab"
				String[] values = line.split("\t");
				// convert it into a list
				List<String> listValues = new LinkedList<String>(Arrays.asList(values));;
				// remove any non numeric value found in the list
				for (int i = 0; i < listValues.size(); i++) {
					// split the tile number in its components (in case it has an effect, it will have a character associated to it
					// in the structure file
					String[] components = listValues.get(i).split(" ");
					if (!isNumeric(components[0])) {
						listValues.remove(i);
						i--;
					}
					// get the updated components array
					components = listValues.get(i).split(" ");
					
					// store the tile number in the correct position of the structure matrix (opposite order of reading order)
					this.tiles[i][(rows - 1) - currentRow] = Integer.parseInt(components[0]);
					
					// if the current tile contains an effect
					if (components.length > 1) {
						// store the effect in the correct location
						this.effect[i][(rows - 1) - currentRow] = components[1].charAt(0);
					} else { // otherwise
						// store an 'n' to show that there isn't any effect being applied
						this.effect[i][(rows - 1) - currentRow] = 'n';
					}
				}
				
				// if a tile size was given, the program will create a hitbox for the structure
				// the hitbox is created by dividing the structure in rows, and creating a hitbox as wide as the
				// consecutive tiles that form that row
				if (size > 0) {
					this.tileSize = size;
					// flag to keep track of whether we're creating a new hitbox or we're just expanding an existing one
					// for example in the case of 2 or more tiles next to each other
					boolean newHitbox = false;
					// counter of consecutive tiles
					int consecutive = 0;
					// variable to keep track of in what position the starting tile for the current hitbox is
					int starting = 0;
					// initialize a new hitbox
					Hitbox hitbox = null;
					// scroll through all the columns of the tiles in the structure
					for (int i = 0; i < this.tiles.length; i++) {
						// if the tile isn't empty (!= -1)
						if (this.tiles[i][(rows - 1) - currentRow] != -1) {
							// update the counter of consecutive tiles
							consecutive++;
							// if we weren't already creating a new hitbox
							if (newHitbox == false) {
								// notify that we're creating a new hitbox
								newHitbox = true;
								// store the position of the first tile of the new hitbox
								starting = i;
							}
							// create a new hitbox
							hitbox = new Hitbox();
							// calculate the dimensions of the hitbox depending on the size of the tiles
							float width = (size * (consecutive + starting) - size / 2) -
										  (size * starting - size / 2);
							float height = (size * (rows - currentRow) - size / 2) -
										   (size * (rows - currentRow - 1) - size / 2);
							// calculate the position of the hitbox
							float positionX = (size * starting - size / 2) + width / 2;
							float positionY = (size * (rows - currentRow - 1) - size / 2) + height / 2;
							// apply these parameters to the hitbox
							hitbox.setPosition(positionX, positionY);
							hitbox.setBBHeight(height);
							hitbox.setBBWidth(width);
							// set the effect to the hitbox (in our case, the only option is the specialjump
							if (this.effect[i][(rows - 1) - currentRow] == 'J') {
								hitbox.setSpecialJump(true);
							}
						}
						// if we find an empty tile or if we reach the end of the row
						if (this.tiles[i][(rows - 1) - currentRow] == -1 || i == this.tiles.length - 1) {
							// notify that we're not creating a new hitbox
							newHitbox = false;
							// add the hitbox to the list of hitboxes that form the structure
							if (consecutive != 0) {
								this.hitboxes.add(hitbox);
							}
							// reset the variables
							consecutive = 0;
							starting = 0;
						}
					}
				}
				// update the current row counter
				currentRow++;
			}
			// close the file reader
			reader.close();
			
			
		} catch (FileNotFoundException e) { // catch a FileNotFoundException in case the file can't be loaded or found
			System.out.println("File Not Found: " + e);
		}
	}

	// method for applying a structure to the world
	public void applyStructure(int x, int y, int[][] world, List<Hitbox> worldHitboxes) {
		// scroll through the tile rows
		for (int i = x; i < this.tiles.length + x && i < world.length; i++) {
			// scroll through the tile columns
			for (int j = y; j < this.tiles[0].length + y && i < world[0].length; j++) {
				// if the tile is not empty
				if (this.tiles[i - x][j - y] != -1) {
					// store the value of the tile to the world tiles
					world[world.length / 2 + i][world[0].length / 2 + j] = this.tiles[i - x][j - y];
				}
			}
		}
		// if the hitboxes list is available
		if (worldHitboxes != null) {
			// scroll through the hitboxes
			for (int i = 0; i < this.hitboxes.size(); i++) {
				Hitbox current = this.hitboxes.get(i);		
				// reposition the current hitbox
				current.setPosition(current.getX() + (this.tileSize * x), current.getY() + (this.tileSize * y));
				// add it to the list of world hitboxes
				worldHitboxes.add(current);
			}
		}
	}
	
	// method for checking whether of not a string contains a number or not
	public static boolean isNumeric(String str) { 
		try {  
			Double.parseDouble(str);  
			return true;
		} catch(NumberFormatException e){  
			return false;  
		}  
	}
	
	
}
