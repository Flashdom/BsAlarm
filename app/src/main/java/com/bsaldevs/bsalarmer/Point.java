package com.bsaldevs.bsalarmer;

import android.net.Uri;

import com.google.android.gms.maps.model.Marker;

public class Point {

    private boolean arrived = false;
    private boolean chosen = false;

    private Marker marker;

    public Point(Marker mark) {
        marker = mark;
    }

    public boolean getChosen()
    {
        return chosen;
    }

    public void setChosen()
    {
        chosen = true;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived)
    {
        this.arrived = arrived;
    }

    public Marker getMarker() {
        return marker;
    }
}
