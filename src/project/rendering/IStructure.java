package project.rendering;

import java.util.List;

import project.entities.Hitbox;

public interface IStructure {
	// getters
	public int[][] getTiles();
	public List<Hitbox> getHitboxes();
	
	// method for loading a structure from a file. if a size is given, the method will also create a hitbox for the structure
	// the size represents the size of the tile
	public void loadStructure(String file, int size);
	
	// method for applying a structure in position (x, y) to a world.
	// if a list of hitboxes is given, it will load the hitboxes of the structure into the list
	public void applyStructure(int x, int y, int[][] world, List<Hitbox> worldHitboxes);

}
