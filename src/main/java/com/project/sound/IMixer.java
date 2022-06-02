package com.project.sound;

public interface IMixer {
	public int uploadSong(String filepath, boolean loop); // method for uploading a new song to the mixer
	public void playSong(int index); // method for playing a song specified by the index
	public int playingSong(); // method for getting the index of the song that is currently playing
}
