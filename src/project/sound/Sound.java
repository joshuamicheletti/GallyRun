package project.sound;

import static org.lwjgl.system.MemoryStack.*;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.*;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcGetString;

// class that implements a sound object, to store and reproduce sound files
public class Sound implements ISound {
	private int bufferID;
	private int sourceID;
	private String filepath;
	private boolean isPlaying;

	// Constructor
	public Sound(String filepath, boolean loops) {
		// keep track of the location of the audio file
		this.filepath = filepath;
		// initialize the sound to not be playing
		this.isPlaying = false;
		
		// Allocate space to store the return info from stb
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);
		// load the file into a ShortBuffer
		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(this.filepath, channelsBuffer, sampleRateBuffer);
		// check that there were no errors while loading the sound
		if (rawAudioBuffer == null) {
			System.out.println("Could not load sound: " + filepath);
			stackPop();
			stackPop();
			return;
		}
		// gather info about the amount of channels in the file and the sample rate
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		// remove the data from the stack
		stackPop();
		stackPop();
		
		int format = -1;
		// set different formats depending on the amount of channels (1 = mono, 2 = stereo)
		if (channels == 1) {
			format = AL_FORMAT_MONO16;
		} else if (channels == 2) {
			format = AL_FORMAT_STEREO16;
		}
		
		// if there is an audio device available
		if (alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER) != null) {
			// create an OpenAL audio buffer
			this.bufferID = alGenBuffers();
			// put the audio data in the buffer
			alBufferData(this.bufferID, format, rawAudioBuffer, sampleRate);
			
			// create a source object for the audio
			this.sourceID = alGenSources();
			// set the parameters for the audio source
			alSourcei(this.sourceID, AL_BUFFER, this.bufferID);  // load the audio file
			alSourcei(this.sourceID, AL_LOOPING, loops ? 1 : 0); // decide whether it should loop or not
			alSourcei(this.sourceID, AL_POSITION, 0);			 // point where to start the audio from
			alSourcef(this.sourceID, AL_GAIN, 0.3f); 			 // volume
		}		
	}
	
	// method to delete an audio source
	public void delete() {
		alDeleteSources(this.sourceID);
		alDeleteBuffers(this.bufferID);
	}
	
	// method to play an audio source
	public void play() {
		// if there are available audio devices
		if (alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER) != null) {
			// get the current state of the audio source
			int state = alGetSourcei(this.sourceID, AL_SOURCE_STATE);
			// if the audio is paused
			if (state == AL_STOPPED) {
				this.isPlaying = false; // notify that it was stopped
				alSourcei(this.sourceID, AL_POSITION, 0); // rewind the audio to the beginning
			}
			// if the audio isn't playing
			if (!this.isPlaying) {
				// play the audio
				alSourcePlay(this.sourceID);
				this.isPlaying = true;
			}
		}
	}
	
	// method to play an audio source from the beginning, regardless of its state
	public void playRaw() {
		if (alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER) != null) {
			alSourcei(this.sourceID, AL_POSITION, 0);
			alSourcePlay(this.sourceID);
		}
	}
	
	// method to stop an audio source
	public void stop() {
		if (alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER) != null) {
			if (this.isPlaying) {
				alSourceStop(this.sourceID);
				this.isPlaying = false;
			}
		}
	}
	
	// getter for the filepath
	public String getFilepath() {
		return(this.filepath);
	}
	
	// getter to check if the audio is playing
	public boolean isPlaying() {
		if (alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER) != null) {
			// check the state of the audio source
			int state = alGetSourcei(this.sourceID, AL_SOURCE_STATE);
			if (state == AL_STOPPED) {
				this.isPlaying = false;
			}
		}
		return(this.isPlaying);
	}
	
	// setter to set the volume of the audio source
	public void setVolume(float volume) {
		if (alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER) != null) {
			alSourcef(this.sourceID, AL_GAIN, volume); // volume
		}
	}
}
