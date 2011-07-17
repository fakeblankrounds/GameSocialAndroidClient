package com.fbrs.novl.social.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.fbrs.novl.social.FriendListAdapter;
import com.fbrs.novl.social.R;
import com.fbrs.novl.social.ServerClient;
import com.fbrs.novl.social.R.layout;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsList extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.test);
		this.setTitle("Friends List");
		String Friends =  this.getIntent().getExtras().getString("friends"); //ServerClient.sendRequest("/Friends/get/" + SocialActivity.Username + "/" + SocialActivity.password + "/");
		if(Friends == null)
		{
			Friends = "Cannot connect to internet-null-null";
		}
			if(Friends.charAt(0) == '/')
				Friends = Friends.substring(1);
			String[] FriendStatus = Friends.split("/");
			
			ArrayList<HashMap<String,String>> groupData = new ArrayList<HashMap<String,String>>();
			for(String o:FriendStatus)
			{
				if(o.equals(""))
					o = "Add Friend+";
				HashMap<String,String> temp = new HashMap<String,String>();
				temp.put("Text",o);
				groupData.add(temp);
				
				
			}

			setListAdapter(new FriendListAdapter(this, R.layout.friend_item, FriendStatus, groupData));

			ListView friends = getListView();
			//friends.setTextFilterEnabled(true);

			friends.setOnItemClickListener(new OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub

				}


			});
		
	}
}