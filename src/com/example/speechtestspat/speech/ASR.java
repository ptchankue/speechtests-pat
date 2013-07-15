package com.example.speechtestspat.speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.speechtestspat.SpeechMainActivity;
import com.example.speechtestspat.utils.Logger;
import com.example.speechtestspat.utils.Sound;


public class ASR  implements RecognitionListener {

	//Purpose of small class - its going to get worse.

	private SpeechMainActivity 			context;
	private HashMap<String, String> 	soundSettings;
	public SpeechRecognizer 			recogniser;
	public Sound 						sound;
	private InteractionCompletedEvent	event;
	private ArrayList<String>			mResults;
	private String 						mError = "";
	public boolean 						mReady;
	private static long 				SPEECH_TIMEOUT = 7 * 1000;
	private Timer 						speechTimeout;
	private AudioManager				mAudioManager;
	private int							volume;
	private static String 				TAG = "ASR";
	
	private Logger						log = new Logger();
	private String 						filename = "speechtest.txt";
	
	public ASR(SpeechMainActivity context, InteractionCompletedEvent event)
	{
		this.context = context;
		this.event = event;

		soundSettings = new HashMap<String, String>();
		soundSettings.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
				String.valueOf(AudioManager.STREAM_ALARM)); //Pass ALL of the settings?
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		sound = new Sound(context);
		volume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		volumeMax();
		sound.beep();
		startSpeechRecognition();
	}

	public void destroy()
	{
		Log.v(TAG, "Goodbye from ASR");
		mute(false);
		if(recogniser != null)
			recogniser.destroy();
		if(sound != null)
			if(sound.soundPool!= null){
				sound.beep();
				sound.soundPool.release();
			}
				
		//Put back the volume to normal
		mAudioManager.setStreamVolume(mAudioManager.STREAM_MUSIC, volume, 0);
	}

	
	public ArrayList<String> getResults(){
		return mResults;
	}

	//Run from the main thread :)
	public void startSpeechRecognition(){
		context.runOnUiThread(new Runnable() {
			public void run() {
				startSpeechIntent();
			}
		});

	}
	//Run from the main thread :)
	public void stopSpeechRecognition(){
		context.runOnUiThread(new Runnable() {
			public void run() {
				if(recogniser != null){
					recogniser.cancel();
					recogniser.stopListening();
				}
			}
		});
		
	}

	public void startSpeechIntent()
	{
		Log.i(TAG, "Initialise speech");

		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
		"za.ac.nmmu.mimic");

		if(recogniser == null) {
			recogniser = SpeechRecognizer
			.createSpeechRecognizer(context.getApplicationContext());
			recogniser.cancel();
			recogniser.setRecognitionListener(this);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			mute(true);
		}

		recogniser.startListening(intent);
	}

	// to ensure that the speech engine is always on
	public class silenceTimer extends TimerTask {
		public void run() {
			onError (SpeechRecognizer.ERROR_SPEECH_TIMEOUT);
		}
	}
	public void mute(boolean b){
		//Log.i(TAG, "mute() " + b);
		mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, b);
	}
	
	private void volumeMax(){
		int amStreamMusicMaxVol = mAudioManager.getStreamMaxVolume(mAudioManager.STREAM_MUSIC);
		// take 3 quater of the max volume
		amStreamMusicMaxVol /=.5;
		mAudioManager.setStreamVolume(mAudioManager.STREAM_MUSIC, amStreamMusicMaxVol, 0);
		Log.i(TAG,"Volume " + amStreamMusicMaxVol);
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		Log.v(TAG, "Adjust volume");
	}
	@Override
	public void onResults(Bundle results) {
		Log.d(TAG, "onResults");

		processResults(results);

		// Continuously listening
		startSpeechIntent();
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		Log.d(TAG, "onPartialResults");

		processResults(partialResults);
		// Continuously listening
		startSpeechIntent();
	}

	private void processResults(Bundle results) {
		mResults = new ArrayList<String>();
		ArrayList<String> voiceResults = results
		.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		if (voiceResults == null) {
			Log.e(TAG, "No voice results");
			sound.error();
		} else {
			sound.beep();
			Log.d(TAG, "Printing matches: ");
			log.appendLog("Printing matches: ", filename);
			for (String match : voiceResults) {
				Log.d(TAG, match);
				log.appendLog(match, filename);
			}

			mResults = voiceResults;
			event.onResultAvailable();
		}
		

	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		Log.d(TAG, "Ready for speech");
		/*if (mError.equalsIgnoreCase(""))
			sound.ready();*/
		mReady = true;

		speechTimeout = new Timer();
		speechTimeout.schedule(new silenceTimer(), SPEECH_TIMEOUT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			mute(false);
		}

	}

	public void onError(int error) {
		Log.d(TAG,"Error listening for speech: " + error);

		if (error != 5 && error != 6 && error != 8)
			sound.error();

		boolean restart = true;
		mReady = false;
		switch (error) {

		case SpeechRecognizer.ERROR_CLIENT: 
			mError = " error client"; 
			restart = false;
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: 
			mError = " insufficient permissions" ; 
			restart = false;
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:				
			mError = " network timeout"; 
			break;
		case SpeechRecognizer.ERROR_NETWORK: 
			mError = " network" ;
			return;
		case SpeechRecognizer.ERROR_AUDIO: 
			mError = " audio"; 
			break;
		case SpeechRecognizer.ERROR_SERVER: 
			mError = " server"; 
			break;

		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: 
			mError = " speech time out" ; 
			break;
		case SpeechRecognizer.ERROR_NO_MATCH: 
			mError = " no match" ; 
			//sound.error();
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: 
			mError = " recogniser busy" ; 
			recogniser.destroy();
			recogniser = null;
			break;

		}
		Log.i(TAG,  "Error: " +  error + " - " + mError);
		log.appendLog("Error: " + mError, filename);
 //mError = "";
		if( restart) {
			startSpeechRecognition();
		}
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.d(TAG, "Speech starting");

		speechTimeout.cancel();
		mResults = null;

	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub
		//Log.d(TAG, "Buffer Received: " + buffer.length);
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onEndofSpeech");
		//sound.beep();

	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onEvent");

	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub
		//Log.d(TAG, "onRMSChanged");

	}


}
