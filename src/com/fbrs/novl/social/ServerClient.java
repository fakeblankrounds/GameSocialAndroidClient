package com.fbrs.novl.social;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import android.util.Log;

public class ServerClient {
	
	public static String EC2Server = null;
	
	private static void getServer()
	{
	
		try {
			URL u = new URL ( "http://www.fakeblankrounds.com/ec2.txt" );
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
			EC2Server = input.readLine();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static String sendRequest(String request)
	{		
		 try {
			 if(EC2Server == null)
				 getServer();
			Socket s = new Socket(EC2Server ,8888);
			OutputStream out = s.getOutputStream();
			
			PrintWriter send = new PrintWriter(out);
			//send.println("/Friends/get/" + SocialActivity.Username + "/" + SocialActivity.password + "/");
			send.println(request);
			//send.println("GET /index http/1.1");
			send.println();
			send.flush();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String t = "";
			String st = input.readLine();
			while(st != null)
			{
				t += st;
				st = input.readLine();
			}
			s.close();
			return t;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e("Nvl", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Nvl", e.getMessage());
			e.printStackTrace();
		}
		return null;
			 
	}
	
	public static void StartNewThreadedRequest(final String request, final IServerCallback callback)
	{
		 new Thread(new Runnable() {
			    public void run() {
			     callback.run(sendRequest(request));
			    }
			  }).start();
	}

}
