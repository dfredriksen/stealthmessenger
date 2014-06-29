package com.ninjitsuware.notepad;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StealthMessengerSettings {
	
	public static String versionString = "2.0.1 Bachata";
	public static String versionMsg = "New in this version..\n\n* Got rid of the ads, they sucked!\n\n* Put the icon back in the launchpad. In the future, option will be available to hide.\n\n* Fixed issue with keyboard hiding message interface.\n\n";
	public static void activitySettings(Activity targetActivity){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(targetActivity);		        
    	String appTitle = sharedPrefs.getString("App_Title", "SSMS");    	
		targetActivity.setTitle(appTitle);
		
	}
	
}
