package com.bsaldevs.bsalarmer.Managers;

import android.content.Context;
import android.util.Log;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azatiSea on 20.08.2018.
 */

public class PointManager {

    private static final String TAG = Constants.TAG;
    private List<Point> points;
    private CacheManager cacheManager;

    public PointManager(Context context) {
        Log.d(TAG, "PointManager: constructor");
        points = new ArrayList<>();
        cacheManager = new CacheManager(context);
        load();
    }

    public void createAll(List<Point> list, List<String> identificators) {
        Log.d(TAG, "PointManager: createAll");
        for (int i = 0; i < list.size(); i++) {
            createPoint(list.get(i), identificators.get(i));
        }
    }

    public void createPoint(Point point, String id) {
        Log.d(TAG, "PointManager: add: point id = " + id);
        point.setId(id);
        points.add(point);
        save();
    }

    public void remove(String id) {
        Log.d(TAG, "PointManager: remove");
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getId().equals(id)) {
                points.remove(i);
                save();
                break;
            }
        }
    }

    public List<Point> getPoints() {
        return points;
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public Point getPoint(String id) {
        Point point = null;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getId().equals(id)) {
                point = points.get(i);
                break;
            }
        }
        return point;
    }

    private void load() {
        Log.d(TAG, "PointManager: load");
        cacheManager.load();
        List<Point> mPoints = cacheManager.parsePoints();
        List<String> identificators = new ArrayList<>();
        for (int i = 0; i < mPoints.size(); i++) {
            identificators.add("m" + i);
        }
        createAll(mPoints, identificators);
        seeAllPointIds();
    }

    private void save() {
        Log.d(TAG, "PointManager: save");
        cacheManager.save(points);
        seeAllPointIds();
    }

    public void setPointPositionByBind(String bind, double lat, double lng) {
        Point point = getPoint(bind);
        point.setLatitude(lat);
        point.setLongitude(lng);
        save();
    }

    private void seeAllPointIds() {
        for (int i = 0; i < points.size(); i++) {
            Log.d(TAG, "point name = " + points.get(i).getName() + ", id = " + points.get(i).getId());
        }
    }

    public void changePoint(Point point) {

        String extras = point.getExtra();
        Point target = getPoint(point.getId());

        if (extras.contains("active"))
            target.setActive(point.isActive());

        if (extras.contains("achieved"))
            target.setAchieved(point.isAchieved());

        if (extras.contains("name"))
            target.setName(point.getName());

        if (extras.contains("radius"))
            target.setRadius(point.getRadius());

        if (extras.contains("longitude"))
            target.setLongitude(point.getLongitude());

        if (extras.contains("latitude"))
            target.setLatitude(point.getLatitude());

        save();
    }
}