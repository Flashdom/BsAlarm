package com.bsaldevs.bsalarmer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MyLocation {

    private LatLng position;
    private double r = 100;
    private List<Marker> markers;
    private Context context;

    public MyLocation(double lat, double lng) {
        position = new LatLng(lat, lng);
        markers = new ArrayList<>();
    }

    public MyLocation() {
        position = new LatLng(0, 0);
        markers = new ArrayList<>();
    }

    public MyLocation(LatLng latLng) {
        position = latLng;
        markers = new ArrayList<>();
    }

    public void setLocation(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        position = new LatLng(lat, lng);
        Log.d("CDA", "setLocation in MyLocation class");
    }

    public void notifyEveryone() {
        for (Marker mark : markers) {
            if (isOnTargetPlace(mark))
                wakeMeUp();
        }
    }

    public boolean isOnTargetPlace(Marker mark) {
        if ((position.latitude + r) > mark.getPosition().latitude && (position.longitude + r) > mark.getPosition().longitude)
            return true;
        else
            return false;
    }

    public void addMarker(Marker mark) {
        markers.add(mark);
    }

    public void removeMarker(Marker mark) {
        markers.remove(mark);
        mark.remove();
    }

    public void wakeMeUp()
    {
        Toast.makeText(context, "Wake Up", Toast.LENGTH_SHORT).show();
        //code for waking up
    }

    public LatLng getPosition() {
        return position;
    }

    public void setContext(MapsActivity context) {
        this.context = context;
    }
}
