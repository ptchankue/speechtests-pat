package com.example.speechtestspat.speech;
public interface InteractionCompletedEvent{ 
		
	public void onStartSpeaking(); 
	public void onDoneSpeaking();
	public void onResultAvailable();
}