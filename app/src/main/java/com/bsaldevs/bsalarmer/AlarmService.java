package com.bsaldevs.bsalarmer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by azatiSea on 10.08.2018.
 */

public class AlarmService extends Service {

    private static final String TAG = Constants.TAG;
    private static int NOTIFY_ID = 0;
    private Uri song;
    private MediaPlayer mediaPlayer;
    private LocationManager locationManager;
    private MyLocation myLocation;
    private NotificationManager notificationManager;
    private boolean isAlarming = true;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged");
                myLocation.setLocation(location);
                List<Point> points = myLocation.getPoints();

                closeUnusedNotification();

                for (Point point : points) {
                    if (point.isArrived()) {
                        Log.d(TAG, "onLocationChanged: anyone is arrived");
                        alarm(point);
                    }
                }

                updateMap();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled");
            }
        });

        Log.d(TAG, "AlarmService: onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        song = Constants.SOUND_URI;
        myLocation = (MyLocation) intent.getSerializableExtra("MY_LOCATION");
        pendingIntent = intent.getParcelableExtra("pendingIntent");

        Log.d(Constants.TAG, "onStartCommand: myLocation points count = " + myLocation.getPoints().size());

        myLocation.notifyEveryone();

        List<Point> points = myLocation.getPoints();

        for (Point point : points) {
            if (point.isArrived()) {
                Log.d(TAG, "onLocationChanged: anyone is arrived");
                alarm(point);
            }
        }

        closeUnusedNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void alarm(Point point) {
        Log.d(TAG, "start alarming");
        if (!point.isNotifies()) {
            createNotification(point);
            if (isAlarming) {
                isAlarming = false;
                playSong();
            }
        } else {
            Log.d(Constants.TAG, "alarm: point already arrived");
        }
    }

    private void createNotification(Point point) {

        point.setNotifies(true);

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        point.setNotificationId(NOTIFY_ID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_baseline_departure_board_24px)
                .setTicker("WAKE UP!")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("WAKE UP!")
                .setContentText(point.getTitle() + " notif id = " + point.getNotificationId())
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.BLUE)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFY_ID, builder.build());

        updateMap();

        NOTIFY_ID++;
    }

    private void closeUnusedNotification() {
        List<Point> points = myLocation.getPoints();
        for (Point point : points) {
            if (point.isNotifies() && !point.isArrived()) {
                point.setNotifies(false);
                notificationManager.cancel(point.getNotificationId());
                updateMap();
            }
        }
    }

    public void playSong() {

        if (song == null) {
            Log.d(TAG, "playSong: song == null");
            return;
        }

        if (isAlarming) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isAlarming = false;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.create(this, song);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), song);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {                                                                                                                                                                         //dsd
            e.printStackTrace();
        }
    }

    public void setSong(Uri song) {
        this.song = song;
    }

    public void updateMap() {
        try {
            Intent intent = new Intent().putExtra("MY_LOCATION", myLocation);
            pendingIntent.send(AlarmService.this, 200, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

}