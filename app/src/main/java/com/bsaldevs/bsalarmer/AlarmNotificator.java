package com.bsaldevs.bsalarmer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bsaldevs.bsalarmer.Activities.MapsActivity;

public class AlarmNotificator {

    private String TAG = Constants.TAG;

    private NotificationManager notificationManager;
    private int NOTIFY_ID = 0;
    private AssociatedNotificationList notifications;
    private Context context;
    private MyApplication application;

    public AlarmNotificator(Context context) {
        this.context = context;
        this.application = (MyApplication) context.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notifications = new AssociatedNotificationList();
    }

    public void createNotification(String id) {

        Point point = application.getTarget(id);

        Intent notificationIntent = new Intent(context, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String CHANNEL_ID = "my_channel_01";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String name = "q";
        long[] vibrate = new long[] { 200, 200 };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
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
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_baseline_close_24px, "Close", contentIntent);

        Bundle dest = new Bundle();
        dest.putString("pointId", point.getId());
        builder.setExtras(dest);

        Notification notification = builder.build();
        notifications.add(notification, NOTIFY_ID);
        Log.d(TAG, "createNotification: notification id = " + NOTIFY_ID);

        notificationManager.notify(NOTIFY_ID, notification);

        NOTIFY_ID++;
    }

    public void closeNotification(String id) {

        Point point = application.getTarget(id);

        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.getNotificationByIndex(i);
            String pointId = notification.extras.getString("pointId", "");
            Log.d(TAG, "closeNotification: compare " + pointId + " and " + point.getId());
            if (pointId.equals(point.getId())) {
                int notifId = notifications.getId(notification);
                Log.d(TAG, "closeNotification: notification id = " + notifId);
                notificationManager.cancel(notifId);
            }
        }
    }

    public boolean isEmpty() {
        if (notifications.size() > 0)
            return true;
        return false;
    }

    public void closeAllNotifications() {
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.getNotificationByIndex(i);
            int notifId = notifications.getId(notification);
            Log.d(TAG, "closeNotification: notification id = " + notifId);
            notificationManager.cancel(notifId);
        }
    }
}
