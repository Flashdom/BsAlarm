package com.bsaldevs.bsalarmer;

import android.content.Intent;

public class Point {
    private double x;
    private double y;
    private boolean arrived = false;
    boolean chosen=false;
    MainMenuActivity mActivity = new MainMenuActivity();
    public double getX()
    {
        return x;

    }
    public boolean getChosen()
    {
        return chosen;
    }
    public void setChosen()
    {
        chosen = true;
    }


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setArrived()
    {
        arrived = true;
        wakeMeUp();
    }

    public void wakeMeUp()
    {
        if (arrived==true)
        {
            mActivity.mplayergo(mActivity.getMyFile());

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
