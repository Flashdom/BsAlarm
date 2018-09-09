package com.bsaldevs.bsalarmer;

import android.util.Log;

/**
 * Created by azatiSea on 20.08.2018.
 */

public class MyLocation {

    private static final String TAG = Constants.TAG;

    private double latitude = 0;
    private double longitude = 0;
    private double actionRadius = 5;

    public MyLocation() {
        latitude = 0;
        longitude = 0;
        actionRadius = 5;
    }

    public MyLocation(double latitude, double longitude, double actionRadius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.actionRadius = actionRadius;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        Log.d(TAG, "setLocation in MyLocation class");
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getActionRadius() {
        return actionRadius;
    }
}