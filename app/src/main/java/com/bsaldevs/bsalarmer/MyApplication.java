package com.bsaldevs.bsalarmer;

import android.app.Application;
import android.content.Context;

import com.bsaldevs.bsalarmer.Managers.MyLocationManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MyApplication extends Application {

    private MyLocationManager myLocationManager;

    public MyApplication() {
        super();
    }

    public void initMyLocationManager(Context context) {
        myLocationManager = new MyLocationManager(context);
    }

    public MyLocationManager getMyLocationManager() {
        return myLocationManager;
    }

    public void setUserLocation(LatLng position) {
        myLocationManager.setLocation(position.latitude, position.longitude);
    }

    public void addTarget(Point point, String id) {
        myLocationManager.addTarget(point, id);
    }

    public void removeTarget(String id) {
        myLocationManager.removeTarget(id);
    }

    public Point getTarget(String id) {
        return myLocationManager.getTargetById(id);
    }

    public void changeTarget(Point point) {
        myLocationManager.changeTarget(point);
    }

    public List<Point> getTargetsList() {
        return myLocationManager.getTargets();
    }
}
