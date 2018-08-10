package com.bsaldevs.bsalarmer;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MyLocation {
    private double x;
    private double y;
    private double r = 100;
    private List<Point> myPoints;
    private int points_amount=0;


    public MyLocation(double x, double y) {
        this.x = x;
        this.y = y;
        myPoints = new ArrayList<>();
    }

    public MyLocation() {
        this.x = 0;
        this.y = 0;
        myPoints = new ArrayList<>();
    }

    public MyLocation(LatLng latLng) {
        this.x = latLng.latitude;
        this.y = latLng.longitude;
        myPoints = new ArrayList<>();
    }

    public void setLocation(Location location) {
        this.x = location.getLatitude();
        this.y = location.getLongitude();
        Log.d("CDA", "setLocation in MyLocation class");
    }

    public double getX() {
        return x;
    }

    public void notifyEveryone()
    {
        for (Point e : myPoints) {
            if ((x + r) > e.getX() && (y + r) > e.getY()) {
                e.setArrived();
            }
        }
    }

    public void addPoint(double x, double y)
    {
        myPoints.add(points_amount, new Point(x,y));
        points_amount++;
    }

    public void deletePoint(Point point)
    {
        for (int i=0; i<points_amount; i++) {

            myPoints.remove(myPoints.indexOf(point));
        }

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
