package com.project.rendering;

import java.util.List;

import com.project.entities.IEntity;

public interface IEngine {
	public void loadTiles(String texture, int w, int h);
	
	public void setWindowSize(int width, int height);
	
	public double render(List<IEntity> entityBuffer, int[][] world, int[][] background);

	public void enableRender();
	
	public boolean canRender();
	
	public ICamera getCamera();
	
	public boolean getDebug();
	
	public void setDebug(boolean flag);
	
	public int getTileSize();
	
	public void setTileSize(int size);
	
	public void setWinTimer(int timer);
}
