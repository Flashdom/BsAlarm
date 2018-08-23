package com.bsaldevs.bsalarmer.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bsaldevs.bsalarmer.Constants;

/**
 * Created by azatiSea on 20.08.2018.
 */

public class MainService extends Service {

    private BroadcastReceiver receiver;
    private static final String UPDATE = "update";

    private static final int UPDATE_CODE = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d(Constants.TAG, "MainService: onCreate");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(Constants.TAG, "MainService: onStartCommand");

        String song = intent.getStringExtra("song");

        Intent alarm = new Intent(this, AlarmService.class)
                .putExtra("song", song);
        startService(alarm);

        Intent notification = new Intent(this, NotificationService.class)
                .putExtra("location manager", 7);
        startService(notification);

        Intent location = new Intent(this, LocationService.class)
                .putExtra("location manager", 7);
        startService(location);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}