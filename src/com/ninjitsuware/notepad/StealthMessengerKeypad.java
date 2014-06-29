package com.ninjitsuware.notepad;


import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;

import android.os.Bundle;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public class StealthMessengerKeypad extends Activity 	
{
	private int mode = 0;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        mode = b.getInt("request_Code");               
        setContentView(R.layout.keypad);
        
                
        EditText txtCombo =  (EditText)this.findViewById( R.id.txtCombo );
        txtCombo.setCursorVisible(false);
        
        StealthMessengerSettings.activitySettings(this);        
        
        this.findViewById( R.id.btn1 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn2 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn3 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn4 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn5 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn6 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn7 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn8 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn9 ).setOnClickListener( KeypadListener );
        this.findViewById( R.id.btn0 ).setOnClickListener( KeypadListener );        
        this.findViewById( R.id.btnBksp ).setOnClickListener( BtnBackspaceListener );
        this.findViewById( R.id.btnClose ).setOnClickListener( BtnCloseListener );
        this.findViewById( R.id.btnEnter ).setOnClickListener( BtnEnterListener );
                
    }
          
    private OnClickListener KeypadListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	EditText txtCombo =  (EditText)StealthMessengerKeypad.this.findViewById(R.id.txtCombo);
        	Button btnClicked =  (Button)StealthMessengerKeypad.this.findViewById(v.getId());
        	txtCombo.setText(txtCombo.getText().toString() + btnClicked.getText().toString());        	
        }
    };

    private OnClickListener BtnBackspaceListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	EditText txtCombo =  (EditText)StealthMessengerKeypad.this.findViewById(R.id.txtCombo);
        	if(txtCombo.getText().length() > 0)
        		txtCombo.setText(txtCombo.getText().subSequence(0, txtCombo.getText().length()-1));   
        }
    };    
    
    
    
    private OnClickListener BtnCloseListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	//Close activity
        	if(mode == 1)
        		setResult(-1, null);
        	
        	StealthMessengerKeypad.this.finish();
        }
    };

    private OnClickListener BtnEnterListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
            //Get content resolver to access Accounts database
        	EditText txtCombo =  (EditText)StealthMessengerKeypad.this.findViewById(R.id.txtCombo);
            
        	if(mode != 1)
        	{
	        	ContentResolver resolver = StealthMessengerKeypad.this.getContentResolver();
	            Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, null, SsmsAccounts.SSMSPIN + "='" + txtCombo.getText() + "'", null, null);
	            
	            c.moveToFirst();

        		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(StealthMessengerKeypad.this);
            	String panicPIN = sharedPrefs.getString("Panic_PIN", "");	            
	                        	
            	if(txtCombo.getText().length() > 0)
            	{
		            if (c.getCount() < 1 && !txtCombo.getText().toString().equals(panicPIN))
		            {
		            	Context context = getApplicationContext();
		            	CharSequence text = "Invalid pin number";
		            	int duration = Toast.LENGTH_SHORT;
		
		            	Toast toast = Toast.makeText(context, text, duration);
		            	toast.show();
		            }
		            else if(c.getCount() < 1 && txtCombo.getText().toString().equals(panicPIN))
		            {		            			            	
		            	Intent PanicIntent = new Intent(StealthMessengerKeypad.this, StealthMessengerPanicActivity.class);
		            	startActivity(PanicIntent);
		            	StealthMessengerKeypad.this.finish();		            	
		            }
		            else if(c.getCount() > 1)
		            {
		            	Context context = getApplicationContext();
		            	CharSequence text = "Error: Multiple accounts found.";
		            	int duration = Toast.LENGTH_SHORT;
		
		            	Toast toast = Toast.makeText(context, text, duration);
		            	toast.show();            	
		            }
		            else
		            {
		            	int acctIndex = Integer.parseInt(c.getString(c.getColumnIndex(SsmsAccounts.SSMSACCT_ID)));
		            	String phoneNum = c.getString(c.getColumnIndex(SsmsAccounts.SSMSACCT_PHONE)); 
		            	Intent SSmsIntent = new Intent(StealthMessengerKeypad.this, StealthMessengerActivity.class);
		            	Bundle b = new Bundle();
		
		            	b.putInt("acctid", acctIndex);
		            	b.putString("phonenum", phoneNum);
		            	SSmsIntent.putExtras(b);
		            	setResult(6,SSmsIntent);
		            	StealthMessengerKeypad.this.finish();
		            	
		            }
            	}
            	else
            	{
	            	Context context = getApplicationContext();
	            	CharSequence text = "Please enter a PIN";
	            	int duration = Toast.LENGTH_SHORT;
	
	            	Toast toast = Toast.makeText(context, text, duration);
	            	toast.show();
            	}
        	}
        	else
        	{
        		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(StealthMessengerKeypad.this);
            	String passwd = sharedPrefs.getString("Settings_Password", "");
            	String panicPin = sharedPrefs.getString("Panic_PIN", "");
            	if(passwd.equals(txtCombo.getText().toString()))
            	{
            		setResult(5,null);
            		StealthMessengerKeypad.this.finish();
            	}
            	else if(panicPin.equals(txtCombo.getText().toString()))
            	{
	            	Intent PanicIntent = new Intent(StealthMessengerKeypad.this, StealthPanicPreferencesActivity.class);
	            	startActivity(PanicIntent);
	            	StealthMessengerKeypad.this.finish();
            	}
            	else
            	{
	            	Context context = getApplicationContext();
	            	CharSequence text = "Invalid pin number";
	            	int duration = Toast.LENGTH_SHORT;
	
	            	Toast toast = Toast.makeText(context, text, duration);
	            	toast.show();
	            	
            	}
        	}
        	
        	txtCombo.setText("");
        }	
    };    
    
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    
}
