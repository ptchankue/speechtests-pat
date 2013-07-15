package com.example.speechtestspat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.speechtestspat.speech.ASR;
import com.example.speechtestspat.speech.InteractionCompletedEvent;
import com.example.speechtestspat.speech.TTS;
import com.example.speechtestspat.utils.Constants;

public class SpeechMainActivity extends Activity implements
InteractionCompletedEvent {

	private Intent 			serviceIntent;
	public ListView 		mList;
	private ASR 			voicerec;
	private TTS 			tts;
	private DialogueManager		mDialogue;

	public static final int DELAY = 1*1000;

	private Handler 		mHandler = new Handler();
	private Timer 			sleepTimeout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech_main);

		mList = (ListView) findViewById(R.id.list);
		((Button)findViewById(R.id.pushstart)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShortPress();
			}
		});
		ShowList(new ArrayList<String>());
		voicerec = new ASR(this, this);
		tts = new TTS(this, this);
		mDialogue = new DialogueManager(this);

		//onShortPress();

		mHandler.postDelayed(periodicTask, DELAY);

	}

	protected void onDestroy(){
		super.onDestroy();
		if(voicerec!= null)
			voicerec.destroy();
		if(tts!= null)
			tts.destroy();

		mHandler.removeCallbacks(periodicTask);
	}

	protected void onPause(){
		super.onPause();
		voicerec.mute(false);
	}

	protected void onResume(){
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speech_main, menu);
		return true;
	}

	private Runnable periodicTask = new Runnable() {
		public void run() {
			//Log.v("PeriodicTimerService","Awake");
			if (!mDialogue.toSay.equalsIgnoreCase("")){

				tts.say(mDialogue.toSay);
				mDialogue.toSay = "";
				
			}
			mDialogue.nextStates();
			mHandler.postDelayed(periodicTask, DELAY);

		}
	};


	private void onShortPress() 
	{
		if(hasSpeech())
		{
			voicerec.startSpeechRecognition();
		}
	}

	private boolean hasSpeech() {
		// Check to see if a recognition activity is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) 
		{
			return true;
		} 
		else 
		{
			Toast.makeText(getApplicationContext(), 
					"ATTN you do not have TTS supported", 
					Toast.LENGTH_LONG).show();
			Log.w(Constants.TAG, "ERROR Speech not supported");
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(Constants.TAG, "Key hit: " + keyCode);

		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	} 


	@Override
	public void onStartSpeaking() {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "onStartSpeaking");
		voicerec.stopSpeechRecognition();
	}

	@Override
	public void onDoneSpeaking() {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "onDoneSpeaking");
		voicerec.startSpeechRecognition();
	}

	@Override
	public void onResultAvailable() {
		// TODO Auto-generated method stub
		ShowList(voicerec.getResults());
		
		

		mDialogue.processSpeech(voicerec.getResults());

		toast(mDialogue.selectedLine);
		//context.launcher.launch(VoiceRec.decipher(voiceResults), voiceResults.get(0));

		//tts.say(most);
	}

	private void ShowList(ArrayList<String> content){
		mList.setAdapter(new ArrayAdapter<String>(getBaseContext(), 
				android.R.layout.simple_list_item_1,
				content));
	}
	private void toast(String text){
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
	}
}
