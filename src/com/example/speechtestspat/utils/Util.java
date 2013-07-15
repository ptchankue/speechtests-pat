package com.example.speechtestspat.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

	
	public Util(){
		
	}
	public int convertToInt(String s){
		int r = 0;
		if(s.equalsIgnoreCase("1")||s.equalsIgnoreCase("one")){
			r = 1;
		} else if(s.equalsIgnoreCase("2")||s.equalsIgnoreCase("two")){
			r = 2;
		} else if(s.equalsIgnoreCase("3")||s.equalsIgnoreCase("three")){
			r = 3;
		} else if(s.equalsIgnoreCase("4")||s.equalsIgnoreCase("four")){
			r = 4;
		} else if(s.equalsIgnoreCase("5")||s.equalsIgnoreCase("five")){
			r = 5;
		}  
		return r;
	}
	
	public static String getNowDateTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	public String getDate(){
		Calendar calendar = Calendar.getInstance();

		int day, month, year; 
		String  date ;
		int d = calendar.get(Calendar.DAY_OF_WEEK);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH);
		year =  calendar.get(Calendar.YEAR);
		date = "Today is " + getDay(d) + " the ";
		
		switch(day)	{	
		case 1 : date += "first" ;
		break;
		case 21 : date += "twenty first" ;
		break;
		case 31 : date += "thirty first" ;
		break;
		default:date += day ; break;
		}
		
		date += " of "+ getMonth(month) + " " + year;
		//System.out.println(date);

		return date;
	}
	public String getTime(){
		Calendar calendar = Calendar.getInstance();
		int hour, min; String  time = "The time is: ";

		hour = calendar.get(Calendar.HOUR);
		min = calendar.get(Calendar.MINUTE);
		String am = calendar.get(Calendar.AM_PM)== 0? "A M": "P M";
		if(hour == 0){
			if (am.equalsIgnoreCase("a m") )
				time += min + " minutes past twelve "; 
			else
				if(am.equalsIgnoreCase("p m"))
					time += min + " minutes past midnight "; 
		}
		else
		{
			time  += 	Integer.toString(hour) + " "+
			Integer.toString(min)+ " "+
			am;
		}
		if(min == 0){
			time="the time is ";
			time  += Integer.toString(hour)+ " "+
			am;

		}
		else if (min < 30 ){ // 3:00
			time="the time is ";
			time  += 	Integer.toString(min) + " minutes past "+
			Integer.toString(hour)+ " "+
			am;
		}

		return time;
	}
	
	private String getMonth(int i){
		String m = "";
		i++;
		switch(i){
		case 1: m = "January"; break;
		case 2: m = "February"; break;
		case 3: m = "March"; break;
		case 4: m = "April"; break;
		case 5: m = "May"; break;
		case 6: m = "June"; break;
		case 7: m = "July"; break;
		case 8: m = "August"; break;
		case 9: m = "September"; break;
		case 10: m = "October"; break;
		case 11: m = "November"; break;
		case 12: m = "December"; break;
		}
		return m;
	}
	private String getDay(int i){
		String m = "";
		//i++;
		switch(i){
		case 1: m = "Sunday"; break;
		case 2: m = "Monday"; break;
		case 3: m = "Tuesday"; break;
		case 4: m = "Wednesday"; break;
		case 5: m = "Thursday"; break;
		case 6: m = "Friday"; break;
		case 7: m = "Saturday"; break;		
		}
		return m;
	}
	
	
}
