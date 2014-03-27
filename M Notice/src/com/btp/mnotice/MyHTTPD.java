package com.btp.mnotice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.btp.mnotice.HttpRequest;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class MyHTTPD extends NanoHTTPD 
{
	InputStream data;
	int counter ;
	Activity c;
	WifiManager wifi;
	String word = "";
	String my_ip="";
	String clientip="";
	String query="";

	public String my_path="";
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static int count = 0 ; 
	public static String client_list="";
	public static String filename="default";
	public static String[] max_cpu,min_cpu,ram;
	public static ArrayList<HashMap<String, String>> song_list = new ArrayList<HashMap<String, String>>();

	String deviceName = "";
	String latitude = "";
	String longitude = "";
	String loginTimestamp = "";

	static String finalResult = "";

	public MyHTTPD(Activity a, String ipaddr, String path) throws IOException
	{
		super(ipaddr, 8765);
		c = a;
		my_ip = ipaddr;
		my_path = path;
	}

	@Override
	public Response serve(String uri, Method method, Map<String, String> header, final Map<String, String> parms, Map<String, String> files)
	{
		try 
		{
			if(parms.get("getinfo") != null)
			{
				counter=0;
				clientip=parms.get("clientip");
				query=parms.get("query");

				c.runOnUiThread(new Runnable() 
				{
					@SuppressWarnings("deprecation")
					public void run()
					{
						try 
						{
							Toast.makeText(c,"User details received : "+query, Toast.LENGTH_LONG).show();
							new sendinfotoclient(clientip,query).execute();
						}
						catch(Exception e)
						{

						}
					}
				});}
			if(parms.get("query_return") != null)
			{
				final String result=parms.get("result");
				final String resultListView=parms.get("resultListView");
				final String totalResultFound=parms.get("totalResultFound");

				c.runOnUiThread(new Runnable()
				{
					@SuppressWarnings("deprecation")
					public void run()
					{
						try 
						{
							finalResult = "";
							finalResult = finalResult.concat(resultListView);

							//							if(finalResult.length() == 0)
							//								Toast.makeText(c,"Empty Search Result", Toast.LENGTH_LONG).show();
							//							if(finalResult.length() > 0)
							//								Toast.makeText(c,"Total Result Found : "+ totalResultFound, Toast.LENGTH_LONG).show();

							MainActivity.text.setText("User details : "+ totalResultFound + "\n" +result);
							//SearchActivity.text.setText("Total Result Found : "+ totalResultFound + "\n" +result);

						}

						catch(Exception e)
						{

						}
					}
				});}
		} 
		catch (Exception e) 
		{

		}
		return null;
	}

	public class sendinfotoclient extends AsyncTask<String, String, String> 
	{
		String clientip="";
		String query="";

		public sendinfotoclient(String clientip11,String query11)
		{
			clientip=clientip11;
			query=query11;
		}

		protected void onPreExecute() 
		{
			//Toast.makeText(c, "Sending..", Toast.LENGTH_LONG).show();
		}

		protected void onPostExecute(String result)
		{

		}

		@Override
		protected String doInBackground(String... arg0)
		{
			String info="";
			String infoListView="";
			int totalResultFound = 0;
			String totalResultFoundStr = ""; 

			//clientip = query + clientip;

			try
			{
				String[] split=query.split(":");

				deviceName = split[0];
				latitude = split[1];
				longitude = split[2];
				loginTimestamp = split[3];
				
				File set1 = new File(Environment.getExternalStorageDirectory().getPath() + "/clientinfo.txt");
				FileWriter fw = new FileWriter(set1.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(deviceName + "\t" + latitude + "\t" + longitude + "\t" + loginTimestamp + "\n");
				postData(deviceName, latitude, longitude, loginTimestamp);
				bw.close();
				if(set1.canRead())
				{

					//					if(split[0].contentEquals(query))
					//					{
					//						totalResultFound++;
					//						info = info + query +  " : "+ split[1]+"\n";
					//						infoListView = infoListView + query  + " : "+ split[1] +":" + split[2] +"#";
					//					}

					//					BufferedReader buffer1 = new BufferedReader(new FileReader(set1));
					//					String l = "";
					//					while ((l = buffer1.readLine()) != null) 
					//					{
					//						String[] split=l.split(":");
					//						if(split[0].contentEquals(query))
					//						{
					//							totalResultFound++;
					//							info = info + query +  " : "+ split[1]+"\n";
					//							infoListView = infoListView + query  + " : "+ split[1] +":" + split[2] +"#";
					//						}
					//					}
					//					totalResultFoundStr = ""+totalResultFound;
					//					buffer1.close();
				}
			}
			catch(Exception e)
			{

			}
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://"+clientip+":8765");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("query_return", "1"));
				nameValuePairs.add(new BasicNameValuePair("result",query));
				nameValuePairs.add(new BasicNameValuePair("resultListView",infoListView));
				nameValuePairs.add(new BasicNameValuePair("totalResultFound",totalResultFoundStr));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httpclient.execute(httppost);
			}
			catch(Exception e)
			{

			}
			return clientip;
		}
	}
	
	public void postData(String deviceName, String latitude, String longitude, String loginTimestamp) {

		String fullUrl = "https://docs.google.com/forms/d/16cIzpzmCZUyU1_BD7xSmdbUDm7Sif9Aji_j1h0-zdfU/formResponse";
		HttpRequest mReq = new HttpRequest();
		
		String data = "entry_324035242=" + URLEncoder.encode(deviceName) + "&" + 
					  "entry_272758658=" + URLEncoder.encode(latitude) + "&" +
					  "entry_1633202945=" + URLEncoder.encode(longitude) + "&" + 
					  "entry_631798686=" + URLEncoder.encode(loginTimestamp);
		String response = mReq.sendPost(fullUrl, data);
	} 
}