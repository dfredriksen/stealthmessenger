package com.ninjitsuware.notepad;


import android.os.Bundle;
import android.view.View.OnClickListener;

import android.app.Activity;
import android.view.View;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public class StealthMainActivity extends Activity 
{
    public void onCreate(Bundle savedInstanceState) 
    {
        
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.menu);
        StealthMessengerSettings.activitySettings(this);
        this.findViewById( R.id.btnView ).setOnClickListener( btnViewListener );
        this.findViewById( R.id.btnSettings ).setOnClickListener( btnSettingsListener );        
    }
    
    private OnClickListener btnViewListener = new OnClickListener()
    {
        public void onClick(View v) 
        {
        	Intent ViewIntent = new Intent(StealthMainActivity.this, StealthMessengerKeypad.class);
    		Bundle b = new Bundle();
        	b.putInt("request_Code", 0);
        	ViewIntent.putExtras(b);        	
        	StealthMainActivity.this.startActivityForResult(ViewIntent,0);
        	
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	switch(resultCode)
    	{
	    	case 5:
	    	{
	    		Intent SettingsIntent = new Intent(StealthMainActivity.this, StealthPreferencesActivity.class);
	    		StealthMainActivity.this.startActivity(SettingsIntent);
	    		break;        	
	    	}    		
    		
    		case 6:
    		{    			
    			/*Bundle b = data.getExtras(); 
    			int acct = b.getInt("acctid");
    			String phone = b.getString("phonenum");
    			Toast.makeText(this, acct + " " + phone, Toast.LENGTH_SHORT);
    			
    			Intent SSmsIntent = new Intent(StealthMainActivity.this, StealthMessengerActivity.class);
    			SSmsIntent.putExtras(b);*/
    			StealthMainActivity.this.startActivity(data);
    			
    			break;
    			
    		}
    		
    		default:
    		{
    			break;
    		}
    	}
    
    }
    
    private OnClickListener btnSettingsListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	
        	
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(StealthMainActivity.this);
        	String passwd = sharedPrefs.getString("Settings_Password", "");
        	
        	if(passwd.length() == 0)
        	{
        		Intent SettingsIntent = new Intent(StealthMainActivity.this, StealthPreferencesActivity.class);
        		StealthMainActivity.this.startActivity(SettingsIntent);
        	}
        	else
        	{
        		Intent SettingsIntent = new Intent(StealthMainActivity.this, StealthMessengerKeypad.class);
        		
        		Bundle b = new Bundle();
            	b.putInt("request_Code", 1);
            	SettingsIntent.putExtras(b);
        		StealthMainActivity.this.startActivityForResult(SettingsIntent, 1);      		
        	}
        }
    };
    
}


