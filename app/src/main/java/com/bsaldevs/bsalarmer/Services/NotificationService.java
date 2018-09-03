package com.bsaldevs.bsalarmer.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bsaldevs.bsalarmer.Activities.MapsActivity;
import com.bsaldevs.bsalarmer.AssociatedNotificationList;
import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Point;
import com.bsaldevs.bsalarmer.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azatiSea on 20.08.2018.
 */

public class NotificationService extends Service {

    private static final String TAG = Constants.TAG;
    private NotificationManager notificationManager;
    private int NOTIFY_ID = 0;

    private AssociatedNotificationList notifications;
    private BroadcastReceiver receiver;

    //private Thread serviceThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "NotificationService: onCreate");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new AssociatedNotificationList();

        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.NOTIFICATION_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread serviceThread = new Thread(new MyRun());
        serviceThread.start();
        if (START_FLAG_REDELIVERY == flags) {
            Log.d(TAG, "flags = START_FLAG_REDELIVERY");
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotification(Point point) {
        Intent notificationIntent = new Intent(this, MapsActivity.class);

        //notificationIntent.putExtra("task", BroadcastActions.STOP_ALARM);

        PendingIntent contentIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String CHANNEL_ID = "my_channel_01";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String name = "q";
        long[] vibrate = new long[] { 200, 200 };

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_baseline_departure_board_24px)
                .setTicker("WAKE UP!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("WAKE UP!")
                .setContentText("You reached the point: " + point.getName())
                .setOngoing(false)
                .setPriority(importance)
                .setColor(Color.BLUE)
                .setChannelId(CHANNEL_ID)
                .setVibrate(vibrate)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_baseline_close_24px, "Close", contentIntent);

        Bundle dest = new Bundle();
        dest.putString("pointId", point.getId());
        builder.setExtras(dest);

        Notification notification = builder.build();
        notifications.add(notification, NOTIFY_ID);
        Log.d(TAG, "createNotification: notification id = " + NOTIFY_ID);

        notificationManager.notify(NOTIFY_ID, notification);
        sendMessageToAlarmService();

        NOTIFY_ID++;
    }

    private void closeNotification(Point point) {
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.getNotificationByIndex(i);
            String pointId = notification.extras.getString("pointId", "");
            Log.d(TAG, "closeNotification: compare " + pointId + " and " + point.getId());
            if (pointId.equals(point.getId())) {
                int id = notifications.getId(notification);
                Log.d(TAG, "closeNotification: notification id = " + id);
                notificationManager.cancel(id);
            }
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int task = intent.getIntExtra("task", 0);
            Log.d(TAG, "NotificationService: onReceive: task code " + task);
            Point point = (Point) intent.getSerializableExtra("point");
            if (task == BroadcastActions.CLOSE_NOTIFICATION) {
                Log.d(TAG, "NotificationService: onReceive: close notification ");
                closeNotification(point);
            } else if (task == BroadcastActions.CREATE_NOTIFICATION) {
                Log.d(TAG, "NotificationService: onReceive: create notification ");
                createNotification(point);
            }
        }
    }

    private void sendMessageToAlarmService() {
        Intent alarm = new Intent(Constants.ALARM_ACTION)
                .putExtra("task", 500);
        sendBroadcast(alarm);
    }

    private class MyRun implements Runnable {

        public MyRun() {
            Log.d(TAG, "MyRun create in NotificationService");
        }

        @Override
        public void run() {

        }
    }
}