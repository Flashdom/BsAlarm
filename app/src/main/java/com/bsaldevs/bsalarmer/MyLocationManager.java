package com.bsaldevs.bsalarmer;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bsaldevs.bsalarmer.Managers.PointManager;

import java.io.Serializable;
import java.util.List;

/**
 * Created by azatiSea on 21.08.2018.
 */

public class MyLocationManager implements Serializable {

    private static final String TAG = Constants.TAG;
    private static final int TASK_CLOSE_NOTIFICATION_CODE = 300;
    private static final int TASK_CREATE_NOTIFICATION_CODE = 301;

    private Context context;
    private MyLocation myLocation;
    private PointManager pointManager;
    private LocationManager locationManager;

    public MyLocationManager(Context context) {
        myLocation = new MyLocation();
        pointManager = new PointManager(context);
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged");
                setLocation(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled");
            }
        });
    }

    public Point getTargetByBind(String bind) {
        return pointManager.getPointByBind(bind);
    }

    public void removeTarget(Point point) {
        pointManager.remove(point);
    }

    public void removeTarget(String bind) {
        pointManager.remove(bind);
    }

    public void addTarget(PseudoPoint pPoint, String bind) {
        pointManager.createPoint(pPoint, bind);
        checkIsTargetReached(pointManager.getPointByBind(bind));
    }

    public void setTargetPosition(String bind, double lat, double lng) {
        pointManager.setPointPositionByBind(bind, lat, lng);
        checkIsTargetReached(getTargetByBind(bind));
    }

    public List<Point> getTargets() {
        return pointManager.getPoints();
    }

    public void setLocation(double latitude, double longitude) {
        myLocation.setLocation(latitude, longitude);
        checkIsTargetsReached();
    }

    private void checkIsTargetReached(Point target) {

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();

        boolean reached = false;

        Log.d(Constants.TAG, "MyLocationManager: checkIsTargetReached: Math.abs(latitude - target.getLatitude()) " + Math.abs(latitude - target.getLatitude()) + " ? " + myLocation.getActionRadius());
        Log.d(Constants.TAG, "MyLocationManager: checkIsTargetReached: Math.abs(longitude - target.getLongitude()) " + Math.abs(longitude - target.getLongitude()) + " ? " + myLocation.getActionRadius());

        if (Math.abs(latitude - target.getLatitude()) < myLocation.getActionRadius() && Math.abs(longitude - target.getLongitude()) < myLocation.getActionRadius()) {
            Log.d(Constants.TAG, "MyLocationManager: checkIsTargetReached: point is achieved");
            reached = true;
        } else {
            Log.d(Constants.TAG, "MyLocationManager: checkIsTargetReached: point is un achieved");
        }

        if (reached != target.isAchieved()) {
            target.setAchieved(reached);
            sendChangedStateOfPoint(target, reached);
        }
    }

    private void checkIsTargetsReached() { // TODO(Rename this method)
        List<Point> points = pointManager.getPoints();
        for (Point target : points) {
            checkIsTargetReached(target);
        }
    }

    private void sendChangedStateOfPoint(Point point, boolean reached) {
        Log.d(TAG, "sendChangedStateOfPoint");
        Intent alarm = new Intent(Constants.NOTIFICATION_ACTION)
                .putExtra("point", point);
        if (reached) {
            Log.d(TAG, "sendChangedStateOfPoint: create notification");
            alarm.putExtra("task", TASK_CREATE_NOTIFICATION_CODE);
        }
        else {
            Log.d(TAG, "sendChangedStateOfPoint: close notification");
            alarm.putExtra("task", TASK_CLOSE_NOTIFICATION_CODE);
        }
        context.sendBroadcast(alarm);
    }
}