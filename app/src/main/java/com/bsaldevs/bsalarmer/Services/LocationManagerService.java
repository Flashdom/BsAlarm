package com.bsaldevs.bsalarmer.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Managers.MyLocationManager;
import com.bsaldevs.bsalarmer.Point;

import java.util.ArrayList;

/**
 * Created by azatiSea on 22.08.2018.
 */

public class LocationManagerService extends Service {

    private static final String TAG = Constants.TAG;
    private MyLocationManager myLocationManager;
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "LocationManagerService: onCreate");

        myLocationManager = new MyLocationManager(this);

        IntentFilter intentFilter = new IntentFilter(Constants.LOCATION_MANAGER_ACTION);

        receiver = new MyReceiver();

        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (START_FLAG_REDELIVERY == flags) {
            Log.d(TAG, "flags = START_FLAG_REDELIVERY");
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int task = intent.getIntExtra("task", 0);
            Log.d(TAG, "LocationManagerService: onReceive: task code " + task);
            if (task == BroadcastActions.ADD_TARGET) {
                Point point = (Point) intent.getSerializableExtra("point");
                String id = intent.getStringExtra("id");
                myLocationManager.addTarget(point, id);
            } else if (task == BroadcastActions.REMOVE_TARGET) {
                String id = intent.getStringExtra("id");
                Intent notification = new Intent(Constants.NOTIFICATION_ACTION)
                        .putExtra("task", BroadcastActions.CLOSE_NOTIFICATION)
                        .putExtra("point", myLocationManager.getTargetById(id));
                sendBroadcast(notification);
                myLocationManager.removeTarget(id);
            } else if (task == BroadcastActions.CHANGE_TARGET) {
                Point point = (Point) intent.getSerializableExtra("point");
                String extras = intent.getStringExtra("packedPointExtras");

                myLocationManager.changeTarget(point, extras);

            } else if (task == BroadcastActions.SET_USER_LOCATION) {
                double lat = intent.getDoubleExtra("lat", 0);
                double lng = intent.getDoubleExtra("lng", 0);
                myLocationManager.setLocation(lat, lng);
            } else if (task == BroadcastActions.GET_TARGETS) {
                ArrayList<Point> points = (ArrayList<Point>) myLocationManager.getTargets();
                String sender = intent.getStringExtra("sender");

                Intent location;

                if (sender.equals("mapsActivity")) {
                    location = new Intent(Constants.MAPS_ACTION)
                            .putExtra("task", BroadcastActions.GET_TARGETS)
                            .putExtra("points", points);
                } else if (sender.equals("pointListActivity")) {
                    location = new Intent(Constants.POINT_LIST_ACTION)
                            .putExtra("task", BroadcastActions.GET_TARGETS)
                            .putExtra("points", points);
                } else {
                    location = new Intent();
                }

                sendBroadcast(location);
            }
        }
    }
}
