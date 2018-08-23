package com.bsaldevs.bsalarmer.Managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Point;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by azatiSea on 20.08.2018.
 */

public class CacheManager implements Serializable {

    private static final String TAG = Constants.TAG;

    private Context context;
    private String buffer;

    public CacheManager(Context context) {
        Log.d(TAG, "CacheManager: constructor");
        this.context = context;
        buffer = "";
    }

    public List<Point> parsePoints() {

        int pointTag = 0;

        Log.d(TAG, "CacheManager: parsePoints");

        List<Point> points = new ArrayList<>();

        String lat = "";
        String lng = "";
        String radius = "";
        String name = "";

        int part = 0;

        for (int i = 0; i < buffer.length(); i++) {

            char symbol = buffer.charAt(i);

            if (symbol == '\n') {

                double latd = 0;
                double lngd = 0;
                double radiusd = 0;

                try {
                    latd = Double.parseDouble(lat);
                    lngd = Double.parseDouble(lng);
                    radiusd = Double.parseDouble(radius);
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }

                Point point = new Point(latd, lngd, radiusd, name, pointTag++);

                points.add(point);

                lat = "";
                lng = "";
                radius = "";
                name = "";
                part = 0;

                continue;
            }

            if (symbol == ';') {
                part++;
                continue;
            }

            switch (part) {
                case 0:
                    lat += symbol;
                    break;
                case 1:
                    lng += symbol;
                    break;
                case 2:
                    radius += symbol;
                    break;
                case 3:
                    name += symbol;
                    break;
                default:
                    Log.d(TAG, "switch -> default -> unknown error");
                    break;
            }
        }
        return points;
    }

    private String writePoints(List<Point> points) {

        Log.d(TAG, "CacheManager: writePoints");

        String data = "";
        for (Point point : points) {
            data += point.getLatitude();
            data += ";";
            data += point.getLongitude();
            data += ";";
            data += point.getRadius();
            data += ";";
            data += point.getName();
            data += "\n";
        }
        return data;
    }

    public void load() {
        Log.d(TAG, "CacheManager: load");
        Log.d(Constants.TAG, "loading user data from file: " + Constants.MARKERS_FILE_NAME);
        FileInputStream in = null;

        try {
            in = context.openFileInput(Constants.MARKERS_FILE_NAME);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            String text = new String(bytes);

            Log.d(TAG, "file length is " + text.length());
            Log.d(TAG, text);

            Toast.makeText(context, "The file was loaded", Toast.LENGTH_SHORT).show();

            buffer = text;
        }
        catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            buffer = "";
        }

        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch(IOException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void save(List<Point> points) {

        Log.d(TAG, "CacheManager: save");

        Log.d(TAG, "saving user data to file: " + Constants.MARKERS_FILE_NAME);
        FileOutputStream out = null;

        String data = writePoints(points);

        try {
            out = context.openFileOutput(Constants.MARKERS_FILE_NAME, MODE_PRIVATE);
            Log.d(TAG, "The file was opened");
            Log.d(TAG, data);
            out.write(data.getBytes());
            Toast.makeText(context, "The file was saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                    Log.d(Constants.TAG, "The file was closed");
                }
            } catch (IOException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}