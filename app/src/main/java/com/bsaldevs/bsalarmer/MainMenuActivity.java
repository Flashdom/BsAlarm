package com.bsaldevs.bsalarmer;


import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainMenuActivity extends AppCompatActivity {
    private Button b1;
    private Button b2;
    private static MediaPlayer mediaPlayer;
    private MyLocation myLocation;

    private Uri myFile;
    public static final int requestCodeForSongChoose = 222;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "CDA";

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
                    Intent intent = new Intent(MainMenuActivity.this, MapsActivity.class);
                    if (myFile != null)
                        intent.putExtra("transfer", myFile.toString());
                    intent.putExtra("MY_LOCATION", myLocation);
                    startActivity(intent);
                }
            }
        });
        b2 = findViewById(R.id.button2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, requestCodeForSongChoose);
            }
        });

        initializeApplicationData();
    }

    private void initializeApplicationData() {
        /*
        try {
            if (openFileInput(FILE_NAME) != null) {
                Log.d(TAG, "The file: " + FILE_NAME + " is exist");
                loadUserData();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        saveUserData();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == requestCodeForSongChoose) && (resultCode == RESULT_OK ) && (data!=null)) {
            myFile = data.getData();
        }
    }

    public Uri getMyFile() {
        return myFile;
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

    public void startService(View view) {
        Log.d(TAG, "buttonStartService: onClick");
        Intent intent = new Intent(this, AlarmService.class);
        intent.putExtra("MY_LOCATION", myLocation);
        startService(intent);
        Log.d(TAG, "buttonStartService: startService");
    }
}