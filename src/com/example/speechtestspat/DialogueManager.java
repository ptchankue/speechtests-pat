package com.example.speechtestspat;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.content.Context;

import com.example.speechtestspat.utils.Constants;
import com.example.speechtestspat.utils.Util;

public class DialogueManager {

	private final byte 		IDLE = 0, BUSY = 1, CONFIRM = 2, OPTION =3, RUN = 4;
	private byte 			state;
	private String 			currentCmd; 
	public String 			selectedLine;
	private static String 	TAG = "Dialogue";

	private NLU 			nlu;

	public String 			toSay = "";
	public String 			lastUtterance = "";
	private Context 		mContext;
	private String 			answer;
	private String 			contact;
	private int 			option;
	private Util			mUtil = new Util();
	private String 			message = "";
	private Pattern			digitPattern, answerPattern;

	//this will be updatable in the settings
	public String[] 		sms_options = 
		{
			"I will call you when I get there", 
			"I can't talk right now, I am driving", 
			"I am running a few minutes late",
			"I am on my way"
		};
	public DialogueManager(Context c){
		mContext = c;
		initialise();
		nlu = new NLU();
		digitPattern = Pattern.compile("[1-4]");
		answerPattern = Pattern.compile("yes|no");
	}

	public void initialise(){
		state 			= IDLE;
		currentCmd 		= null;
		selectedLine	= null;
		//toSay 			= "";
		answer			= "";
		option 			= 0;
		contact			= null;
	}
	public void nextStates(){
		/*if(state == IDLE) {
			// looking for a new command

		}
		else {
			// A command is in progress

		}*/
		switch (state) {
		case IDLE:
			if(currentCmd != null)
				state = BUSY;
			break;

		case BUSY:
			if(currentCmd.equalsIgnoreCase("close")
					||currentCmd.equalsIgnoreCase("call")){

				state = CONFIRM;
				contact = nlu.getContact();

				confirm(currentCmd);

			} else if(currentCmd.equalsIgnoreCase("sms")){
			
				state = OPTION;
				option = 0;
				String msg = ""; int j = 0;
				contact = nlu.getContact();
				
				for (int i= 0; i< sms_options.length; i++) {
					j = i+1;
					msg +=  j + ", " + sms_options[i] + ".\n";
				}
				toSay = "Please choose an option." + "\n" + (msg);
				//toSay = "Please choose an option";
			}
			else {
				state = RUN;
			}
			break;

		case CONFIRM:
			//System.out.println("Confirm branch: \nState " + state + "\nAnswer " + answer);
			if("yes".equalsIgnoreCase(answer)){
				state  = RUN;
				runCmd(currentCmd);	
			}
			else if ("no".equalsIgnoreCase(answer)){
				cancelCmd();
			}
			break;

		case OPTION:
			if(option != 0) {
				state = CONFIRM;
				//contact = nlu.getContact();
				message = sms_options[option - 1];
				confirm(currentCmd);	
			}
			break;
		case RUN:
			runCmd(currentCmd);
			break;
		}
		//System.out.println("Current state :" + state);
	}

	private void cancelCmd() {
		// TODO Auto-generated method stub
		toSay = "cancel the command";
		initialise();
	}


	private void confirm(String currentCmd) {
		// TODO Auto-generated method stub

		if("call".equalsIgnoreCase(currentCmd)){	
			if (contact != null) {
				toSay = "Do you want to call " + contact + ". Say Yes or No";
			} else{
				toSay = "Please provide the contact to be called";
				// start the command incomplete timer
			}

		} else if("close".equalsIgnoreCase(currentCmd)){
			toSay = "Do you want to close the application. Say Yes or No";

		} else if("send".equalsIgnoreCase(currentCmd) || "sms".equalsIgnoreCase(currentCmd)){
			String sms ="Do you want to send the message";
			sms += " " + message + " to " + contact;
			sms += ". Say Yes or No";
			toSay = sms;			
		}

	}

