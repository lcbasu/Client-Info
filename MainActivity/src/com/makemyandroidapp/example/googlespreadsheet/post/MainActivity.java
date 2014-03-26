package com.makemyandroidapp.example.googlespreadsheet.post;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	
	final String myTag = "DocsUpload";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i(myTag, "OnCreate()");
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				postData();
				
			}
		});
		t.start();
	}

	

	public void postData() {

		String fullUrl = "https://docs.google.com/forms/d/152nOjJC0hET-YV0TQHxDNuSdTtqWVNs8Etu3s0MtCsI/formResponse";
		HttpRequest mReq = new HttpRequest();
		String col1 = "Lokesh";
		String col2 = "Basu";
		
		String data = "entry_876925703=" + URLEncoder.encode(col1) + "&" + 
					  "entry_2081306665=" + URLEncoder.encode(col2);
		String response = mReq.sendPost(fullUrl, data);
		Log.i(myTag, response);
	} 

}
