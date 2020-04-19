package theGhastModding.meshingTest.sound;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import theGhastModding.meshingTest.object.Camera;
import theGhastModding.meshingTest.util.FileChannelInputStream;

public class SoundEngine {
	
	private long device;
	
	private Map<String, IntBuffer> w = new HashMap<String, IntBuffer>();
	private List<AudioSource> sources = new ArrayList<AudioSource>();
	
	public static SoundEngine current;
	
	public SoundEngine() throws Exception {
		/*String s = ALC10.alcGetString(0, ALC11.ALC_ALL_DEVICES_SPECIFIER);
		System.out.println(s);
		System.exit(0);*/
		
		
		this.device = ALC10.alcOpenDevice((ByteBuffer)null);
		
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        
        int[] l = new int[16];
        l[0] = ALC10.ALC_REFRESH;
        l[1] = 60;
        l[2] = ALC10.ALC_SYNC;
        l[3] = ALC10.ALC_FALSE;
        l[4] = 0;
        
        long newContext = ALC10.alcCreateContext(device, l);
        
        if(!ALC10.alcMakeContextCurrent(newContext)) {
        	throw new Exception("Failed to make context current");
        }
        
        AL.createCapabilities(deviceCaps);
        
        AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f);
        AL10.alListenerfv(AL10.AL_ORIENTATION, new float[]{0f, 0f, -1f});
        AL10.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
        AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
        current = this;
	}
	
	public void registerSource(File f, String name) {
		try {
			AL10.alGetError();
			IntBuffer buffer = BufferUtils.createIntBuffer(1);
			AL10.alGenBuffers(buffer);
			int alerr = AL10.alGetError();
			if(alerr != AL10.AL_NO_ERROR) {
				System.err.println("AL ERROR! " + alerr);
				return;
			}
			
			FileInputStream fis = new FileInputStream(f);
			FileChannelInputStream in = new FileChannelInputStream(fis.getChannel());
			
			byte[] allBytes = new byte[(int)f.length()];
			in.read(allBytes);
			in.close();
			fis.close();
			
			ByteArrayInputStream b = new ByteArrayInputStream(allBytes);
			WaveData waveFile = WaveData.create(b);
			b.close();
			AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
			w.put(name, buffer);
		}catch(Exception e) {
			System.err.println("Error registering audio source " + name + ": ");
			e.printStackTrace();
		}
	}
	
	public synchronized int playSound(String name, Vector3f loc, boolean loop) {
		if(w.get(name) == null) {
			return -1;
		}
		
		int source = AL10.alGenSources();
		
		AL10.alSourcei(source, AL10.AL_BUFFER, w.get(name).get(0));
		AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f);
        AL10.alSource3f(source, AL10.AL_POSITION, loc.x, loc.y, loc.z);
        
        AL10.alSourcei(source, AL10.AL_SOURCE_RELATIVE, AL10.AL_FALSE);
        AL10.alSourcef(source, AL10.AL_PITCH, 1);
        AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, 25f);
        AL10.alSourcef(source, AL10.AL_GAIN, 0.15f);
        AL10.alSourcef(source, AL10.AL_MAX_GAIN, 1f);
        AL10.alSourcei(source, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
        
        AL10.alSourcePlay(source);
        AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f);
        
        sources.add(new AudioSource(source, loc));
        
        return source;
	}
	
	public void stopSound(int sourceID) {
		//AL10.alSourceStop(sourceID);
		AL10.alSourcePause(sourceID);
	}
	
	public void disposeSound(int sourceID) {
		AL10.alSourceStop(sourceID);
		AL10.alDeleteSources(sourceID);
	}
	
	public void cleanUp() {
		ALC10.alcCloseDevice(device);
	}
	
	public synchronized void update(Camera c) {
		AL10.alListener3f(AL10.AL_POSITION, c.getPosition().x, c.getPosition().y, c.getPosition().z);
		AL10.alListenerfv(AL10.AL_ORIENTATION, new float[]{c.getPitch(), c.getYaw(), c.getRoll()});
		
		for(AudioSource as:sources) {
			double dist = Math.sqrt(Math.pow(c.getPosition().x - as.loc.x, 2) + Math.pow(c.getPosition().y - as.loc.y, 2) + Math.pow(c.getPosition().z - as.loc.z, 2));
			if(dist >= 32) {
				if(as.playing) {
					stopSound(as.sourceID);
					as.playing = false;
				}
			}else {
				if(!as.playing) {
					AL10.alSourcePlay(as.sourceID);
					as.playing = true;
				}
			}
		}
	}
	
	public static class AudioSource {
		
		private int sourceID;
		private Vector3f loc;
		public boolean playing = true;
		
		public AudioSource(int sourceID, Vector3f loc) {
			this.sourceID = sourceID;
			this.loc = loc;
		}
		
	}
	
}