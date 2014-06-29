package com.ninjitsuware.notepad;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.stacservices.stealthmessenger.db.Ssms.Ssmss;
import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;

public class StealthMessengerActivity extends Activity 
{
	private int acctId;
	private String phoneNumber;
	private boolean recEncrypted;
	private boolean sendEncrypted;
	private String strEncKey;
	private final int CMDELETE_CLICK = 0;
	private final int CMDELETEALL_CLICK = 1;
	private final int CMDCOPYTEXT_CLICK = 2;
	private final int CMREFRESH_CLICK = 3;

    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);                
        Bundle b = getIntent().getExtras();
        acctId = b.getInt("acctid");
        phoneNumber = b.getString("phonenum");
        setTheme(android.R.style.Theme_Light);
        setContentView(R.layout.main);

        loadSmsListView();
        
        this.findViewById(R.id.btnSend).setOnClickListener(BtnSendSSMSListener);
        
        StealthMessengerSettings.activitySettings(this);      
        
        ListView lstSms = (ListView)findViewById(R.id.SMSList);
        registerForContextMenu(lstSms);
   
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("com.ninjitsuware.notepad.NEW_MESSAGE");
        iFilter.addAction("com.ninjitsuware.notepad.SEND_MESSAGE");
        registerReceiver(SSMSreceiver, iFilter);
        
        
        ContentResolver resolver = getBaseContext().getContentResolver();
		
		Cursor d = resolver.query(SsmsAccounts.CONTENT_URI, null,SsmsAccounts.SSMSACCT_ID +"=" +acctId, null, null);             	
		
		if(d.moveToFirst())
		{
		
			int recEncIdx = d.getColumnIndex(SsmsAccounts.SSMSACCT_RECEIVE_ENC);
			int sendEncIdx = d.getColumnIndex(SsmsAccounts.SSMSACCT_SEND_ENC);
			int encKeyIdx = d.getColumnIndex(SsmsAccounts.SSMSACCT_ENC_KEY);
		
			recEncrypted = Boolean.parseBoolean(d.getString(recEncIdx));
			sendEncrypted = Boolean.parseBoolean(d.getString(sendEncIdx));
			strEncKey = d.getString(encKeyIdx).toString().trim();
		}
		else
		{
			recEncrypted = false;
			sendEncrypted = false;
			strEncKey = "";
		}
		
		//Toast.makeText(getApplicationContext(), recEncIdx + " " + sendEncIdx, Toast.LENGTH_SHORT).show();
		
		d.close();
    	        
    }
    
    private BroadcastReceiver SSMSreceiver = new BroadcastReceiver() 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
        	
        	if(intent.getAction().equals("com.ninjitsuware.notepad.SEND_MESSAGE"))
        	{
        		Bundle b = intent.getExtras();
        		String msgid = b.getString("msgid");
        		ContentResolver resolver = getContentResolver();
        		switch(getResultCode())
        		{
        			case Activity.RESULT_OK:
        			{
                        ContentValues values = new ContentValues();
                        values.put( Ssmss.STATUS, "Sent");
        				resolver.update(Ssmss.CONTENT_URI,values,Ssmss.SSMS_ID+"=" + msgid,null);
        				break;
        			}
        			
        			default:
        			{
                        ContentValues values = new ContentValues();
                        values.put( Ssmss.STATUS, "FAILED! NOT SENT: " + getResultCode());
        				resolver.update(Ssmss.CONTENT_URI,values,Ssmss.SSMS_ID+"=" + msgid,null);        				
        				break;
        			}
        			
        		}
        	}
        	
			loadSmsListView(); 			
        }
    };
    
    private OnClickListener BtnSendSSMSListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
            String phoneNo = phoneNumber;
            EditText txtMessage = (EditText) findViewById(R.id.txtSSMS);
            String message = txtMessage.getText().toString();  
            
            if (phoneNo.length()>0 && message.length()>0)                
                sendSSMS(phoneNo, message);                
            else
                Toast.makeText(getBaseContext(), 
                    "Please enter a message to send.", 
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void sendSSMS(String phoneNumber, String message)
    {                
    	ContentResolver resolver = getContentResolver();
        SmsManager ssms = SmsManager.getDefault();
        EditText txtMessage = (EditText) findViewById(R.id.txtSSMS);
        txtMessage.setText("");

        String plainTextMessage = message;
        
        byte[] encMsg = {' '};
        
        if(sendEncrypted)        	
        {
        	try
        	{
        		message = StealthMessengerEncryption.encrypt(message, strEncKey);
        		plainTextMessage += " [Encrypted]";
        	}
        	catch(Exception ex)
        	{
        		message = message += " [Encryption Failed]";
        	}
        }
        else
        {
        	plainTextMessage += " [Unencrypted]";
        }
               
        
        
        ArrayList<String> smmsmessage = ssms.divideMessage(message);
    	ArrayList<PendingIntent> sentIntent = new ArrayList<PendingIntent>();

    	ContentValues values = new ContentValues();
        values.put( Ssmss.ADDRESS, "Me" );
        values.put( Ssmss.SSMSACCT_ID, acctId );        
        values.put( Ssmss.DATE,  System.currentTimeMillis());
        values.put( Ssmss.READ, Ssmss.MESSAGE_IS_NOT_READ );
        values.put( Ssmss.STATUS, "Sending...");
        values.put( Ssmss.TYPE, Ssmss.MESSAGE_TYPE_INBOX );
        values.put( Ssmss.SEEN, Ssmss.MESSAGE_IS_NOT_SEEN );
        values.put(Ssmss.BODY, plainTextMessage );
        
        Uri newUri = resolver.insert( Ssmss.CONTENT_URI, values );
        List<String> uriList = newUri.getPathSegments();
    	
	    for(int i = 0; i < smmsmessage.size();i++)
	    {

            String msgid = uriList.get(uriList.size()-1);                      
            Bundle b = new Bundle();
            b.putString("msgid", msgid);            
        	PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, 
        			new Intent("com.ninjitsuware.notepad.SEND_MESSAGE").putExtras(b),PendingIntent.FLAG_ONE_SHOT);            
            sentIntent.add(contentIntent);
	    }
        
        if(smmsmessage.size() > 1)
        {
        	if(sendEncrypted)
        	{	
        		Toast.makeText(getApplicationContext(), "The encrypted message length will be too long. Please break up into smaller messages to send.", Toast.LENGTH_SHORT).show();
        	}
        	else
        	{
        		ssms.sendMultipartTextMessage(phoneNumber, null, smmsmessage, sentIntent, null);
        	}
        }
        else
        {        	        		
        		ssms.sendTextMessage(phoneNumber, null, message, sentIntent.get(0), null);        		
        }
        loadSmsListView();
    }    
    
    ArrayList<HashMap<String,String>> smsList = new ArrayList<HashMap<String,String>>();

    private void loadSmsListView()
    {
    	    	
		ContentResolver contentResolver = getContentResolver();		
		
		Cursor cursor = contentResolver.query( Ssmss.CONTENT_URI, new String[] {Ssmss.SSMSACCT_ID, Ssmss.BODY, Ssmss.ADDRESS, Ssmss.DATE, Ssmss.SSMS_ID, Ssmss.STATUS}, Ssmss.SSMSACCT_ID + "=" + acctId, null, null);
		Cursor acctCursor = contentResolver.query(SsmsAccounts.CONTENT_URI, new String[] {SsmsAccounts.SSMSACCT_ID, SsmsAccounts.SSMSACCT_NAME, SsmsAccounts.SSMSACCT_PHONE}, SsmsAccounts.SSMSACCT_ID + "=" + acctId, null,null);
		
		
		int indexBody = cursor.getColumnIndex( Ssmss.BODY );
		int indexAddr = cursor.getColumnIndex( Ssmss.ADDRESS );
		int timeAddr = cursor.getColumnIndex( Ssmss.DATE);
		int ssmsidIdx = cursor.getColumnIndex( Ssmss.SSMS_ID);
		int statusIdx = cursor.getColumnIndex( Ssmss.STATUS);
		int acctNameIdx = acctCursor.getColumnIndex(SsmsAccounts.SSMSACCT_NAME);
		int acctPhoneIdx = acctCursor.getColumnIndex(SsmsAccounts.SSMSACCT_PHONE);
		
		
		ListView smsListView = (ListView) findViewById( R.id.SMSList );
		
		smsList.clear();
		
		if ( !(indexBody < 0 || !cursor.moveToFirst()) )
		{			
			String sender = "";
			String sendName = "";
			String sendString = "";			
		    sender = cursor.getString( indexAddr );
		    if(acctNameIdx < 0 || !acctCursor.moveToFirst())
		    	sendName = "";
		    else
		    	sendName = acctCursor.getString( acctNameIdx );
		    
		    sender = acctCursor.getString( acctPhoneIdx );
	
		    if(sendName.trim() == "")
		    	sendString = sender;
		    else
		    	sendString = sendName + "<" + sender + ">";	    
		    
			do
			{
				String body = cursor.getString( indexBody );
				String status = cursor.getString(statusIdx);
				String strSender = sendString;
				
				if(cursor.getString(indexAddr).equals("Me"))
					strSender = "Me";
				
				DateFormat df;
				df = new SimpleDateFormat("MM/dd/yyyy kk:mm");
				String timeRec = cursor.getString( timeAddr );			
			    String timeStamp = df.format(Long.parseLong(timeRec));   
			   
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("sender", strSender + ":");
				map.put("body", body);
				if(status.equals("Sending..."))
					map.put("timestamp", status);
				else
					map.put("timestamp", status+": "+timeStamp);
				map.put("ssmsid", cursor.getString(ssmsidIdx));
				smsList.add(map);						
			}
			while( cursor.moveToNext() );
	
			cursor.close();
			acctCursor.close();
						
		}			
		
		// the from array specifies which keys from the map
		// we want to view in our ListView
		String[] from = {"sender", "body", "timestamp"};
		 
		// the to array specifies the TextViews from the xml layout
		// on which we want to display the values defined in the from array
		int[] to = {R.id.listtext1, R.id.listtext2, R.id.listtext3};
		
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);		        
    	String fontSize = sharedPrefs.getString("Font_Size", "2");    	
		int fontValue = R.layout.listtext_medium;
				
    	if(fontSize.equals("1")){
	    		fontValue = R.layout.listtext;	    
	    }
    	else if(fontSize.equals("3")){    		    	
	    		fontValue = R.layout.listtext_large;
	    }
    	else{
	    		fontValue = R.layout.listtext_medium;
	    	
    	}
    	
		// create the adapter and assign it to the listview
		SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), smsList, fontValue, from, to);
		smsListView.setAdapter(adapter);					
		smsListView.setStackFromBottom(true);
		
		//smsListView.setOnItemClickListener( this );            	
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) 
    {
      if (v.getId()==R.id.SMSList) 
      {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("SSMS Options");
        String[] menuItems = getResources().getStringArray(R.array.ssmsmenu);
        for (int i = 0; i<menuItems.length; i++) 
        {
          menu.add(0, i, i, menuItems[i]);
        }
      }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId())
      {
	      case CMDELETE_CLICK:
	      {
	        deleteSms(info.id, CMDELETE_CLICK);
	        return true;
	      }
	      case CMDELETEALL_CLICK:
	      {
	        deleteSms(info.id, CMDELETEALL_CLICK);
	        return true;
	      }
	      case CMDCOPYTEXT_CLICK:
	      {
	        copySmsTxt(info.id);
	        return true;
	      }	      
	      case CMREFRESH_CLICK:
	      {
	    	loadSmsListView();
	        return true;
	      }	      
	      
	      default:
	      {
	        return super.onContextItemSelected(item);
	      }      
      }
    }    
    
    private void deleteSms(long rowId, int mode)
    {
    	if(mode == CMDELETEALL_CLICK)
    	{
    		ContentResolver resolver = getContentResolver();
    		
    		resolver.delete(Ssmss.CONTENT_URI, Ssmss.SSMSACCT_ID + "=" + acctId, null);
    		loadSmsListView();
    	}
    	else if(mode == CMDELETE_CLICK)
    	{
    		ContentResolver resolver = getContentResolver();
    		ListView lvwSms = (ListView)findViewById(R.id.SMSList);
    		HashMap<String,String> map = (HashMap<String,String>)lvwSms.getItemAtPosition((int)rowId);
    		resolver.delete(Ssmss.CONTENT_URI, Ssmss.SSMSACCT_ID + "=" + acctId + " AND " + Ssmss.SSMS_ID + "=" + map.get("ssmsid"), null);
    		loadSmsListView();    		
    	}
    }

    private void copySmsTxt(long rowId)
    {
		ListView lvwSms = (ListView)findViewById(R.id.SMSList);    	
		HashMap<String,String> map = (HashMap<String,String>)lvwSms.getItemAtPosition((int)rowId);
		String text = map.get("body");
		
		ClipboardManager clipboard = (ClipboardManager)
        getSystemService(this.CLIPBOARD_SERVICE);
		clipboard.setText(text);
		
    }
    
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

    @Override
    protected void onDestroy() 
    {        
        unregisterReceiver(SSMSreceiver);
       
        super.onDestroy();   
    }
    
}