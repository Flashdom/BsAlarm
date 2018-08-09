package com.bsaldevs.bsalarmer;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MyLocation {
    private double x;
    private double y;
    private double r = 100;

    public MyLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public MyLocation() {
        this.x = 0;
        this.y = 0;
    }

    public MyLocation(LatLng latLng) {
        this.x = latLng.latitude;
        this.y = latLng.longitude;
    }

    public void setLocation(Location location) {
        this.x = location.getLatitude();
        this.y = location.getLongitude();
        Log.d("CDA", "setLocation in MyLocation class");
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

}