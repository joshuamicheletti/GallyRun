package com.project.sound;

public interface ISound {
	public void play(); // method for playing a sound, and restarting it in case it's already playing
	public void playRaw(); // method for playing a sound regardless of its state
	public void stop(); // method for stopping a sound
	public boolean isPlaying(); // method for checking if a sound is playing
	public void delete(); // method for deleting a sound
	public String getFilepath(); // method for getting the location of the sound file loaded
	public void setVolume(float volume); // method for changing the volume of the sound
}
