package com.example.speechtestspat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import com.example.speechtestspat.utils.Constants;

public class NLU {

	String[] KEYWORD = 
	{
			"what", "time", /*"is", "it", "the",*/ "weather", "date",
			"call", "text", "send", "message", "sms", "exit", "repeat", "close",
			"please", "phone", "galaxy", "music", "hi", "name", "reminder",
			"alarm", "set", "show", "map", "where", "am", "galaxy","mimic",
			"text", "phone"	, "sms",
			"1", "2", "3", "4"
	};

	private String currentCmd; 
	private String selectedLine;
	private String contactName;

	private ArrayList<String[]> homophone, synonym;
	private ArrayList<String> contact;

	public NLU(){
		selectedLine 	= null;
		currentCmd 		= null;
		contactName		= null;
		homophone 		= getRawRes("homophone.txt");
		//homophone 		= getRawRes("homophon.txt");
		contact 		= getRawRes_("contact.txt");
	}

	public String getCommand(ArrayList<String> speech){
		System.out.println("GetCommand");
		currentCmd = null;
		
		selectedLine = getBestResult(speech);
		
		//what was the best commands?
		if(selectedLine.contains("weather"))
			currentCmd = Constants.CMD_WEATHER;
		if(selectedLine.contains("where"))
			currentCmd = Constants.CMD_WEATHER;
		else if(selectedLine.contains("time"))
			currentCmd = Constants.CMD_TIME; 
		else if(selectedLine.contains("date"))
			currentCmd = Constants.CMD_DATE; 
		else if(selectedLine.contains("repeat"))
			currentCmd = Constants.CMD_REPEAT; 
		else if(selectedLine.contains("exit"))
			currentCmd = Constants.CMD_EXIT; 
		else if(selectedLine.contains("close"))
			currentCmd = "close"; 
		else if(selectedLine.contains("call"))
			currentCmd = Constants.CMD_CALL; 
		else if(selectedLine.contains("send") || selectedLine.contains("sms"))
			currentCmd = Constants.CMD_SMS; 
		else if(selectedLine.contains("reminder"))
			currentCmd = "reminder"; 
		else if(selectedLine.contains("alarm"))
			currentCmd = "alarm"; 
		else if(selectedLine.contains("name"))
			currentCmd = Constants.CMD_NAME; 
		else if(selectedLine.contains("cancel"))
			currentCmd = Constants.CMD_CANCEL; 
		else if(selectedLine.contains("map"))
			currentCmd = Constants.CMD_MAP; 
		else if(selectedLine.contains("galaxy") || selectedLine.contains("mimic"))
			currentCmd = Constants.CMD_WAKEUP; 

		return selectedLine;

	}
	
	public String getBestResult(ArrayList<String> speech){
		System.out.println("getBestResult");
		double[] prob 	= new double[speech.size()];
		selectedLine 	= null;
		contactName 	= null;
		//currentCmd = null;

		//looking for the result that contains the most keywords
		for(int i=0; i< speech.size(); i++) {
			//clean the entry
			speech.set(i, applyHomophones(speech.get(i).toLowerCase(Locale.ENGLISH)));
			String line = speech.get(i);
			// for each result			
			
			for(String k: KEYWORD){
				if(line.contains(k))
					prob[i]++;
			}
		}
		//getting the index of the highest result
		int best = 0; double value = prob[0];
		for (int i=1; i<prob.length; i++){
			if(value < prob[i]){
				value = prob[i];
				best = i;
			}
		}
		System.out.println("BestResult= " +speech.get(best));
		return speech.get(best).toLowerCase();
	}
	
	public String getSelectedLine(){
		return selectedLine;
	}
	public String getCmd(){
		return currentCmd;
	}
	public String getContact(){
		for(String c: contact){
			
			if(selectedLine.contains(c)){
				
				contactName = c;
				System.out.println("Contact -> " + c);
				break;
			}
		}
		return contactName;
	}
	/**
	 * 
	 * @param value
	 */
	public String applyHomophones(String value){
		//System.out.println("Apply homophones");
		for(String[] s: homophone){
			if(value.trim().contains(s[0].trim())) {
				value = value.replace(s[0].trim(), s[1].trim());
				System.out.println("replace  "+s[0].trim()+" by " + s[1].trim());
			}
		}
		return value;	
	}
	
	private String homophone(String text){
		// the homophone files hould be structured this way 
		// so that a loop does all teh replacements
		text = text.toLowerCase();
		for(String[] s: homophone){
			text.replaceAll(s[0].trim(), s[1].trim());
		}
		/*text.replaceAll("if it is|estimate", "sms");
		text.replaceAll("jones|june|joint|join", "john");
		text.replaceAll("ya|okay|yeah|yesss|oui|positive", "yes");
		text.replaceAll("nope|note|nah|kno|i know|positive", "no");*/
		return text;
	}
	
	public ArrayList<String[]> getRawRes (String file){
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("res/raw/" + file);

		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		ArrayList<String[]> list = new ArrayList<String[]>();
		try {
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] key = strLine.split(">");
				list.add(key);
			}
			//Close the input stream
			in.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return list;

	}
	
	
	public ArrayList<String> getRawRes_(String file){
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("res/raw/" + file);

		DataInputStream in = new DataInputStream(inputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		ArrayList<String> list = new ArrayList<String>();
		try {
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				list.add(strLine);
			}
			//Close the input stream
			in.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return list;

	}
	public boolean isContact(String str){
		for(String s: contact)
			if (s.equalsIgnoreCase(str))
				return true;
		return false;

	}


}