	public void processSpeech(ArrayList<String> speech){

		nlu.getCommand(speech);

		selectedLine = nlu.getSelectedLine();

		if(selectedLine!= null){
			//selectedLine = nlu.getSelectedLine();
			System.out.println(selectedLine);
			if(state==IDLE){
				currentCmd = nlu.getCmd();

				if(currentCmd != null){

					System.out.println("<" + currentCmd + ">");

					// a command was detected and the current state is 
					state = BUSY;
					//toSay = currentCmd;

					
				}
				else
				{
					// the system does not understand what was said
					//<<Buzz

				}
			}
			//selectedLine	= null;

			// These commands can be calle anytime
			if(Constants.CMD_CANCEL.equalsIgnoreCase(nlu.getCmd())){
				cancelCmd();
			} else if (Constants.CMD_REPEAT.equalsIgnoreCase(nlu.getCmd())){
				runCmd("repeat");
			}
			
			if(state == CONFIRM){
				// check if it is an allowed answer Y or N
				answer = selectedLine;
			}
			else if (state == OPTION){
				//check if best result is a digit
				if(digitPattern.matcher(selectedLine).matches()) {
					option = Integer.valueOf(selectedLine);
				}
				else
				{
					//not number from 1 to 4
					toSay = "Please say a number between 1 and 4";
				}
			}


		}else {
			// nothing was selected from the speech engine results
			toSay = "I didn't understand what you say";

		}

		nextStates();

		lastUtterance = toSay;

	}

	private void runCmd(String currentCmd) {
		// TODO Auto-generated method stub
		if(Constants.CMD_CALL.equalsIgnoreCase(currentCmd)){
			toSay = "calling " + contact;		

		}else if(Constants.CMD_SMS.equalsIgnoreCase(currentCmd)){
			toSay = "sending " + message + " to " + contact;

		}else if(Constants.CMD_REPEAT.equalsIgnoreCase(currentCmd)){
			toSay = lastUtterance;

		}else if(Constants.CMD_TIME.equalsIgnoreCase(currentCmd)){
			//toSay = "telling the the time";
			toSay = mUtil.getTime();
		}else if("close".equalsIgnoreCase(currentCmd)){
			toSay = "closing the application";

		} else if(Constants.CMD_NAME.equalsIgnoreCase(currentCmd)){
			toSay = "my name is mimic";

		} else if(Constants.CMD_DATE.equalsIgnoreCase(currentCmd)){

			//toSay = "telling the date ";
			toSay = mUtil.getDate();
		}else if(Constants.CMD_WEATHER.equalsIgnoreCase(currentCmd)){

			toSay = "telling the weather ";

		}else if(Constants.CMD_LOCATION.equalsIgnoreCase(currentCmd)){

			toSay = "telling the location ";

		}else if(Constants.CMD_MAP.equalsIgnoreCase(currentCmd)){

			toSay = "showing the map ";

		}
		else if(Constants.CMD_WAKEUP.equalsIgnoreCase(currentCmd)){

			toSay = "what can I do for you? ";

		}
		initialise();
	}


	/**
	 * Convert emoticones from sms into text
	 * @param text
	 * @return
	 */
	private String convertEmoticones(String text){

		text.replaceAll(":-\\)|:\\)|:o\\)|:]|:3|:c\\)|:>|=]|8\\)|=\\)|:}|:^\\)|:っ\\)", "[Smiley]");
		//:-D :D 8-D 8D x-D xD X-D XD =-D =D =-3 =3 B^D
		text.replaceAll(":-D |:D |8-D |8D |x-D| xD| X-D |XD |=-D |=D| =-3| =3| B^D:lol", "[Laughing]");

		text.replaceAll(":-\\)\\)", "[Very happy]");
		//>:[ :-( :(  :-c :c :-<  :っC :< :-[ :[ :{
		text.replaceAll(">:[ |:-\\( |:\\(  |:-c |:c |:-< | :っC| :< |:-[| :[| :{", "[Sad]");
		return text;
	}



}
