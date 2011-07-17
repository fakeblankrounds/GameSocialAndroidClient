package com.fbrs.novl.social.activities;

import com.fbrs.novl.social.R;
import com.fbrs.novl.social.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MatchMakeing extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        TextView textview = new TextView(this);
        textview.setText("This is the matchmakeing tab");
        setContentView(textview);
    }
}
