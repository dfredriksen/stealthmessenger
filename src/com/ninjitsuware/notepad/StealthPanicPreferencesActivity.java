package com.ninjitsuware.notepad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class StealthPanicPreferencesActivity extends PreferenceActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.panicpreferences); 
        StealthMessengerSettings.activitySettings(this);
        
    }

}