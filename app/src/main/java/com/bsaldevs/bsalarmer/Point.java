package com.bsaldevs.bsalarmer;

import java.io.Serializable;

public class Point implements Serializable {

    private boolean arrived = false;
    private boolean chosen = false;

    private double radius = 0;

    private double lat = 0;
    private double lng = 0;
    private String title = "";

    private String id = "";

    private boolean notifies = false;

    private int notificationId = -1;


    public Point(double lat, double lng, String title) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean getChosen() {
        return chosen;
    }

    public void setChosen() {
        chosen = true;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (arrived != point.arrived) return false;
        if (chosen != point.chosen) return false;
        if (Double.compare(point.radius, radius) != 0) return false;
        if (Double.compare(point.lat, lat) != 0) return false;
        if (Double.compare(point.lng, lng) != 0) return false;
        return title.equals(point.title);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (arrived ? 1 : 0);
        result = 31 * result + (chosen ? 1 : 0);
        temp = Double.doubleToLongBits(radius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + title.hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public boolean isNotifies() {
        return notifies;
    }

    public void setNotifies(boolean notifies) {
        this.notifies = notifies;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void setPosition(double latitude, double longitude) {
        lat = latitude;
        lng = longitude;
    }
}
