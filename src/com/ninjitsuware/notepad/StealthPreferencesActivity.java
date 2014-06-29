package com.ninjitsuware.notepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class StealthPreferencesActivity extends PreferenceActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);       
        StealthMessengerSettings.activitySettings(this);
        addPreferencesFromResource(R.xml.preferences); 
        
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(sharedPrefs.getBoolean("show_version_notes_201", true) )
		{
			AlertDialog myDialog = new AlertDialog.Builder(this).create();
			myDialog.setTitle("Stealth Messenger\nVersion " + StealthMessengerSettings.versionString);
			myDialog.setIcon(R.drawable.icon);
			myDialog.setButton("Ok", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			
			myDialog.setMessage(StealthMessengerSettings.versionMsg);
			myDialog.show();
			SharedPreferences.Editor editor1 = sharedPrefs.edit();
			editor1.putBoolean("show_version_notes_201",false);
			editor1.commit();
		}
        
    }

}