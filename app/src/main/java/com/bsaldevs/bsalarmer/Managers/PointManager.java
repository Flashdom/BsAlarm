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
    private List<String> binds;
    private CacheManager cacheManager;
    private static int id = 0;

    public PointManager(Context context) {
        Log.d(TAG, "PointManager: constructor");
        points = new ArrayList<>();
        binds = new ArrayList<>();
        cacheManager = new CacheManager(context);
        load();
        id = points.size();
    }

    public void createAll(List<Point> list, List<String> binds) {
        Log.d(TAG, "PointManager: createAll");
        for (int i = 0; i < list.size(); i++) {
            createPoint(list.get(i), binds.get(i));
        }
    }

    public void createPoint(Point point, String bind) {
        Log.d(TAG, "PointManager: add: point bind = " + bind);

        point.setId(id++);

        points.add(point);
        binds.add(bind);
        save();
    }

    public void remove(Point point) {
        Log.d(TAG, "PointManager: remove");
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).equals(point)) {
                binds.remove(i);
                points.remove(i);
                save();
                break;
            }
        }
    }

    public void remove(String bind) {
        Log.d(TAG, "PointManager: remove");
        for (int i = 0; i < binds.size(); i++) {
            if (binds.get(i).equals(bind)) {
                binds.remove(i);
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

    public Point getPointByBind(String bind) {
        Point point = null;
        for (int i = 0; i < binds.size(); i++) {
            if (binds.get(i).equals(bind)) {
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
        List<String> fakeBinds = new ArrayList<>();
        for (int i = 0; i < mPoints.size(); i++) {
            fakeBinds.add("m" + i);
        }
        createAll(mPoints, fakeBinds);
        seeAllPointBinds();
    }

    private void save() {
        Log.d(TAG, "PointManager: save");
        cacheManager.save(points);
        seeAllPointBinds();
    }

    public void setPointPositionByBind(String bind, double lat, double lng) {
        Point point = getPointByBind(bind);
        point.setLatitude(lat);
        point.setLongitude(lng);
        save();
    }

    private void seeAllPointBinds() {
        for (int i = 0; i < points.size(); i++) {
            Log.d(TAG, "point name = " + points.get(i).getName() + ", bind = " + binds.get(i));
        }
    }

    public void changePointByBind(String bind, Point pseudoPoint) {
        double lat = pseudoPoint.getLatitude();
        double lng = pseudoPoint.getLongitude();
        double radius = pseudoPoint.getRadius();
        String name = pseudoPoint.getName();

        Point point = getPointByBind(bind);

        if (lat > 0)
            point.setLatitude(lat);

        if (lng > 0)
            point.setLongitude(lng);

        if (radius > 0)
            point.setRadius(radius);

        if (!name.equals(""))
            point.setName(name);
    }
}