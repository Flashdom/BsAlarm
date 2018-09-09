package com.bsaldevs.bsalarmer.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.MyApplication;
import com.bsaldevs.bsalarmer.Point;
import com.bsaldevs.bsalarmer.R;
import com.bsaldevs.bsalarmer.Services.AlarmService;
import com.bsaldevs.bsalarmer.Utils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private String TAG = Constants.TAG;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean locationPermissionGranted = false;

    private Projection projection;
    private ImageView trashView;

    private boolean isUserAddingPoint = false;
    private EditText searchText;
    private RelativeLayout seekLayout;

    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent alarmService = new Intent(MapsActivity.this, AlarmService.class);
        startService(alarmService);

        application = (MyApplication) getApplication();
        application.initMyLocationManager(this);

        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mapToolbar);

        //Transparent Toolbar

        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Transparent StatusBar

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //makes navigation bar transparent
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        init();
        getLocationPermission();
        initMap();


    }

    private void init() {

        Log.d(TAG, "MapsActivity: init");

        trashView = findViewById(R.id.trashView);

        seekLayout = findViewById(R.id.seekerLayout);
        seekLayout.setVisibility(View.INVISIBLE);

        searchText = findViewById(R.id.input_search);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchText.setCursorVisible(true);
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                    seekLayout.setVisibility(View.INVISIBLE);
                    searchText.setText("");
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "MapsActivity: geoLocate");
        String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate (catch): " + e.getMessage());
        }
        if (addresses.size() > 0) {
            Address address = addresses.get(0);
            Log.d(TAG, "getLocate: found location " + address.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_point_list) {
            Intent openPointList = new Intent(MapsActivity.this, PointListActivity.class);
            startActivity(openPointList);
        } else if (id == R.id.action_add_point) {
            Toast.makeText(MapsActivity.this, "Add point by button", Toast.LENGTH_SHORT).show();
            showSelectAddPointMethodDialog();
        }
        else if (id==R.id.action_settings) {
            Intent openSettings = new Intent(MapsActivity.this, SettingsActivity.class);
            startActivity(openSettings);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSelectAddPointMethodDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_select_method_add_target, null);

        TextView textSelectMethod = mView.findViewById(R.id.textViewSelectAddTargetMethod);

        ImageButton buttonSearch = mView.findViewById(R.id.buttonSearchMethod);
        TextView textSearchMethod = mView.findViewById(R.id.textViewSearchMethod);

        ImageButton buttonTap = mView.findViewById(R.id.buttonTapMethod);
        TextView textTapMethod = mView.findViewById(R.id.textViewTapMethod);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                seekLayout.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);

            }
        });

        buttonTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isUserAddingPoint = true;
                TextView solution = findViewById(R.id.textSolutionAddPoint);
                solution.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setPadding(0, 900, 0, 0);

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

        addTargetsToMap();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "onMapLongClick");
                //showPointCreatingDialog(latLng);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick");
                Toast.makeText(MapsActivity.this, "on map click" , Toast.LENGTH_SHORT);

                if (isUserAddingPoint) {
                    isUserAddingPoint = false;
                    TextView solution = findViewById(R.id.textSolutionAddPoint);
                    solution.setVisibility(View.INVISIBLE);

                    showPointCreatingDialog(latLng);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d(TAG, "onMarkerClick");
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Log.d(TAG, "getDeviceLocation: onComplete");
                        if (task.isSuccessful() && (task.getResult() != null)) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = task.getResult();
                            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Toast.makeText(MapsActivity.this, "" + Utils.CalculateDistanceBetween(marker.getPosition(), myLocation), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                    double lat = marker.getPosition().latitude;
                    double lng = marker.getPosition().longitude;

                    Point point = new Point.Builder()
                            .setLatitude(lat)
                            .setLongitude(lng)
                            .setId(marker.getId())
                            .build();

                    changeTarget(point);
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

    private void addTargetsToMap() {
        List<Point> points = application.getTargetsList();

        Log.d(TAG, "addTargetsToMap: points size " + points.size());

        for (int i = 0; i < points.size(); i++) {

            Point point = points.get(i);

            LatLng position = new LatLng(point.getLatitude(), point.getLongitude());
            String name = point.getName();
            addMarker(position, name);
        }
    }

    private void showPointCreatingDialog(final LatLng latLng) {
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

                String name = String.valueOf(editStationName.getText());

                String id = addMarker(latLng, name);
                Point point = new Point(latLng.latitude, latLng.longitude, 0, name);

                addTarget(point, id);
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

    public String addMarker(LatLng position, String title) {
        Marker marker = createMarker(position, title);
        moveAndZoomCamera(position, mMap.getCameraPosition().zoom);
        return marker.getId();
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
    public void onResume() {
        super.onResume();
        if (AlarmService.isIsAlarming()) {
            AlertDialog.Builder ad;
            String button1String = getResources().getString(R.string.switch_off);
            ad = new AlertDialog.Builder(MapsActivity.this);
            ad.setTitle(R.string.wake_up);  // заголовок
            ad.setMessage(R.string.arrived); // сообщение
            ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    Intent stopAlarming = new Intent(Constants.ALARM_ACTION);
                    stopAlarming.putExtra("task", BroadcastActions.STOP_ALARM);
                    sendBroadcast(stopAlarming);

                }
            });
            ad.setCancelable(true);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                }
            });
            ad.show();
        }
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
                        if (task.isSuccessful() && task.getResult() != null) {
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
        LatLng position = new LatLng(lat, lng);
        application.setUserLocation(position);
    }

    private void addTarget(Point point, String id) {
        Log.d(TAG, "addTarget");
        application.addTarget(point, id);
    }

    private void removeTarget(String id) {
        Log.d(TAG, "removeTarget");
        application.removeTarget(id);
    }

    private void changeTarget(Point point) {
        Log.d(TAG, "changeTargetPosition");
        application.changeTarget(point);
    }

}