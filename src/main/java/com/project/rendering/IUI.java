package com.project.rendering;

import com.project.entities.Player;

public interface IUI {
	public void renderUI(Camera camera, Player player);
	public void setWidth(float width);
	public void setHeight(float height);
	public void setWinTimer(int number);
}
