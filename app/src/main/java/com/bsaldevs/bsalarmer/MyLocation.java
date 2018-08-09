package com.bsaldevs.bsalarmer;

<<<<<<< HEAD
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

=======
>>>>>>> 515533694084c117c04b8d2e0cfdfb35c8abdb31
public class MyLocation {
    private double x;
    private double y;
    private double r = 100;
<<<<<<< HEAD

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

=======
>>>>>>> 515533694084c117c04b8d2e0cfdfb35c8abdb31
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

<<<<<<< HEAD
=======


>>>>>>> 515533694084c117c04b8d2e0cfdfb35c8abdb31
}
