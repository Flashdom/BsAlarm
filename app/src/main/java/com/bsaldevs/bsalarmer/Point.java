package com.bsaldevs.bsalarmer;
import java.io.Serializable;

public class Point implements Serializable {

    private boolean achieved = false;
    private boolean active = true;
    private double radius = 0;
    private double latitude = 0;
    private double longitude = 0;
    private String name = "";
    private String id = "";
    private String extra = "";

    public Point(double latitude, double longitude, double radius, String name) {
        this.achieved = false;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
        this.active = true;
    }

    public Point(double latitude, double longitude, double radius, String name, boolean active) {
        this.achieved = false;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.name = name;
        this.active = active;
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

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public String getExtra() {
        return extra;
    }

    public static class Builder {
        private boolean achieved = false;
        private boolean active = true;
        private double radius = 0;
        private double latitude = 0;
        private double longitude = 0;
        private String name = "";
        private String id = "";
        private String extra = "";

        public Builder() {

        }

        public Builder setAchieved(boolean value) {
            achieved = value;
            extra += "|achieved|";
            return this;
        }

        public Builder setActive(boolean value) {
            active = value;
            extra += "|active|";
            return this;
        }

        public Builder setRadius(double value) {
            radius = value;
            extra += "|radius|";
            return this;
        }

        public Builder setLatitude(double value) {
            latitude = value;
            extra += "|latitude|";
            return this;
        }

        public Builder setLongitude(double value) {
            longitude = value;
            extra += "|longitude|";
            return this;
        }

        public Builder setName(String value) {
            name = value;
            extra += "|name|";
            return this;
        }

        public Builder setId(String value) {
            id = value;
            extra += "|id|";
            return this;
        }

        public Point build() {
            return new Point(this);
        }
    }

    private Point(Builder builder) {
        achieved = builder.achieved;
        active = builder.active;
        radius = builder.radius;
        latitude = builder.latitude;
        longitude = builder.longitude;
        name = builder.name;
        id = builder.id;
        extra = builder.extra;
    }
}