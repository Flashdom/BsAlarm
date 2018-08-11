package com.bsaldevs.bsalarmer;

public class Point {
    private double x;
    private double y;
    private boolean arrived = false;

    private boolean chosen = false;
    private MainMenuActivity mActivity = new MainMenuActivity();

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
        if (this.arrived)
            wakeMeUp();
    }

    public void wakeMeUp()
    {
        mActivity.mplayergo(mActivity.getMyFile());
        //code for waking up
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
