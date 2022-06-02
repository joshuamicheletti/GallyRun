package com.project.rendering;

public interface ITexture {
	// method to load an image to the texture
	public void loadImage(String filename);
	// method to bind a texture to be used in the slot defined by "sampler"
	public void bind(int sampler);
	// getters
	public int getWidth();
	public int getHeight();
	public int[] getPixels();
}
