package com.bsaldevs.bsalarmer;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainMenuActivity extends AppCompatActivity {

    private Button b1;
    private Button b2;
    private MyLocation myLocation;

    public static final int requestCodeForSongChoose = 222;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        myLocation = new MyLocation();

        b1 = findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServicesOK()) {

                    Intent service = new Intent(MainMenuActivity.this, AlarmService.class);
                    service.putExtra("MY_LOCATION", myLocation);
                    startService(service);

                    Intent maps = new Intent(MainMenuActivity.this, MapsActivity.class);
                    maps.putExtra("MY_LOCATION", myLocation);
                    startActivity(maps);
                }
            }
        });
        b2 = findViewById(R.id.button2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, requestCodeForSongChoose);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == requestCodeForSongChoose) && (resultCode == RESULT_OK ) && (data!=null)) {
            Constants.SOUND_URI = data.getData();
        }
    }

    public boolean isServicesOK() {
        Log.d("CDA", "isServicesOK");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainMenuActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d("CDA", "isServicesOK: GP services is available");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d("CDA", "isServicesOK: error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainMenuActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "We can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}