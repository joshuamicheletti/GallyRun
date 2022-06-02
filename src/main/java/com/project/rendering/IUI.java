package com.project.rendering;

import com.project.entities.IPlayer;

public interface IUI {
	// method for rendering the UI elements
	public void renderUI(ICamera camera, IPlayer player);
	// setters
	public void setWidth(float width);
	public void setHeight(float height);
	public void setWinTimer(int number);
}
