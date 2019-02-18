package com.nexstreaming.app.apis;

import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class PlayerSampleUtils {
	private static PlayerSampleUtils sharedUtils = null;
	private static final String LOG_TAG = "NexPlayerSample";

	private PlayerSampleUtils() {
	}

	public static synchronized PlayerSampleUtils sharedInstance() {
		if ( sharedUtils == null ) {
			sharedUtils = new PlayerSampleUtils();
		}
		return sharedUtils;
	}

	public void logHTTPResponse(String strResponse) {
		ArrayList<String> strCookie = new ArrayList<String>();
		StringTokenizer tk = new StringTokenizer(strResponse, "\n");
		while(tk.hasMoreTokens())
		{
			String tmp = tk.nextToken();
			Log.d(LOG_TAG, "onHTTPResponse : " + tmp);
			if(tmp.startsWith("Set-Cookie"))
			{
				strCookie.add(tmp);
			}
		}
		for(int i=0;i<strCookie.size();i++)
		{
			Log.d(LOG_TAG, "COOKIE: " + strCookie.get(i));
		}
	}
	
	public void logHTTPRequest(String strRequest) {
		StringTokenizer tk = new StringTokenizer(strRequest, "\n");
		while(tk.hasMoreTokens())
		{
			String tmp = tk.nextToken();
			Log.d(LOG_TAG, "onHTTPRequest : " + tmp);
		}
	}
	
	public String[] concatStringArrays(String a[], String b[]) {
		
		if( a == null ) {
			a = new String[0];
		}
		if( b == null ) {
			b = new String[0];
		}
		String[] newArray =
				new String[a.length + b.length];

		int i = 0;
		int newArrayIdx = 0;

		for( i=0; i<a.length; i++ ) {
			newArray[newArrayIdx++] = a[i];
		}
		for( i=0; i<b.length; i++ ) {
			newArray[newArrayIdx++] = b[i];
		}
		return newArray;
	}
	
}
