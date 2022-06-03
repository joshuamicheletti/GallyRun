package project.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

// class that implements a texture object for storing image files and render them
public class Texture implements ITexture {
	// variable to store the id number of the texture given by OpenGL
	private int id;
	// width of the texture
	private int width;
	// height of the texture
	private int height;
	// array containing the raw pixels of the texture
	private int[] rawPixels;
	
	// Constructor
	public Texture() {
		// generate a new texture in OpenGL
		this.id = glGenTextures();
		// bind the texture to be the current GL_TEXTURE_2D to be working on
		glBindTexture(GL_TEXTURE_2D, this.id);
		// set the texture parameters (anti-aliasing filters)
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		// unbind the texture
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	// method for binding the texture to be used as the current texture
	public void bind(int sampler) { // sampler represents the slot in GPU where the texture is gonna be loaded
		if (sampler >= 0 && sampler <= 31) { // if sampler is a valid number (between 0 and 32)
			glActiveTexture(GL_TEXTURE0 + sampler); // set the active texture to be the one on the slot "sampler"
			glBindTexture(GL_TEXTURE_2D, id); // bind the texture to be the current texture
		}
	}
	
	// load an image to be a texture
	public void loadImage(String filename) {
		BufferedImage bi;
		
		try {
			// read the file through ImageIO and load it into a BufferedImage object
			bi = ImageIO.read(new File(filename));
			// store the width and height of the image
			this.width = bi.getWidth();
			this.height = bi.getHeight();
			
			// initialize the rawPixels array with the size of the pixels (width * height * 4 (4 channels, RGBA))
			this.rawPixels = new int[width * height * 4];
			// store the raw pixels
			this.rawPixels = bi.getRGB(0, 0, width, height, null, 0, width);
			
			// create a ByteBuffer to load the pixels into an OpenGL texture
			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
			
			// scroll through the pixels
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					// get the current pixel. pixels are stored linearly, and to be accessed through rows and columns, the formula is:
					// "row * width + col"
					int pixel = this.rawPixels[i * width + j];
					// store the pixel values to the buffer
					pixels.put((byte)((pixel >> 16) & 0xFF)); // red
					pixels.put((byte)((pixel >>  8) & 0xFF)); // green
					pixels.put((byte)((pixel >>  0) & 0xFF)); // blue
					pixels.put((byte)((pixel >> 24) & 0xFF)); // alpha					
				}
			}
			// flip the buffer
			pixels.flip();
			// bind the current texture
			glBindTexture(GL_TEXTURE_2D, this.id);
			// load the pixel data to the texture
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// getter methods
	public int getWidth() {
		return(this.width);
	}
	public int getHeight() {
		return(this.height);
	}
	public int[] getPixels() {
		return(this.rawPixels);
	}

}
