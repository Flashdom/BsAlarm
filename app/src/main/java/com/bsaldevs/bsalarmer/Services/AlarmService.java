package com.bsaldevs.bsalarmer.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.MyLocationManager;
import com.bsaldevs.bsalarmer.Point;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by azatiSea on 10.08.2018.
 */

public class AlarmService extends Service {

    private static final String TAG = Constants.TAG;
    private Uri song;
    private MediaPlayer mediaPlayer;
    private boolean isAlarming = false;

    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "AlarmService: onCreate");

        mediaPlayer = new MediaPlayer();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int task = intent.getIntExtra("task", -1);
                Log.d(TAG, "AlarmService: onReceive: task code " + task);
                if (task == BroadcastActions.ALARM) {
                    alarm();
                } else if (task == BroadcastActions.SET_SONG) {
                    song = Uri.parse(intent.getStringExtra("song"));
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(Constants.ALARM_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand");

        song = Uri.parse(intent.getStringExtra("song"));

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
        Log.d(TAG, "onDestroy");
    }

    private void alarm() {
        Log.d(TAG, "start alarming");
        if (isAlarming)
            return;
        isAlarming = true;
        playSong();
    }

    private void stopAlarming() {
        Log.d(Constants.TAG, "stopAlarming");
        stopSong();
    }

    private void playSong() {

        if (song == null) {
            Log.d(TAG, "playSong: song == null");
            return;
        }

        /*if (isAlarming) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isAlarming = false;
        }*/

        try {
            mediaPlayer.create(this, song);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), song);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {                                                                                                                                                                         //dsd
            e.printStackTrace();
        }
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isAlarming = false;
    }

    class MyRun implements Runnable {

        int startId;
        int param;

        public MyRun(int startId, int param) {
            this.startId = startId;
            this.param = param;
            Log.d(TAG, "MyRun#" + startId + " create");
        }

        @Override
        public void run() {
            Log.d(TAG, "myRun started with param = " + param);
            stopSelfResult(startId);
        }
    }

}