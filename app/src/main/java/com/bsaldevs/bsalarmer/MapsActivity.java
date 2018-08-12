package com.bsaldevs.bsalarmer;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MyLocation myLocation;

    private String TAG = "CDA";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean locationPermissionGranted = false;

    private Boolean isTouched = false;
    private Boolean isMarkerTouched = false;
    private MediaPlayer mediaPlayer;
    private List<Marker> markers;
    private double mapsZoom;
    public static final int requestCodeForStringTransfer = 333;
    private double epsLat = 1;
    final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            + "/music.mp3";
    private double epsLng = 1;
    Uri mysong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Intent intent = getIntent();
        mysong = Uri.parse(intent.getStringExtra("transfer"));
        markers = new ArrayList<>();
        myLocation = new MyLocation();
        myLocation.addPoint(54.7771791, 56.0792353);
        myLocation.notifyEveryone();

        if (myLocation.wakeMeUp())
            mplayergo();


        getLocationPermission();
        initMap();
    }
    public void mplayergo ()
    {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.create(this, mysong);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), mysong);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {                                                                                                                                                                         //dsd
            e.printStackTrace();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
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
            public void onMapLongClick(final LatLng latLng) {

                isMarkerTouched = false;

                for (Marker mark : markers) {

                    LatLng difference = new LatLng(Math.abs(latLng.latitude - mark.getPosition().latitude), Math.abs(latLng.longitude - mark.getPosition().longitude));

                    Log.d(TAG, latLng.latitude + " ? " + mark.getPosition().latitude + ";" + latLng.longitude + " ? " + mark.getPosition().longitude);
                    Log.d(TAG, "Lat giff = " + difference.latitude);
                    Log.d(TAG, "Lng giff = " + difference.longitude);
                    if (difference.longitude < epsLat && difference.latitude < epsLng) {
                        isMarkerTouched = true;
                        Log.d(TAG, "onMapLongClick: long click to marker");
                    }
                }

                if (!isMarkerTouched) {

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
                            addMarkOfStationToMap(latLng, new String(String.valueOf(editStationName.getText())));
                            dialog.dismiss();
                        }
                    });
                }
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
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.d(TAG, "onMarkerDrag");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "onMarkerDragEnd");
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d(TAG, "onCameraMove");
                mapsZoom = mMap.getCameraPosition().zoom;
                Log.d(TAG, "mapsZoom = " + mapsZoom);

                if (mapsZoom == 2) {
                    epsLat = 2.4;
                    epsLng = 8;
                }

                if (mapsZoom > 2 && mapsZoom <= 2.5) {
                    epsLat = 2.015;
                    epsLng = 6.575;
                }

                if (mapsZoom > 2.5 && mapsZoom <= 3) {
                    epsLat = 1.385;
                    epsLng = 4.415;
                }

                if (mapsZoom > 3 && mapsZoom <= 3.5) {
                    epsLat = 0.98;
                    epsLng = 3.26;
                }

                if (mapsZoom > 3.5 && mapsZoom <= 4) {
                    epsLat = 0.695;
                    epsLng = 2.36;
                }

                if (mapsZoom > 4 && mapsZoom <= 4.5) {
                    epsLat = 0.525;
                    epsLng = 1.65;
                }

                if (mapsZoom > 4.5 && mapsZoom <= 5) {
                    epsLat = 0.4;
                    epsLng = 1.2;
                }

                if (mapsZoom > 5 && mapsZoom <= 5.5) {
                    epsLat = 0.315;
                    epsLng = 0.8;
                }

                if (mapsZoom > 5.5 && mapsZoom <= 6) {
                    epsLat = 0.275;
                    epsLng = 0.5;
                }

                if (mapsZoom > 6 && mapsZoom <= 6.5) {
                    epsLat = 0.18;
                    epsLng = 0.345;
                }

                if (mapsZoom > 6.5 && mapsZoom <= 7) {
                    epsLat = 0.135;
                    epsLng = 0.26;
                }

                if (mapsZoom > 7 && mapsZoom <= 7.5) {
                    epsLat = 0.1;
                    epsLng = 0.19;
                }

                if (mapsZoom > 7.5 && mapsZoom <= 8) {
                    epsLat = 0.065;
                    epsLng = 0.15;
                }

                if (mapsZoom > 8 && mapsZoom <= 8.5) {
                    epsLat = 0.042;
                    epsLng = 0.11;
                }

                if (mapsZoom > 8.5 && mapsZoom <= 9) {
                    epsLat = 0.029;
                    epsLng = 0.0865;
                }

                if (mapsZoom > 9 && mapsZoom <= 9.5) {
                    epsLat = 0.023;
                    epsLng = 0.0615;
                }

                if (mapsZoom > 9.5 && mapsZoom <= 10) {
                    epsLat = 0.016;
                    epsLng = 0.045;
                }

                if (mapsZoom > 10 && mapsZoom <= 10.5) {
                    epsLat = 0.0094;
                    epsLng = 0.03;
                }

                if (mapsZoom > 10.5 && mapsZoom <= 11) {
                    epsLat = 0.00625;
                    epsLng = 0.0197;
                }

                if (mapsZoom > 11 && mapsZoom <= 11.5) {
                    epsLat = 0.0048;
                    epsLng = 0.012;
                }

                if (mapsZoom > 11.5 && mapsZoom <= 12) {
                    epsLat = 0.00315;
                    epsLng = 0.01;
                }

                if (mapsZoom > 12 && mapsZoom <= 12.5) {
                    epsLat = 0.0024;
                    epsLng = 0.008;
                }

                if (mapsZoom > 12.5 && mapsZoom <= 13) {
                    epsLat = 0.00157;
                    epsLng = 0.00545;
                }

                if (mapsZoom > 13 && mapsZoom <= 13.5) {
                    epsLat = 0.000654;
                    epsLng = 0.00365;
                }

                if (mapsZoom > 13.5 && mapsZoom <= 14) {
                    epsLat = 0.000507;
                    epsLng = 0.0027;
                }

                if (mapsZoom > 14 && mapsZoom <= 14.5) {
                    epsLat = 0.000275;
                    epsLng = 0.002;
                }

                if (mapsZoom > 14.5 && mapsZoom <= 15) {
                    epsLat = 0.000217;
                    epsLng = 0.00134;
                }

                if (mapsZoom > 15 && mapsZoom <= 15.5) {
                    epsLat = 0.000195;
                    epsLng = 0.0009;
                }

                if (mapsZoom > 15.5 && mapsZoom <= 16) {
                    epsLat = 0.000155;
                    epsLng = 0.00057;
                }

                if (mapsZoom > 16 && mapsZoom <= 16.5) {
                    epsLat = 0.00014;
                    epsLng = 0.00045;
                }

                if (mapsZoom > 16.5 && mapsZoom <= 17) {
                    epsLat = 0.00001;
                    epsLng = 0.00035;
                }

                if (mapsZoom > 17 && mapsZoom <= 17.5) {
                    epsLat = 0.000085;
                    epsLng = 0.00025;
                }

                if (mapsZoom > 17.5 && mapsZoom <= 18) {
                    epsLat = 0.00005;
                    epsLng = 0.00017;
                }

                if (mapsZoom > 18 && mapsZoom <= 18.5) {
                    epsLat = 0.000035;
                    epsLng = 0.00013;
                }

                if (mapsZoom > 18.5 && mapsZoom <= 19) {
                    epsLat = 0.000028;
                    epsLng = 0.00001;
                }

                if (mapsZoom > 19 && mapsZoom <= 19.5) {
                    epsLat = 0.00002;
                    epsLng = 0.00007;
                }

                if (mapsZoom > 19.5 && mapsZoom <= 20) {
                    epsLat = 0.000015;
                    epsLng = 0.00005;
                }

                if (mapsZoom > 20 && mapsZoom <= 20.5) {
                    epsLat = 0.0000012;
                    epsLng = 0.000035;
                }

                if (mapsZoom > 20.5 && mapsZoom <= 21) {
                    epsLat = 0.000014;
                    epsLng = 0.000016;
                }
            }
        });
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

    public void addMarkOfStationToMap(LatLng position, String name) {
        Log.d("mapLog", "new point added to map: " + mMap + ", position: " + position);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(name);

        Marker marker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(position)
                .zoom(mMap.getCameraPosition().zoom)
                .build()), 500, null);

        markers.add(marker);
       // myLocation.addPoint();
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
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f);
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

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        Toast.makeText(this, "Your location changed", Toast.LENGTH_SHORT).show();
        myLocation.notifyEveryone();
    }
}
