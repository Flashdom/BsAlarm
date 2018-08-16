package com.bsaldevs.bsalarmer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private String TAG = Constants.TAG;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean locationPermissionGranted = false;

    private Projection projection;
    private ImageView trashView;
    private MyLocation myLocation;

    final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            + "/music.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        trashView = findViewById(R.id.trashView);
        Intent intent = getIntent();
        myLocation = (MyLocation) intent.getSerializableExtra("MY_LOCATION");

        getLocationPermission();
        initMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (locationPermissionGranted) {
            Log.d(TAG, "locationPermissionGranted: true");
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onMapReady: we can't get needs permission");
                return;
            }

            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "setMyLocation(true)");
        } else {
            Log.d(TAG, "onMapReady: locationPermissionGranted is false");
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showPointOfCreatingDialog(latLng);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick");
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "onMarkerClick");
                marker.showInfoWindow();
                return false;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "onMarkerDragStart");
                trashView.setVisibility(View.VISIBLE);
                projection = mMap.getProjection();
                Log.d(Constants.TAG, "onMarkerDragStart: currentMarker position: lat = " + marker.getPosition().latitude + ", lng = " + marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.d(TAG, "onMarkerDrag");
                LatLng latLngPosition = marker.getPosition();
                android.graphics.Point screenPosition = projection.toScreenLocation(latLngPosition);
                Log.d(TAG, "screen position of current marker = " + screenPosition);
                Log.d(TAG, "maps position of current marker = " + marker.getPosition().toString());
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "onMarkerDragEnd");

                if (isAboveTrashZone(marker)) {

                    Point point = new Point(marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle());
                    point.setId(marker.getId());

                    myLocation.removePoint(point);
                    marker.remove();
                    Toast.makeText(MapsActivity.this, "marker has been deleted", Toast.LENGTH_SHORT).show();

                    updateService();

                    save();
                    Log.d(TAG, "onMarkerDragEnd: marker has been deleted");
                }

                trashView.setVisibility(View.INVISIBLE);
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d(TAG, "onCameraMove");
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG, "onInfoWindowClick");
                Toast.makeText(MapsActivity.this, "onInfoWindowClick", Toast.LENGTH_SHORT).show();
            }
        });

        load();
    }

    private void showPointOfCreatingDialog(final LatLng latLng) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_mark, null);
        final EditText editStationName = mView.findViewById(R.id.stationNameEdit);
        final Button confirmStationName = mView.findViewById(R.id.stationNameConfirm);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        confirmStationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Mark: " + String.valueOf(editStationName.getText()) + " successfully added to map", Toast.LENGTH_SHORT).show();
                addPointToMap(latLng, new String(String.valueOf(editStationName.getText())));
                dialog.dismiss();
            }
        });
    }

    private void updateService() {
        Intent intent = new Intent(MapsActivity.this, AlarmService.class);
        intent.putExtra("MY_LOCATION", myLocation);
        startService(intent);
    }

    private boolean isAboveTrashZone(Marker marker) {

        LatLng latLngPosition = marker.getPosition();
        android.graphics.Point screenPosition = projection.toScreenLocation(latLngPosition);

        // Получение размеров экрана и элементов экрана

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        int height = mapFragment.getView().getHeight();

        Log.d(TAG, "onMarkerDragEnd: screen position of point is " + screenPosition.toString());
        Log.d(TAG, "onMarkerDragEnd: screen height is " + height);

        if (screenPosition.y > height - trashView.getHeight())
            return true;
        else
            return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d(TAG, "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        super.onBackPressed();
    }

    public void addPointToMap(LatLng position, String title) {
        Marker marker = createMarker(position, title);
        moveAndZoomCamera(position, mMap.getCameraPosition().zoom);

        Point point = new Point(position.latitude, position.longitude, title);
        point.setId(marker.getId());
        myLocation.addPoint(point);

        updateService();
        save();
    }

    private Marker createMarker(LatLng position, String name) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(name)
                .draggable(true);
        Log.d(TAG, "createMarker: new point added to map: " + mMap + ", position: " + position);
        return mMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                Log.d(TAG, "case LOCATION_PERMISSION_REQUEST_CODE");
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                    }
                    locationPermissionGranted = true;
                    initMap();
                    Log.d(TAG, "map has been initialized");
                }
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission() {
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                Log.d(TAG, "location permission granted");
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermissionGranted) {
                Log.d(TAG, "getDeviceLocation: locationPermissionGranted");
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Log.d(TAG, "getDeviceLocation: onComplete");
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: location lat: " + currentLocation.getLatitude() + ", lng: " + currentLocation.getLongitude());
                            myLocation.setLocation(currentLocation);
                            Log.d(TAG, "onComplete: set location to myLocation");
                            moveAndZoomCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }

    private void moveAndZoomCamera(LatLng position, float zoom) {
        int time = 500;
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(position)
                .zoom(zoom)
                .build()), time, null);
    }

    private void readMarkers(String data) {

        String lat = "";
        String lng = "";
        String name = "";

        boolean isLat = true;
        boolean isPosition = true;
        int count = 0;

        for (int i = 0; i < data.length(); i++) {

            if (data.charAt(i) == '\n') {

                double latd = 0;
                double lngd = 0;

                try {
                    latd = Double.parseDouble(lat);
                    lngd = Double.parseDouble(lng);
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }

                addPointToMap(new LatLng(latd, lngd), name);

                lat = "";
                lng = "";
                name = "";
                count = 0;
                isLat = true;
                isPosition = true;

                continue;
            }

            if (data.charAt(i) == ';') {
                count++;
                if (count == 1)
                    isLat = false;
                if (count == 2)
                    isPosition = false;
                continue;
            }

            if (isPosition) {
                if (isLat)
                    lat += data.charAt(i);
                else
                    lng += data.charAt(i);
            } else {
                name += data.charAt(i);
            }
        }
    }

    private String writeMarkers() {
        String data = "";
        for (Point point : myLocation.getPoints()) {
            data += point.getLat();
            data += ";";
            data += point.getLng();
            data += ";";
            data += point.getTitle();
            data += "\n";
        }
        return data;
    }

    private void load() {
        Log.d(Constants.TAG, "loading user data from file: " + Constants.MARKERS_FILE_NAME);
        FileInputStream in = null;

        try {
            in = openFileInput(Constants.MARKERS_FILE_NAME);
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            String text = new String(bytes);

            Log.d(TAG, "file length is " + text.length());
            Log.d(TAG, text);

            readMarkers(text);
            Toast.makeText(this, "The file was loaded", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finally {
            try {
                if (in != null)
                    in.close();
            }
            catch(IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void save() {
        Log.d(Constants.TAG, "saving user data to file: " + Constants.MARKERS_FILE_NAME);
        FileOutputStream out = null;

        String data = writeMarkers();

        try {
            out = openFileOutput(Constants.MARKERS_FILE_NAME, MODE_PRIVATE);
            Log.d(Constants.TAG, "The file was opened");
            Log.d(Constants.TAG, data);
            out.write(data.getBytes());
            Toast.makeText(this, "The file was saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                    Log.d(Constants.TAG, "The file was closed");
                }
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}