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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (achieved != point.achieved) return false;
        if (active != point.active) return false;
        if (Double.compare(point.radius, radius) != 0) return false;
        if (Double.compare(point.latitude, latitude) != 0) return false;
        if (Double.compare(point.longitude, longitude) != 0) return false;
        if (!name.equals(point.name)) return false;
        return id.equals(point.id);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (achieved ? 1 : 0);
        result = 31 * result + (active ? 1 : 0);
        temp = Double.doubleToLongBits(radius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    public static class Builder {
        private boolean achieved = false;
        private boolean active = true;
        private double radius = 0;
        private double latitude = 0;
        private double longitude = 0;
        private String name = "";
        private String id = "";

        public Builder() {

        }

        public Builder setAchieved(boolean value) {
            achieved = value;
            return this;
        }

        public Builder setActive(boolean value) {
            active = value;
            return this;
        }

        public Builder setRadius(double value) {
            radius = value;
            return this;
        }

        public Builder setLatitude(double value) {
            latitude = value;
            return this;
        }

        public Builder setLongitude(double value) {
            longitude = value;
            return this;
        }

        public Builder setName(String value) {
            name = value;
            return this;
        }

        public Builder setId(String value) {
            id = value;
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
    }
}