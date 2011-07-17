package com.fbrs.novl.social.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NovlSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static NovlSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new NovlSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}