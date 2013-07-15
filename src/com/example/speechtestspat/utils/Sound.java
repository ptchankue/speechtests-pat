package com.example.speechtestspat.utils;

import java.util.HashMap;

import com.example.speechtestspat.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Sound {
	private Context context;
	public SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;

	public Sound(Context context)
	{
		this.context = context;
		soundPool = new SoundPool(4, AudioManager.STREAM_RING, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(Constants.READYSOUND, 
				soundPool.load(context, R.raw.ready, Constants.READYSOUND));
		soundPoolMap.put(Constants.FAILSOUND, 
				soundPool.load(context, R.raw.buzzer1, Constants.FAILSOUND));
		soundPoolMap.put(Constants.BEEPSOUND, 
				soundPool.load(context, R.raw.beep, Constants.BEEPSOUND));
	}

	public void play(int sound) 
	{
		AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_RING);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_RING);    
		float volume = streamVolumeCurrent / streamVolumeMax;

		/* Play the sound with the correct volume */
		soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
	}

	public void error()
	{
		play(Constants.FAILSOUND);
	}

	public void ready()
	{
		play(Constants.READYSOUND);
	}

	public void beep()
	{
		play(Constants.BEEPSOUND);
	}

}
