package com.bsaldevs.bsalarmer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

public class MyLocation implements Serializable{

    private double latitude;
    private double longitude;
    private double radius = 0.001;
    private List<Point> points;

    public MyLocation(double lat, double lng) {
        latitude = lat;
        longitude = lng;
        points = new ArrayList<>();
        Log.d("CDA", "myLocation obj created by (double, double) constructor");
    }

    public MyLocation() {
        latitude = 0;
        longitude = 0;
        points = new ArrayList<>();
        Log.d("CDA", "myLocation obj created by () constructor");
    }

    public MyLocation(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        points = new ArrayList<>();
        Log.d("CDA", "myLocation obj created by (LatLng) constructor");
    }

    public void setLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("CDA", "setLocation in MyLocation class");
        notifyEveryone();
    }

    public void notifyEveryone() {

        Log.d(Constants.TAG, "notifyEveryone: points size is " + points.size());

        for (Point p : points) {
            if (isOnTargetPlace(p))
                p.setArrived(true);
        }
    }

    public boolean isOnTargetPlace(Point p) {

        Log.d(Constants.TAG, "isOnTargetPlace: myLocation lat " + latitude);
        Log.d(Constants.TAG, "isOnTargetPlace: myLocation lng " + longitude);
        Log.d(Constants.TAG, "isOnTargetPlace: point lat " + p.getLat());
        Log.d(Constants.TAG, "isOnTargetPlace: point lng " + p.getLng());

        Log.d(Constants.TAG, "isOnTargetPlace: Math.abs(latitude + p.getLat()) = " + Math.abs(latitude + p.getLat()));
        Log.d(Constants.TAG, "isOnTargetPlace: Math.abs(longitude + p.getLng()) = " + Math.abs(longitude + p.getLng()));

        if (Math.abs(latitude + p.getLat()) < radius && Math.abs(longitude + p.getLng()) < radius)
            return true;
        else
            return false;
    }

    public void addPoint(Point point) {
        points.add(point);

        Log.d(Constants.TAG, "addPoint: points size is " + points.size());

        Log.d("CDA", "addPoint from MyLocation class");
    }

    public void removePoint(Point point) {
        for (Point p : points) {
            if (p.getId().equals(point.getId())) {
                points.remove(p);
                Log.d(Constants.TAG, "removePoint: id = " + p.getId());
                break;
            }
        }
    }

    public double getR() {
        return radius;
    }

    public List<Point> getPoints() {
        return points;
    }

    public boolean isAnyoneArrived() {

        for (Point p : points) {
            if (p.isArrived())
                return true;
        }

        return false;
    }
}