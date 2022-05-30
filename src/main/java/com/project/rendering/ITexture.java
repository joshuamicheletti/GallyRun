package com.project.rendering;

public interface ITexture {
	public void loadImage(String filename);
	public void bind(int sampler);
	
	public int getWidth();
	public int getHeight();
	public int[] getPixels();
}
