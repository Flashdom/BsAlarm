package com.bsaldevs.bsalarmer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class MyLocation implements Serializable{

    private double latitude;
    private double longitude;
    private double r = 100;
    private List<Point> points;

    public MyLocation(double lat, double lng) {
        latitude = lat;
        longitude = lng;
        points = new ArrayList<>();
    }

    public MyLocation() {
        latitude = 0;
        longitude = 0;
        points = new ArrayList<>();
    }

    public MyLocation(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        points = new ArrayList<>();
    }

    public void setLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("CDA", "setLocation in MyLocation class");
    }

    public void notifyEveryone() {
        for (Point p : points) {
            if (isOnTargetPlace(p))
                wakeMeUp();
        }
    }

    public boolean isOnTargetPlace(Point p) {

        Marker mark = p.getMarker();

        if ((latitude + r) > mark.getPosition().latitude && (longitude + r) > mark.getPosition().longitude)
            return true;
        else
            return false;
    }


    public void addPoint(Marker marker) {
        points.add(new Point(marker));
    }

    public void removePoint(Marker marker) {
        for (Point p : points) {
            if (p.getMarker().equals(marker)) {
                points.remove(p);
                p.getMarker().remove();
                break;
            }
        }
    }

    public void wakeMeUp() {
        Log.d("CDA", "wakeMeUp triggered");
    }

    public double getR() {
        return r;
    }

}
