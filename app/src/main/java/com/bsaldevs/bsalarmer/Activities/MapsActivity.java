package com.bsaldevs.bsalarmer.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.Point;
import com.bsaldevs.bsalarmer.R;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private String TAG = Constants.TAG;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final int TASK_ADD_TARGET_CODE = 100;
    private static final int TASK_REMOVE_TARGET_CODE = 101;
    private static final int TASK_CHANGE_LOCATION_CODE = 102;
    private static final int TASK_SET_LOCATION_CODE = 103;

    private static final int TASK_GET_TARGETS_CODE = 200;
    private static final int TASK_ON_MAP_READY_CODE = 201;

    private BroadcastReceiver receiver;

    private Boolean locationPermissionGranted = false;

    private Projection projection;
    private ImageView trashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        trashView = findViewById(R.id.trashView);
        receiver = new MyReceiver();

        IntentFilter intentFilter = new IntentFilter(Constants.MAPS_ACTION);
        registerReceiver(receiver, intentFilter);

        getLocationPermission();
        initMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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

        sendOnMapReady();

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
                    removeTarget(marker.getId());
                    marker.remove();
                    Toast.makeText(MapsActivity.this, "marker has been deleted", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "onMarkerDragEnd: marker has been deleted");
                } else {
                    changeTargetPosition(marker.getPosition().latitude, marker.getPosition().longitude, marker.getId());
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
                addPoint(latLng, new String(String.valueOf(editStationName.getText())));
                dialog.dismiss();
            }
        });
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

    public void addPoint(LatLng position, String title) {
        Marker marker = createMarker(position, title);
        moveAndZoomCamera(position, mMap.getCameraPosition().zoom);
        double radius = 0;
        Point point = new Point(position.latitude, position.longitude, radius, title, 2);
        addTarget(point, marker.getId());
    }

    public void addPointWithoutSending(LatLng position, String title) {
        createMarker(position, title);
        moveAndZoomCamera(position, mMap.getCameraPosition().zoom);
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
                            Location currentLocation = task.getResult();
                            Log.d(TAG, "onComplete: location lat: " + currentLocation.getLatitude() + ", lng: " + currentLocation.getLongitude());
                            sendNewLocationToLocationService(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d(TAG, "onComplete: success set location");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            Log.d(Constants.TAG, "MapsActivity: onActivityResult: request code 11");
        }
    }

    private void sendNewLocationToLocationService(double lat, double lng) {
        Log.d(TAG, "sendNewLocationToLocationService");
        Intent location = new Intent(Constants.BROADCAST_ACTION)
                .putExtra("task", TASK_SET_LOCATION_CODE)
                .putExtra("lat", lat)
                .putExtra("lng", lng);
        sendBroadcast(location);
    }

    private void addTarget(Point point, String bind) {
        Log.d(TAG, "addTarget");
        Intent location = new Intent(Constants.BROADCAST_ACTION)
                .putExtra("task", TASK_ADD_TARGET_CODE)
                .putExtra("point", point)
                .putExtra("bind", bind);
        sendBroadcast(location);
    }

    private void removeTarget(String bind) {
        Log.d(TAG, "removeTarget");
        Intent location = new Intent(Constants.BROADCAST_ACTION)
                .putExtra("task", TASK_REMOVE_TARGET_CODE)
                .putExtra("bind", bind);
        sendBroadcast(location);
    }

    private void changeTargetPosition(double lat, double lng, String bind) {
        Log.d(TAG, "changeTargetPosition");
        Intent location = new Intent(Constants.BROADCAST_ACTION)
                .putExtra("task", TASK_CHANGE_LOCATION_CODE)
                .putExtra("lat", lat)
                .putExtra("lng", lng)
                .putExtra("bind", bind);
        sendBroadcast(location);
    }

    private void sendOnMapReady() {
        Log.d(TAG, "sendOnMapReady");
        Intent location = new Intent(Constants.BROADCAST_ACTION)
                .putExtra("task", TASK_ON_MAP_READY_CODE);
        sendBroadcast(location);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int task = intent.getIntExtra("task", 0);
            Log.d(TAG, "MapsActivity: onReceive: task code " + task);
            if (task == TASK_GET_TARGETS_CODE) {
                List<Point> points = (ArrayList<Point>) intent.getSerializableExtra("points");
                for (Point point : points) {
                    addPointWithoutSending(new LatLng(point.getLatitude(), point.getLongitude()), point.getName());
                }
            }
        }
    }

}