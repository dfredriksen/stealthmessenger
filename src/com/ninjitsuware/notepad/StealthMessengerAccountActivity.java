package com.ninjitsuware.notepad;


import java.util.ArrayList;

import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.content.SharedPreferences;
import android.database.Cursor;

public class StealthMessengerAccountActivity extends Activity 
{
	private int mode = 0;
	private long acctid = 0;
	//private ArrayAdapter<String> adapter;
	
	private LoadContactsClass myClass;
	
	final String[] projection = new String[] {
			RawContacts.CONTACT_ID,	// the contact id column
			RawContacts.DELETED		// column if this contact is deleted
			};
		
	public void onCreate(Bundle savedInstanceState) 
    {
        
    	super.onCreate(savedInstanceState);
            	
    	
        setContentView(R.layout.modaccounts);                 
        
        Bundle b = getIntent().getExtras();
        
        mode = b.getInt("mode");
                
        Button btnSubmit = (Button)findViewById(R.id.btnAddInsert);
        
        StealthMessengerSettings.activitySettings(this);       
        
        switch(mode)
        {
        	case 2:
        	{
        		acctid = b.getLong("acctid");
        		
        		ContentResolver resolver = getContentResolver();
        		
        		Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, null,SsmsAccounts.SSMSACCT_ID +"=" +acctid, null, null); //new String[] {SsmsAccounts.SSMSACCT_ID, SsmsAccounts.SSMSACCT_PHONE}, SsmsAccounts.SSMSACCT_PHONE + "='" + address + "'"            	
	            
        		int acctIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_ID);
        		int nameIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_NAME);
        		int phoneIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_PHONE);
        		int pinIdx = c.getColumnIndex(SsmsAccounts.SSMSPIN);
        		int recEncIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_RECEIVE_ENC);
        		int sendEncIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_SEND_ENC);
        		int encKeyIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_ENC_KEY);
        	
            	if ( acctIdx >= 0 && c.moveToFirst() )
            	{            	        
	        		do
	        		{
	        			
	//        			String accountIdx = c.getString(acctIdx);
	        			String acctname= c.getString(nameIdx);
	        			String acctPhone = c.getString( phoneIdx );	        			
	        			String acctpin = c.getString(pinIdx);
	        			String recEncrypt = c.getString(recEncIdx);
	        			String sendEncrypt = c.getString(sendEncIdx);	      
	        			String strEncKey = c.getString(encKeyIdx).trim();
	        			
	                	EditText txtAcctName = (EditText)findViewById(R.id.txtAcctName);
	                	AutoCompleteTextView txtNumbers = (AutoCompleteTextView)findViewById(R.id.mtxtNumbers);
	                	EditText txtPin = (EditText)findViewById(R.id.txtPinEntry);
	                	CheckBox cbxRecEnc = (CheckBox)findViewById(R.id.cbxRecEnc);
	                	CheckBox cbxSendEnc = (CheckBox)findViewById(R.id.cbxSendEnc);
	                	EditText txtEncKey = (EditText)findViewById(R.id.txtEncKey);
	                	
	                	txtAcctName.setText(acctname);
	                	txtNumbers.setText(acctPhone);
	                	txtPin.setText(acctpin);
	                	txtEncKey.setText(strEncKey);
	                	
	                	cbxRecEnc.setChecked(Boolean.parseBoolean(recEncrypt));	
	                	cbxSendEnc.setChecked(Boolean.parseBoolean(sendEncrypt));	                
	                	
	        		}
	        		while( c.moveToNext() );

            	}          
            	
            	c.close();
            	        		
        		
        		btnSubmit.setOnClickListener(BtnUpdateListener);
        		btnSubmit.setText("Update Account");
        		break;	
        	}        		
        	default:
        	{        		
        		btnSubmit.setOnClickListener(BtnInsertListener);		
        		btnSubmit.setText("Add to Accounts");
        		break;
        	}
        		        		        
        }
        
        
        
        myClass = new LoadContactsClass();
        myClass.execute();
        
        //textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());	        
        
    }
	
	private class LoadContactsClass extends AsyncTask<Void, Void, ArrayList<String>> 
	{
		protected ArrayList<String> doInBackground(Void... params)			 
	     {
	    	 ArrayList<String> ContactArray = new ArrayList<String>();
	         String name = "";
	    	 
	         final Cursor rawContacts = managedQuery(RawContacts.CONTENT_URI,
	                 projection, null, null, null);               
	         
	         final int contactIdColumnIndex = rawContacts.getColumnIndex(RawContacts.CONTACT_ID);
	         final int deletedColumnIndex = rawContacts.getColumnIndex(RawContacts.DELETED);
	         
	    	 if(rawContacts.moveToFirst())
	         {
	         	while(!rawContacts.isAfterLast()) 
	         	{		
	         		
	         		// still a valid entry left?
	         		final int contactId = rawContacts.getInt(contactIdColumnIndex);
	         		final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);
	         		if(!deleted) 
	         		{
	         			final String[] cprojection = new String[] {
	         					Contacts.DISPLAY_NAME,
	         			};

	         			final Cursor contact = managedQuery(
	         					Contacts.CONTENT_URI,
	         					cprojection,
	         					Contacts._ID + "=?",
	         					new String[]{String.valueOf(contactId)},
	         					null);        			
	         			
	         			if(contact.moveToFirst())
	         			{
	         				final int contactNameColumnIndex = contact.getColumnIndex(Contacts.DISPLAY_NAME);
	         				name = contact.getString(contactNameColumnIndex);
	         			}
	         			
	         			contact.close();
	         			
	         			final String[] phprojection = new String[] {
	         					Phone.NUMBER,
	         			};
	         			
	         			final Cursor phone = managedQuery(
	         					Phone.CONTENT_URI,
	         					phprojection,
	         					Data.CONTACT_ID + "=?",
	         					new String[]{String.valueOf(contactId)},
	         					null);
	         			
	         			if(phone.moveToFirst()) 
	         			{
	         				final int contactNumberColumnIndex = phone.getColumnIndex(Phone.NUMBER);

	         				while(!phone.isAfterLast()) 
	         				{
	         					final String number = phone.getString(contactNumberColumnIndex);
	         					        				
	         					ContactArray.add(name + " <" + number + ">");
	         					
	         					phone.moveToNext();
	         				}

	         			}
	         			phone.close();
	         			        		
	         		}
	         		rawContacts.moveToNext();			// move to the next entry
	         	}
	         }        
	         rawContacts.close();

	         return ContactArray;
	     }

	     protected void onPostExecute(ArrayList<String> ContactArray) 
	     {	    	 
	    	 try
	    	 {
	 	        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.mtxtNumbers);
		        textView.setAdapter(new ArrayAdapter<String>(StealthMessengerAccountActivity.this,
	    	    		android.R.layout.simple_dropdown_item_1line, ContactArray));
		        textView.setHint("Contact List Loaded");
	    	 }
	    	 catch(Throwable e)
	    	 {
	    		 
	    	 }
	        
	     }	
	
	 }
	
    private OnClickListener BtnUpdateListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	ContentValues values = validateForm();
        	if(values != null)
        	{
                try
                {
                	ContentResolver resolver = getApplicationContext().getContentResolver();                	
                	resolver.update(SsmsAccounts.CONTENT_URI, values, SsmsAccounts.SSMSACCT_ID + "=" + acctid, null);
                	
                	Context context = getApplicationContext();
                	CharSequence text = "Account successfully created";
                	int duration = Toast.LENGTH_LONG;

                	Toast toast = Toast.makeText(context, text, duration);
                	toast.show();             		
            		finish();                	
                }
                catch(android.database.SQLException e)
                {
                	e.printStackTrace();
                }
        	}        	        	
        }
    };

    private OnClickListener BtnInsertListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	ContentValues values = validateForm();
        	if(values != null)
        	{
                try
                {
                	ContentResolver resolver = getApplicationContext().getContentResolver();                	
                	resolver.insert( SsmsAccounts.CONTENT_URI, values );
                	
                	Context context = getApplicationContext();
                	CharSequence text = "Account successfully created";
                	int duration = Toast.LENGTH_LONG;

                	Toast toast = Toast.makeText(context, text, duration);
                	toast.show();             		
            		finish();                	
                }
                catch(android.database.SQLException e)
                {
                	e.printStackTrace();
                }
        	}
        }
    };	

    private ContentValues validateForm()
    {
       	String errMsg = "";
    	EditText txtAcctName = (EditText)findViewById(R.id.txtAcctName);
    	AutoCompleteTextView txtNumbers = (AutoCompleteTextView)findViewById(R.id.mtxtNumbers);
    	EditText txtPin = (EditText)findViewById(R.id.txtPinEntry);
    	EditText txtEncKey = (EditText)findViewById(R.id.txtEncKey);
    	
    	CheckBox cbxRecEnc = (CheckBox)findViewById(R.id.cbxRecEnc);
    	CheckBox cbxSendEnc = (CheckBox)findViewById(R.id.cbxSendEnc);

    	
    	if(txtAcctName.getText().length() < 1)
        	errMsg += "Account name must not be blank\n\n";

    	if(txtNumbers.getText().length() < 1)
    		errMsg += "Numbers must contain at least 1 entry\n\n";        	
    	
    	if(txtPin.getText().length() < 1)
    		errMsg += "PIN must not be blank\n\n";

    	if(!cbxRecEnc.isChecked() && !cbxSendEnc.isChecked() && txtEncKey.getText().length() > 0)
    	{
    		errMsg += "Either send, receive, or both encryption modes must be checked to use an encryption key";
    	}
    	else if((cbxRecEnc.isChecked() || cbxSendEnc.isChecked()) && txtEncKey.getText().length() != 16)
    	{ 
    		errMsg += "You must specify a shared encryption key when using encryption that is exactly 16 characters";
    	}
    	
    	if(errMsg.length() > 0)
    	{
        	Context context = getApplicationContext();
        	CharSequence text = errMsg;
        	int duration = Toast.LENGTH_LONG;

        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();    
        	return null;
    	}
    	else
    	{
    		//Parse phone numbers.
    		String strTemp = txtNumbers.getText().toString();
    		String strPhone = "";
    		if(strTemp.contains(","))
    		{
    			String strTemp2[] = strTemp.split(",");
    			
    			for(int i = 0; i < strTemp2.length; i++)
    			{
    				String strTemp3 = "";
    				
    				strTemp3 = strTemp2[i];
    				
    				if(strTemp3.length() > 0)
    				{
    				
        				if(strTemp2[i].contains("<"))
        				{
        					int i1 = strTemp2[i].indexOf('<');
        					int i2 = strTemp2[i].indexOf('>');
        					strTemp3 = strTemp2[i].substring(i1+1, i2);
        				}

    					if(i < strTemp2.length-1)
    						strPhone += strTemp3;
    					else
    						strPhone += strTemp3;
    				}
    				
    			}
    		}
    		else if(strTemp.contains("<"))
    		{
    			String strTemp4 = "";
    			
    			int i1 = strTemp.indexOf('<');
				int i2 = strTemp.indexOf('>');
				strTemp4 = strTemp.substring(i1+1, i2);
				
				strPhone += strTemp4;										        	
    		}
    		else
    		{
    			strPhone = strTemp;
    		}
    		
    		ContentResolver resolver = getApplicationContext().getContentResolver();
    		Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, new String[] {SsmsAccounts.SSMSPIN}, SsmsAccounts.SSMSPIN+"='"+txtPin.getText().toString().trim()+"' AND " + SsmsAccounts.SSMSACCT_ID + "<>" + acctid, null, null);
    		
    		int pinIdx = c.getColumnIndex(SsmsAccounts.SSMSPIN);

    		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(StealthMessengerAccountActivity.this);
    		String strSettingPin = sharedPrefs.getString("Settings_Password", "");
    		String strPanicPin = sharedPrefs.getString("Panic_PIN", "");
    		
    		if( (pinIdx < 0|| !c.moveToFirst()) 
    				&& !txtPin.getText().toString().equals(strSettingPin) 
    				&& !txtPin.getText().toString().equals(strPanicPin) )
    		{ 
    		        			
                ContentValues values = new ContentValues();
                values.put( SsmsAccounts.SSMSACCT_NAME, txtAcctName.getText().toString());
                values.put( SsmsAccounts.SSMSACCT_PHONE, strPhone );        
                values.put( SsmsAccounts.SSMSPIN, txtPin.getText().toString() );
    			values.put( SsmsAccounts.SSMSACCT_RECEIVE_ENC, String.valueOf(cbxRecEnc.isChecked()) );
    			values.put( SsmsAccounts.SSMSACCT_SEND_ENC, String.valueOf(cbxSendEnc.isChecked()) );
    			values.put( SsmsAccounts.SSMSACCT_ENC_KEY, String.valueOf(txtEncKey.getText().toString().trim()) );
    			
    			c.close();   
                // Push row into the SMS table
    			return values;
    		}
    		else
    		{
            	Context context = getApplicationContext();
            	CharSequence text = "PIN already exists, please enter a unique PIN";
            	int duration = Toast.LENGTH_LONG;
            	Toast toast = Toast.makeText(context, text, duration);
            	toast.show();
            	c.close();
            	return null;
    		}    		

    	}
    }
    
    @Override
    protected void onPause() 
    {
    	myClass.cancel(true);
    	finish();
        super.onPause();
    }

    
    @Override
    protected void onStop() 
    {
    	myClass.cancel(true);
    	finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() 
    {
    	myClass.cancel(true);
        super.onDestroy();
    }    
}

