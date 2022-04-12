package com.test;

import java.util.ArrayList;
import java.util.List;

public class Mixer {
	List<Sound> songs;
	private int playingSong;
	
	public Mixer() {
		this.songs = new ArrayList<Sound>();
		this.playingSong = 0;
	}
	
	public int uploadSong(String filepath, boolean loop) {
		Sound sound = new Sound(filepath, loop);
		this.songs.add(sound);
		return(this.songs.size() - 1);
	}
	
	public void playSong(int index) {
		for (int i = 0; i < this.songs.size(); i++) {
			if (i == index) {
				this.songs.get(i).play();
			} else {
				this.songs.get(i).stop();
			}	
		}
		
		this.playingSong = index;
	}
	
	public int playingSong() {
		return(this.playingSong);
	}
}
