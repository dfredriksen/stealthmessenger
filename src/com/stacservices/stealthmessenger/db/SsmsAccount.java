package com.stacservices.stealthmessenger.db;

import android.net.Uri;
import android.provider.BaseColumns;
import com.stacservices.stealthmessenger.providers.SSMSAccountProvider;

public class SsmsAccount 
{
	
	public SsmsAccount() 
	{
	}

	public static final class SsmsAccounts implements BaseColumns 
	{
		private SsmsAccounts() 
		{
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ SSMSAccountProvider.AUTHORITY 
				+ "/ssmsaccount");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.stacservices.ssmsaccount";
		public static final String SSMSACCT_ID = "_id";
		public static final String SSMSACCT_NAME = "acctname";
		public static final String SSMSACCT_PHONE = "phone";		
		public static final String SSMSCT_ID = "contact_id";
		public static final String SSMSPIN = "pin";
		public static final String SSMSACCT_RECEIVE_ENC = "receive_enc";	
		public static final String SSMSACCT_SEND_ENC = "send_enc";
		public static final String SSMSACCT_ENC_KEY = "enc_key";
	}
}
