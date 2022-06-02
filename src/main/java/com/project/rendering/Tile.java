package com.project.rendering;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;

// class that implements a tileSet, which is a collection of tiles (models)
public class Tile extends Model {
	// method for loading a tileset
	public void loadTileSet(String texture) {
		this.tex.loadImage(texture);
	}
	
	// method for moving through the tile set to render different tiles
	public void changeTileUV(int tile, int w, int h) {
		// calculate the position of the tile in the tilemap
		int positionX = tile % w;
		int positionY = (int)Math.floor((float)tile / (float)w);
	
		// calculating the UVs corresponding to the selected tile position
		float[] textureUV;		
		textureUV = new float[] {										   
				(1f / w) *  positionX,      (1f / h) * (positionY),
				(1f / w) * (positionX + 1), (1f / h) * (positionY),
				(1f / w) *  positionX,      (1f / h) * (positionY + 1),
				(1f / w) * (positionX + 1), (1f / h) * (positionY + 1)
		};

		// replace the UV buffer with the new UV coordinates
		glBindBuffer(GL_ARRAY_BUFFER, this.TEXVBOid);
		glBufferData(GL_ARRAY_BUFFER, this.createBuffer(textureUV), GL_STATIC_DRAW);
	}
}
