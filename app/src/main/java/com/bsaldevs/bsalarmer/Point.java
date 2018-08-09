package com.bsaldevs.bsalarmer;

import android.content.Intent;

public class Point {
    private double x;
    private double y;
    private boolean arrived = false;
    public double getX()
    {
        return x;

    }

    public void wakeMeUp()
    {
        if (arrived==true)
        {

            //code for waking up
        }
    }
    public double getY()
    {

        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
