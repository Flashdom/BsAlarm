package com.bsaldevs.bsalarmer.Managers;

import android.content.Context;
import android.util.Log;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by azatiSea on 20.08.2018.
 */

public class PointManager implements Serializable{

    private static final String TAG = Constants.TAG;
    private List<Point> points;
    private List<String> binds;
    private CacheManager cacheManager;
    private static int pointTag = 0;

    public PointManager(Context context) {
        Log.d(TAG, "PointManager: constructor");
        points = new ArrayList<>();
        binds = new ArrayList<>();
        cacheManager = new CacheManager(context);
        load();
        pointTag = points.size();
    }

    public void addAll(List<Point> list, List<String> binds) {
        Log.d(TAG, "PointManager: addAll");
        for (int i = 0; i < list.size(); i++) {
            add(list.get(i), binds.get(i));
        }
    }

    public void add(Point point, String bind) {
        Log.d(TAG, "PointManager: add: point bind = " + bind);
        if (point.getTag() == -1)
            point.setTag(pointTag++);
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
        Point point = new Point(pointTag++);
        for (int i = 0; i < binds.size(); i++) {
            if (binds.get(i).equals(bind)) {
                point = points.get(i);
                break;
            }
        }
        return point;
    }

    private void updateBinds() {

    }

    private void removeBind() {

    }

    private void addBind() {

    }

    private void load() {
        Log.d(TAG, "PointManager: load");
        cacheManager.load();
        List<Point> mPoints = cacheManager.parsePoints();
        List<String> fakeBinds = new ArrayList<>();
        for (int i = 0; i < mPoints.size(); i++) {
            fakeBinds.add("m" + i);
        }
        addAll(mPoints, fakeBinds);
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
}