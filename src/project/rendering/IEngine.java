package project.rendering;

import java.util.List;

import project.entities.IEntity;

public interface IEngine {
	// method for loading a tilemap
	public void loadTiles(String texture, int w, int h);
	// method for resizing the window and update all the objects connected to it
	public void setWindowSize(int width, int height);
	// method for rendering all the models (entities and tiles)
	public double render(List<IEntity> entityBuffer, int[][] world, int[][] background);
	// method to enable the engine to render
	public void enableRender();
	// method to check whether or not the engine can render a new frame
	public boolean canRender();
	
	// getters and setters
	public ICamera getCamera();
	public boolean getDebug();
	public void setDebug(boolean flag);
	public int getTileSize();
	public void setTileSize(int size);
	public IUI getUI();
}
