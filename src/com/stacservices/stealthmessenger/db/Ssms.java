package com.stacservices.stealthmessenger.db;

import android.net.Uri;
import android.provider.BaseColumns;
import com.stacservices.stealthmessenger.providers.SSMSProvider;;

public class Ssms 
{
	
	public Ssms() 
	{
	}

	public static final class Ssmss implements BaseColumns 
	{
	    public static final int MESSAGE_TYPE_INBOX = 1;
	    public static final int MESSAGE_TYPE_SENT = 2;
	    
	    public static final int MESSAGE_IS_NOT_READ = 0;
	    public static final int MESSAGE_IS_READ = 1;
	    
	    public static final int MESSAGE_IS_NOT_SEEN = 0;
	    public static final int MESSAGE_IS_SEEN = 1;
		
		private Ssmss() 
		{
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ SSMSProvider.AUTHORITY 
				+ "/ssms");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.stacservices.ssms";
		public static final String SSMS_ID = "_id";
		public static final String SSMSACCT_ID = "acctid";		
		public static final String ADDRESS = "address";
		public static final String DATE = "dt";		
		public static final String READ = "read";		
		public static final String STATUS = "status";		
		public static final String TYPE = "type";		
		public static final String SEEN = "seen";		
		public static final String BODY = "body";		
		
	}
}
