package com.test;

import static org.lwjgl.system.MemoryStack.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.*;

import static org.lwjgl.openal.AL10.*;

//import static org.lwjgl.system.MemoryAccessJNI.free;

public class Sound {
	private int bufferID;
	private int sourceID;
	private String filepath;
	private boolean isPlaying;
	
	
	public Sound(String filepath, boolean loops) {
		this.filepath = filepath;
		this.isPlaying = false;
		
		// Allocate space to store the return info from stb
		
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);
		
		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(this.filepath, channelsBuffer, sampleRateBuffer);
		
		if (rawAudioBuffer == null) {
			System.out.println("Could not load sound: " + filepath);
			stackPop();
			stackPop();
			return;
		}
		
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		
		stackPop();
		stackPop();
		
		int format = -1;
		
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}
		
		// sound data
		this.bufferID = alGenBuffers();
		alBufferData(this.bufferID, format, rawAudioBuffer, sampleRate);
		
		this.sourceID = alGenSources();
		
		alSourcei(this.sourceID, AL_BUFFER, this.bufferID);
		alSourcei(this.sourceID, AL_LOOPING, loops ? 1 : 0);
		alSourcei(this.sourceID, AL_POSITION, 0);
		alSourcef(this.sourceID, AL_GAIN, 0.3f); // volume
		
//		free(rawAudioBuffer);
		
	}
	
	public void delete() {
		alDeleteSources(this.sourceID);
		alDeleteBuffers(this.bufferID);
	}
	
	public void play() {
		int state = alGetSourcei(this.sourceID, AL_SOURCE_STATE);
		
		if (state == AL_STOPPED) {
			this.isPlaying = false;
			alSourcei(this.sourceID, AL_POSITION, 0);
		}
		
		if (!this.isPlaying) {
			alSourcePlay(this.sourceID);
			this.isPlaying = true;
		}
	}
	
	public void playRaw() {
		alSourcei(this.sourceID, AL_POSITION, 0);
		alSourcePlay(this.sourceID);
	}
	
	public void stop() {
		if (this.isPlaying) {
			alSourceStop(this.sourceID);
			this.isPlaying = false;
		}
	}
	
	public String getFilepath() {
		return(this.filepath);
	}
	
	public boolean isPlaying() {
		int state = alGetSourcei(this.sourceID, AL_SOURCE_STATE);
		if (state == AL_STOPPED) {
			this.isPlaying = false;
		}
		
		return(this.isPlaying);
	}
	
	public void setVolume(float volume) {
		alSourcef(this.sourceID, AL_GAIN, volume); // volume
	}
}
