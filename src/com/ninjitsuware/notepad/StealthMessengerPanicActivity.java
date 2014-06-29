package com.ninjitsuware.notepad;


import android.os.Bundle;
import android.view.View.OnClickListener;
import android.app.Activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public class StealthMessengerPanicActivity extends Activity 	
{	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);               
        setTheme(android.R.style.Theme_Light);
        setContentView(R.layout.panicscreen);
                      
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);		
		StealthMessengerSettings.activitySettings(this);
		TextView lblPanic = (TextView)findViewById(R.id.textView1);
		String appTitle = sharedPrefs.getString("App_Title", "SSMS");
		lblPanic.setText(appTitle + " is actively protecting your device");
		boolean virus = sharedPrefs.getBoolean("virus_on", true);
		boolean firewall = sharedPrefs.getBoolean("firewall_on", true);
		boolean sms = sharedPrefs.getBoolean("text_on", true);
        
		CheckBox cbxV = (CheckBox)findViewById(R.id.checkBox1);
		CheckBox cbxF = (CheckBox)findViewById(R.id.checkBox2);
		CheckBox cbxT = (CheckBox)findViewById(R.id.checkBox3);
		
		cbxV.setChecked(virus);
		cbxF.setChecked(firewall);
		cbxT.setChecked(sms);
		
		
		findViewById(R.id.btnPanicBack).setOnClickListener(BtnPanicBackListener);
       
    }
    
    private OnClickListener BtnPanicBackListener = new OnClickListener()
    {
        public void onClick(View v) 
        {
        	StealthMessengerPanicActivity.this.finish();
        }
    };
    
    @Override
    protected void onPause() 
    {
        super.onPause();
        finish();
    }
    @Override
    protected void onStop() 
    {
        super.onStop();
        finish();
    }
    
}
