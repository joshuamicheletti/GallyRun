package com.test;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Texture {
	private int id;
	private int width;
	private int height;
	private int[] rawPixels;
	
	public Texture() {
		this.id = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, this.id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void bind(int sampler) {
		if (sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			glBindTexture(GL_TEXTURE_2D, id);
		}
	}
	
	public void loadImage(String filename) {
		BufferedImage bi;
		
		try {
			bi = ImageIO.read(new File(filename));
			this.width = bi.getWidth();
			this.height = bi.getHeight();
			
			this.rawPixels = new int[width * height * 4];
			
			this.rawPixels = bi.getRGB(0, 0, width, height, null, 0, width);
			
			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
			
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					int pixel = this.rawPixels[i * width + j];
					
					pixels.put((byte)((pixel >> 16) & 0xFF)); // red
					pixels.put((byte)((pixel >>  8) & 0xFF)); // green
					pixels.put((byte)((pixel >>  0) & 0xFF)); // blue
					pixels.put((byte)((pixel >> 24) & 0xFF)); // alpha
					
					
//					System.out.println("(" + ((pixel >> 16) & 0xFF) + ", " + ((pixel >> 8) & 0xFF) + ", " + ((pixel >> 0) & 0xFF) + ", " + ((pixel >> 24) & 0xFF) + ")");
				}
			}
			
			pixels.flip();
			
			glBindTexture(GL_TEXTURE_2D, this.id);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
