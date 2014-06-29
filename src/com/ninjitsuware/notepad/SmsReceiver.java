package com.ninjitsuware.notepad;

import com.stacservices.stealthmessenger.db.Ssms.Ssmss;
import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class SmsReceiver extends BroadcastReceiver 
{
	// All available column names in SMS table
    // [_id, thread_id, address, 
	// person, date, protocol, read, 
	// status, type, reply_path_present, 
	// subject, body, service_center, 
	// locked, error_code, seen]
	
	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = Ssmss.CONTENT_URI.toString();
	
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;
    
    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;
    
    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;
	    
    private static final int HELLO_ID = 0000;
       
    private boolean recEncrypted;
    private String strEncKey;
    
    private Context myContext;
    
	public void onReceive( Context context, Intent intent ) 
	{
		myContext = context;
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean showTaskbar = sharedPrefs.getBoolean("taskbar_on", false);		
		String appTitle = sharedPrefs.getString("App_Title", "SSMS");
		String taskbarTxt = sharedPrefs.getString("taskbar_text", appTitle + " is protecting your device");
		
		String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
    	
    	int icon = R.drawable.icon;        			   // icon from resources
    	CharSequence tickerText = taskbarTxt;              // ticker-text
    	
    	long when = System.currentTimeMillis();         // notification time
    	CharSequence contentTitle = appTitle;  			// message title
    	CharSequence contentText = taskbarTxt;  	 	// message text

    	Intent notificationIntent = 
    		new Intent(context, StealthMainActivity.class);
    	PendingIntent contentIntent = 
        		PendingIntent.getActivity(context, 0, notificationIntent, 0);
    	
    	// the next two lines initialize the Notification, using the configurations above
    	Notification notification = new Notification(icon, tickerText, when);
    	
		
		if(showTaskbar)
		{

	    	notification.flags |= Notification.FLAG_NO_CLEAR;
	    	notification.flags |= Notification.FLAG_ONGOING_EVENT;                
	    
	    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	    	
	    	mNotificationManager.notify(HELLO_ID, notification); 
		}
		
		// Get SMS map from Intent
        Bundle extras = intent.getExtras();        
        
        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
            
            ContentResolver resolver = context.getContentResolver();
            
            for ( int i = 0; i < smsExtra.length; ++i )
            {
            	SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
            	            	
            	String address = sms.getOriginatingAddress();
            	         
            	Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, null,null, null, null); //new String[] {SsmsAccounts.SSMSACCT_ID, SsmsAccounts.SSMSACCT_PHONE}, SsmsAccounts.SSMSACCT_PHONE + "='" + address + "'"            	
            	             	           	            	            	            	            
            	int phoneIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_PHONE);
            	int acctIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_ID);
            	int acctNmIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_NAME);
    			int recEncIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_RECEIVE_ENC);
    			int encKeyIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_ENC_KEY);
    			String acctName = ""; 
    			
            	if ( phoneIdx >= 0 && c.moveToFirst() )
            	{           
            		boolean sendBroadcast = false;            		
            		
	        		do
	        		{
	        			String acctPhone = c.getString( phoneIdx );
	        			String accountIdx = c.getString(acctIdx);	        			
	        			
	                	if(address.trim().replaceAll("-", "").equals(acctPhone.trim().replaceAll("-", "")) || 
	                			address.trim().replaceAll("-", "").equals("1" + acctPhone.trim().replaceAll("-", "")) ||
	                			("1" + address.trim().replaceAll("-", "")).equals(acctPhone.trim().replaceAll("-", "")))
	                	{ 
	                		this.abortBroadcast();
	                		strEncKey = c.getString(encKeyIdx).toString().trim();
	                		recEncrypted = Boolean.parseBoolean(c.getString(recEncIdx));
	                		putSmsToDatabase( resolver, sms, accountIdx);
	                		sendBroadcast = true;
	                		acctName = c.getString(acctNmIdx);
	                	}	        			
	        		}
	        		while( c.moveToNext() );

	        		if(sendBroadcast)
	        		{	                	
	                	boolean notify = sharedPrefs.getBoolean("notify_on", false);
	                	
	        			if(notify)
	        			{
	        				String noteMsg = sharedPrefs.getString("SSMS_NotificationMsg", "SSMS Activity").replace("{0}", acctName);
	        				String noteSound = sharedPrefs.getString("SSMSNotify_ringtone", "");
	        				boolean vibrate = sharedPrefs.getBoolean("SSMSNotify_vibrate", false);
		        			ns = Context.NOTIFICATION_SERVICE;
		        	    	mNotificationManager = (NotificationManager) context.getSystemService(ns);
		        	    	
		        	    	icon = R.drawable.notify;        			   // icon from resources
		        	    	tickerText = "SSMS";              // ticker-text
		        	    	
		        	    	when = System.currentTimeMillis();         // notification time
		        	    	contentTitle = "SSMS";  			// message title
		        	    	contentText = noteMsg;   // message text
	
		        	    	notificationIntent = 
		        	    		new Intent(context, StealthMainActivity.class);
		        	    	contentIntent = 
		        	        		PendingIntent.getActivity(context, 0, notificationIntent, 0);
		        	    	
		        	    	// the next two lines initialize the Notification, using the configurations above		        	    	
		        	    	notification = new Notification(icon, tickerText, when);

		        	    	if(noteSound.length() > 0)
		        	    	{
		        	    		notification.sound = Uri.parse(noteSound);		        	    		
		        	    		if(vibrate)
		        	    			notification.defaults |= Notification.DEFAULT_VIBRATE;
		        	    	}
		        	    	
		        	    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
		        	    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		        	    	
		        	    	mNotificationManager.notify(1, notification); 	        			
	        			}
	        			
	        			Intent broadcastIntent = new Intent(); 
	        			broadcastIntent.setAction("com.ninjitsuware.notepad.NEW_MESSAGE");  
	        			context.sendBroadcast(broadcastIntent);  	        			
	        		}
            	}          
            	
            	c.close();
            }           
                    	
        }
         
	}
	
	private void putSmsToDatabase( ContentResolver resolver, SmsMessage sms, String acctIdx)
	{
		// Create SMS row
        ContentValues values = new ContentValues();
        values.put( Ssmss.ADDRESS, sms.getOriginatingAddress() );
        values.put( Ssmss.SSMSACCT_ID, acctIdx );   
        values.put( Ssmss.DATE, System.currentTimeMillis() );
        values.put( Ssmss.READ, MESSAGE_IS_NOT_READ );
        values.put( Ssmss.STATUS, "Received" );
        values.put( Ssmss.TYPE, MESSAGE_TYPE_INBOX );
        values.put( Ssmss.SEEN, MESSAGE_IS_NOT_SEEN );  
        if(recEncrypted)
        {
        	try
        	{
        		values.put( Ssmss.BODY, StealthMessengerEncryption.decrypt(sms.getMessageBody(), strEncKey) + " [Decrypted]");
        	}
        	catch(Exception ex)
        	{
        		values.put( Ssmss.BODY, sms.getMessageBody() + " " + ex.getMessage() );	
        		
        	}
        }
        else
        	values.put( Ssmss.BODY, sms.getMessageBody() );
        
        // Push row into the SMS table
        try
        {
        	resolver.insert( Uri.parse( SMS_URI ), values );
        }
        catch(android.database.SQLException e)
        {
        	e.printStackTrace();
        }
	}
}
