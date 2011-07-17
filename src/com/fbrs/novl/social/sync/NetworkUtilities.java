package com.fbrs.novl.social.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;

import com.fbrs.novl.social.sync.auth.AuthenticatorActivity;

public class NetworkUtilities {

	public static List<User> fetchFriendUpdates(Account a,String authtoken, Date d)
	{
		
		List<User> u= new ArrayList<User>();
		u.add(new User("Nick", "Offline", "NA", "Offline"));
		return u;
		
	}

	public static boolean authenticate(String username, String password,
			Handler handler, final Context context) {
		// TODO Auto-generated method stub
		 sendResult(true, handler, context);
         return true;
	}

	public static Thread attemptAuth(final String mUsername, final String mPassword,
			final Handler mHandler, final AuthenticatorActivity authenticatorActivity) {
		final Runnable runnable = new Runnable() {
            public void run() {
                authenticate(mUsername, mPassword, mHandler, authenticatorActivity );
            }
        };
        // run on background thread.
        return NetworkUtilities.performOnBackgroundThread(runnable);
	}
	
	public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                }
            }
        };
        t.start();
        return t;
    }
	
	private static void sendResult(final Boolean result, final Handler handler,
	        final Context context) {
	        if (handler == null || context == null) {
	            return;
	        }
	        handler.post(new Runnable() {
	            public void run() {
	                ((AuthenticatorActivity) context).onAuthenticationResult(result);
	            }
	        });
	    }
}
