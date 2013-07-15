package com.example.speechtestspat.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class Logger {

	String file="", FILENAME;
	private String TAG="Logger";
	
	public Logger(){
		
	}
	public void save(String s){
		
	}
	public void appendLog(String text)
	{       
		String extDir = Environment.getExternalStorageDirectory().getPath();
		File myFolder = new File(extDir + "/MIMIC/Dialogue");
		if (!myFolder.exists())
			myFolder.mkdir();

		File logFile = new File(myFolder + "/"+  FILENAME);
		Log.d(TAG, logFile.toString() + " created");

		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void appendLog(String text, String filename)
	{       
		String extDir = Environment.getExternalStorageDirectory().getPath();
		File myFolder = new File(extDir + "/MIMIC/Dialogue");
		if (!myFolder.exists())
			myFolder.mkdir();

		File logFile = new File(myFolder + "/"+  filename);
		Log.d(TAG, logFile.toString() + " created");

		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
}
