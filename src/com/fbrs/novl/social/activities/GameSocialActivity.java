package com.fbrs.novl.social.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.fbrs.novl.social.IServerCallback;
import com.fbrs.novl.social.R;
import com.fbrs.novl.social.ServerClient;

public class GameSocialActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socialview);
        removeSpinner();
        RelativeLayout friends = (RelativeLayout)this.findViewById(R.id.FriendsLayout);
        final Intent friends_intent = new Intent().setClass(this, FriendsList.class);
       
        friends.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				addSpinner();
				ServerClient.StartNewThreadedRequest("/Friends/get/" + SocialActivity.Username + "/" + SocialActivity.password + "/", new IServerCallback() {

					@Override
					public void run(String s) {
						// TODO Auto-generated method stub
						friends_intent.putExtra("friends", s);
						//removeSpinner();
						startIntent(friends_intent);
					}
					
				});
				
				
				
			}
		});
        
        RelativeLayout Groups = (RelativeLayout)this.findViewById(R.id.GroupsLayout);
        final Intent groups_intent = new Intent().setClass(this, FriendsList.class);
        
        Groups.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//addSpinner();
				//startActivity(groups_intent); 
				
			}
		});
        
        RelativeLayout Teams = (RelativeLayout)this.findViewById(R.id.TeamsLayout);
        final Intent teams_intent = new Intent().setClass(this, FriendsList.class);
        
        Teams.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//addSpinner();
				
				//startActivity(teams_intent); 
				
			}
		});
    }
    
    @Override
    public void onBackPressed()
    {
    	super.onBackPressed();
    	removeSpinner();
    }
    
    @Override
	public void onStart()
	{
		super.onStart();
		
		removeSpinner();
		
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		removeSpinner();
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		removeSpinner();
	}
    ProgressDialog dialog;
    public void addSpinner()
    {
    	dialog = ProgressDialog.show(this, "", 
                "Loading. Please wait...", true);
    	
    }
    
    public void removeSpinner()
    {
    	if(dialog != null && dialog.isShowing())
    		dialog.hide();
    }
    
    public void startIntent(Intent i)
    {
    	startActivity(i); 
    }
}