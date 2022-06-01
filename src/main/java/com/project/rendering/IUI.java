package com.project.rendering;

import com.project.entities.IPlayer;

public interface IUI {
	public void renderUI(ICamera camera, IPlayer player);
	public void setWidth(float width);
	public void setHeight(float height);
	public void setWinTimer(int number);
}
