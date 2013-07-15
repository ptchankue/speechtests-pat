package com.example.speechtestspat.speech;

import java.util.HashMap;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

public class TTS implements TextToSpeech.OnInitListener{
	private TextToSpeech 			tts;
	Context 						c;
	InteractionCompletedEvent 		event;
	HashMap<String, String> 		myHashAlarm;
	OnUtteranceCompletedListener 	ouct;
	static String 					TAG = "TTS";


	public TTS(Context context, InteractionCompletedEvent event)
	{
		c = context;
		tts = new TextToSpeech(c, this);
		this.event = event;
		//mAudioManager
		//tts.setPitch((float) 2.0);
	}

	public void say(String words)
	{
		Log.i(TAG, "Say " + words);
		Intent intent = new Intent();
		intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

		myHashAlarm = new HashMap<String, String>();
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
		tts.speak(words, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@Override
	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS) 
		{
			Log.i(TAG, "TTS initialisation succeeded");
			if(tts.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
				tts.setLanguage(Locale.US);

			tts.setOnUtteranceProgressListener(new UtteranceProgressListener(){
				
				@Override
				public void onDone(String arg0) {
					event.onDoneSpeaking();

				}

				@Override
				public void onError(String utteranceId) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStart(String utteranceId) {
					event.onStartSpeaking();

				}

			});

		}
		else
		{
			Log.i(TAG, "TTS failed!");
		}
	}

	public void stop()
	{
		tts.stop();
	} 
	public void destroy()
	{
		tts.shutdown();
	} 
	public void setPitch(int pitch){
		tts.setPitch(pitch);
	}
}