package com.bsaldevs.bsalarmer;

import android.net.Uri;

public class Point {
    private double x;
    private double y;

    public boolean isArrived() {
        return arrived;
    }

    private boolean arrived = false;
    private boolean chosen = false;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean getChosen()
    {
        return chosen;
    }
    public void setChosen()
    {
        chosen = true;
    }

    public void setArrived(boolean arrived)
    {
        this.arrived = arrived;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
