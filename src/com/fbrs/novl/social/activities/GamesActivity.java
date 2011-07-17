package com.fbrs.novl.social.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fbrs.novl.social.R;

public class GamesActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameslist);
        
        ImageView battleships = (ImageView)this.findViewById(R.id.battleshipbanner);
        
        battleships.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
			}
		});
    }
}