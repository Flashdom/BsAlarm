package com.bsaldevs.bsalarmer;

import java.util.List;

public class MyLocation {
    private double x;
    private double y;
    private double r = 100;
    List<Point> myPoints;
    int points_amount=0;

    public double getX() {
        return x;
    }
    public void notifyEveryone()
    {
        for (Point e:myPoints
             ) {
            if ((x+r)>e.getX() && (y+r)>e.getY())
            {
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
