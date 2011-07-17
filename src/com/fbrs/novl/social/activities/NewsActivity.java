package com.fbrs.novl.social.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class NewsActivity extends Activity {
    /** Called when the activity is first created. */
	
	public WebView webview;
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(webview != null)
		webview.reload();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if(webview != null)
		webview.reload();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.test);
        webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://m.fakeblankrounds.com/");
        webview.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				webview.reload();
				return true;
				
			}
			
		});
        webview.setLongClickable(true);
    }
}
