package com.bsaldevs.bsalarmer.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.MyLocationManager;
import com.bsaldevs.bsalarmer.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by azatiSea on 22.08.2018.
 */

public class LocationService extends Service {

    private static final String TAG = Constants.TAG;
    private MyLocationManager myLocationManager;
    private BroadcastReceiver receiver;

    private static final int ADD_TARGET_CODE = 100;
    private static final int REMOVE_TARGET_CODE = 101;
    private static final int CHANGE_LOCATION_CODE = 102;
    private static final int SET_LOCATION_CODE = 103;

    private static final int TASK_GET_TARGETS = 200;
    private static final int ON_MAP_READY_CODE = 201;

    private static final int TASK_CLOSE_NOTIFICATION_CODE = 300;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "LocationService: onCreate");

        myLocationManager = new MyLocationManager(this);

        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);

        receiver = new MyReceiver();

        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int task = intent.getIntExtra("task", 0);
            Log.d(TAG, "LocationService: onReceive: task code " + task);
            if (task == ADD_TARGET_CODE) {
                Point point = (Point) intent.getSerializableExtra("point");
                String bind = intent.getStringExtra("bind");
                myLocationManager.addTarget(point, bind);
                Point target = myLocationManager.getTargetByBind(bind);
            } else if (task == REMOVE_TARGET_CODE) {
                String bind = intent.getStringExtra("bind");
                Intent notification = new Intent(Constants.NOTIFICATION_ACTION)
                        .putExtra("task", TASK_CLOSE_NOTIFICATION_CODE)
                        .putExtra("point", myLocationManager.getTargetByBind(bind));
                sendBroadcast(notification);
                myLocationManager.removeTarget(bind);
            } else if (task == CHANGE_LOCATION_CODE) {
                String bind = intent.getStringExtra("bind");
                double lat = intent.getDoubleExtra("lat", 0);
                double lng = intent.getDoubleExtra("lng", 0);
                myLocationManager.setTargetPosition(bind, lat, lng);
            } else if (task == SET_LOCATION_CODE) {
                double lat = intent.getDoubleExtra("lat", 0);
                double lng = intent.getDoubleExtra("lng", 0);
                myLocationManager.setLocation(lat, lng);
            } else if (task == ON_MAP_READY_CODE) {
                ArrayList<Point> points = (ArrayList<Point>) myLocationManager.getTargets();
                Intent location = new Intent(Constants.MAPS_ACTION)
                        .putExtra("task", TASK_GET_TARGETS)
                        .putExtra("points", points);
                sendBroadcast(location);
            }
        }
    }
}
