package com.ninjitsuware.notepad;


import java.util.ArrayList;

import com.stacservices.stealthmessenger.db.SsmsAccount.SsmsAccounts;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;

public class StealthMessengerAccounts extends Activity 
{

	ArrayList<String> acctList = new ArrayList<String>();
	private final int CMDELETE_CLICK = 0;
   
    private SimpleCursorAdapter mAdapter;
    
    
	public void onCreate(Bundle savedInstanceState) 
    {
        
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.accounts);

        //this.findViewById( R.id.AccountsList ).setOnClickListener( lstAccountsListener );
        this.findViewById( R.id.btnAddAcct ).setOnClickListener( btnAddListener );
        
        
        StealthMessengerSettings.activitySettings(this);      
        
        ListView lstAccounts =  (ListView)StealthMessengerAccounts.this.findViewById(R.id.lstAccounts);
        // Get ContentResolver object for pushing encrypted SMS to incoming folder
		lstAccounts.setOnItemClickListener(lstItemClickListener);
		registerForContextMenu(lstAccounts);
		
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, new String[] { SsmsAccounts.SSMSACCT_NAME, SsmsAccounts.SSMSACCT_PHONE, SsmsAccounts.SSMSACCT_ID }, null, null, null);
        startManagingCursor(c);
        
        // the desired columns to be bound
        String[] columns = new String[] { SsmsAccounts.SSMSACCT_NAME, SsmsAccounts.SSMSACCT_PHONE };
        // the XML defined views which the data will be bound to
        int[] to = new int[] { R.id.acctlisttext1, R.id.acctlisttext2};

        // create the adapter using the cursor pointing to the desired data as well as the layout information
        mAdapter = new SimpleCursorAdapter(this, R.layout.acctlist, c, columns, to);
                
		lstAccounts.setAdapter( mAdapter );        
                


    }
        
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	mAdapter.notifyDataSetChanged();
        /*ListView lstAccounts =  (ListView)StealthMessengerAccounts.this.findViewById(R.id.lstAccounts);

        // Get ContentResolver object for pushing encrypted SMS to incoming folder
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, null, null, null, null);
        
        // the desired columns to be bound
        String[] columns = new String[] { SsmsAccounts.SSMSACCT_NAME, SsmsAccounts.SSMSACCT_PHONE };
        // the XML defined views which the data will be bound to
        int[] to = new int[] { R.id.listtext2, R.id.listtext3 };

        // create the adapter using the cursor pointing to the desired data as well as the layout information
        mAdapter = new SimpleCursorAdapter(this, R.layout.listtext, c, columns, to);
                
		lstAccounts.setAdapter( mAdapter );        
                
		lstAccounts.setOnItemClickListener(lstItemClickListener);
		registerForContextMenu(lstAccounts);
		c.close();*/
    }	
	
	private OnClickListener btnAddListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	Intent AcctModIntent = new Intent(StealthMessengerAccounts.this, StealthMessengerAccountActivity.class);
    		
    		Bundle b = new Bundle();
        	b.putInt("mode", 1);
        	AcctModIntent.putExtras(b);
        	StealthMessengerAccounts.this.startActivityForResult(AcctModIntent, 1);        	
        }
    };
    
    private OnItemClickListener lstItemClickListener = new OnItemClickListener() 
    {
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) 
        {
        	/*
        	String strItem = acctList.get(pos);
        	String strTemp[] = strItem.split("\n");        	
        	String strPhone = "";
        	int acct = 0;
        	if(strTemp.length > 0)
        		strPhone = strTemp[1];
        	
        	ContentResolver resolver = StealthMessengerAccounts.this.getContentResolver();
        	
        	Cursor c = resolver.query(SsmsAccounts.CONTENT_URI, null,null, null, null); //new String[] {SsmsAccounts.SSMSACCT_ID, SsmsAccounts.SSMSACCT_PHONE}, SsmsAccounts.SSMSACCT_PHONE + "='" + address + "'"            	
            
        	int phoneIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_PHONE);
        	int acctIdx = c.getColumnIndex(SsmsAccounts.SSMSACCT_ID);
        	        	        	        
        	if ( phoneIdx >= 0 && c.moveToFirst() )
        	{            	        
        		do
        		{
        			String acctPhone = c.getString( phoneIdx );
        			String accountIdx = c.getString(acctIdx);	        			
        			
                	if(acctPhone.equals(strPhone))
                	{
                		acct = Integer.parseInt(accountIdx);
                		break;
                	}	        			
        		}
        		while( c.moveToNext() );

        	}          
        	
        	c.close();        	
        	*/
        
        	Intent AcctModIntent = new Intent(StealthMessengerAccounts.this, StealthMessengerAccountActivity.class);
    	
        	Bundle b = new Bundle();
        	b.putInt("mode", 2);
        	b.putLong("acctid", id);        	
        	AcctModIntent.putExtras(b);
        	StealthMessengerAccounts.this.startActivityForResult(AcctModIntent, 1);        	
        }
    };    
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) 
    {
      if (v.getId()==R.id.lstAccounts) 
      {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Account Options");
        String[] menuItems = getResources().getStringArray(R.array.acctmenu);
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
	        deleteAcct(info.id);
	        return true;
	      }	   
	      
	      default:
	      {
	        return super.onContextItemSelected(item);
	      }      
      }
    }    
    
    private void deleteAcct(long rowId) 
    {
    	ContentResolver resolver = getContentResolver();
    	resolver.delete(SsmsAccounts.CONTENT_URI, SsmsAccounts.SSMSACCT_ID+"="+rowId, null);
    	mAdapter.notifyDataSetChanged();
    }        
}

