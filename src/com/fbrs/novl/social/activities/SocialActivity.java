package com.fbrs.novl.social.activities;

import com.fbrs.novl.social.R;
import com.fbrs.novl.social.R.layout;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SocialActivity extends TabActivity {
	
	public static final String Username = "nick";
	public static final String password = "password";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent intent = new Intent().setClass(this, ArtistsActivity.class);
        Intent news_intent = new Intent().setClass(this, NewsActivity.class);
        Intent matchmakeing_intent = new Intent().setClass(this, GamesActivity.class);
        Intent friends_intent = new Intent().setClass(this, GameSocialActivity.class);
        
        TabHost tabs = (TabHost) this.findViewById(android.R.id.tabhost);
        tabs.setup();
        
       
        
        TabSpec tspec1 = tabs.newTabSpec("Games");
        tspec1.setIndicator("Games");
        tspec1.setContent(matchmakeing_intent);
        tabs.addTab(tspec1); 
        TabSpec tspec2 = tabs.newTabSpec("News");
        tspec2.setIndicator("News");
        tspec2.setContent(news_intent);
        tabs.addTab(tspec2);
        TabSpec tspec3 = tabs.newTabSpec("Social");
        tspec3.setIndicator("Social");
        tspec3.setContent(friends_intent);
        tabs.addTab(tspec3);
        for(int i = 0; i < 3; i++) {
        	tabs.getTabWidget().getChildAt(i).getLayoutParams().height = 60;
        }
        tabs.setCurrentTab(1);
    }
}
/*
    Intent news_intent = new Intent().setClass(this, NewsActivity.class);
        Intent matchmakeing_intent = new Intent().setClass(this, MatchMakeing.class);
        Intent friends_intent = new Intent().setClass(this, FriendsList.class);
*/