package com.project.sound;

import java.util.LinkedList;
import java.util.List;

// class to implement a mixer object. its purpose is to store songs and play them
public class Mixer implements IMixer {
	// list of songs
	List<ISound> songs;
	// index in the list of the song being played
	private int playingSong;
	
	// Constructor
	public Mixer() {
		// initialize the list of songs
		this.songs = new LinkedList<ISound>();
		// at the beginning we're not playing any song
		this.playingSong = -1;
	}
	
	// method for uploading a song to the mixer
	public int uploadSong(String filepath, boolean loop) {
		// create a new sound object
		ISound sound = new Sound(filepath, loop);
		// add the object to the list
		this.songs.add(sound);
		// return the index of the song in the list
		return(this.songs.size() - 1);
	}
	
	// method for playing a song by its index
	public void playSong(int index) {
		// scroll through all the songs in the list
		for (int i = 0; i < this.songs.size(); i++) {
			if (i == index) { // play the song selected
				this.songs.get(i).play();
			} else {		  // stop all other songs
				this.songs.get(i).stop();
			}	
		}
		// store the new index of the song being played
		this.playingSong = index;
	}
	
	// method to check which song is being played
	public int playingSong() {
		return(this.playingSong);
	}
}
