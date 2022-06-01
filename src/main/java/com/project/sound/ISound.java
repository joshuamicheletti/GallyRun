package com.project.sound;

public interface ISound {
	public void play();
	public void playRaw();
	public void stop();
	public boolean isPlaying();
	public void delete();
	public String getFilepath();
	public void setVolume(float volume);
}
