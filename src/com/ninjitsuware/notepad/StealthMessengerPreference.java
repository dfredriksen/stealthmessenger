package com.ninjitsuware.notepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.Preference; 
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StealthMessengerPreference extends Preference 
{
	public StealthMessengerPreference(Context context) 
	{
		super(context);
	}
		 
	public StealthMessengerPreference(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
	}
		 
	public StealthMessengerPreference(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent)
	{
		
	   LinearLayout layout = new LinearLayout(getContext());
	   
	   LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
	                                       LinearLayout.LayoutParams.WRAP_CONTENT,
	                                                LinearLayout.LayoutParams.WRAP_CONTENT);
	   params1.gravity = Gravity.LEFT;
	   params1.weight  = 1.0f;
	   
	   
	   LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
	                                        80,
	                                        LinearLayout.LayoutParams.WRAP_CONTENT);
	   params2.gravity = Gravity.RIGHT;
	  
	   
	   LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
	                                       30,
	                                       LinearLayout.LayoutParams.WRAP_CONTENT);
	   params3.gravity = Gravity.CENTER;
	   
	   
	   layout.setPadding(15, 5, 10, 5);
	   layout.setOrientation(LinearLayout.HORIZONTAL);
	   
	   TextView view = new TextView(getContext());
	   view.setText(getTitle());
	   view.setTextSize(18);
	   view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	   view.setGravity(Gravity.LEFT);
	   view.setLayoutParams(params1);
	   view.setOnClickListener( AccountClickListener );
	   
	   layout.addView(view);
	   layout.setId(android.R.id.widget_frame);
	   	  
	   return layout; 
	 }
	
    private OnClickListener AccountClickListener = new OnClickListener() 
    {
        public void onClick(View v) 
        {
        	Intent AccountIntent = new Intent(getContext(), StealthMessengerAccounts.class);
        	getContext().startActivity(AccountIntent);
        	
        }
    };
    
    
}