package com.bsaldevs.bsalarmer;

import java.io.Serializable;

/**
 * Created by azatiSea on 23.08.2018.
 */

public class PseudoPoint implements Serializable {
    private double latitude = 0;
    private double longitude = 0;
    private double radius = 0;
    private String name = "";

    public PseudoPoint(double latitude, double longitude, double radius, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
