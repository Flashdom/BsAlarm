package com.bsaldevs.bsalarmer;
import java.io.Serializable;

public class Point implements Serializable {

    private boolean achieved = false;
    private boolean active = true;
    private double radius = 0;
    private double latitude = 0;
    private double longitude = 0;
    private String name = "";
    private int id = 0;

    public Point(double latitude, double longitude, double radius, String name) {
        this.achieved = false;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
        this.active = true;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
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

    public void setPosition(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (achieved != point.achieved) return false;
        if (Double.compare(point.radius, radius) != 0) return false;
        if (Double.compare(point.latitude, latitude) != 0) return false;
        if (Double.compare(point.longitude, longitude) != 0) return false;
        if (id != point.id) return false;
        return name != null ? name.equals(point.name) : point.name == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (achieved ? 1 : 0);
        temp = Double.doubleToLongBits(radius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}