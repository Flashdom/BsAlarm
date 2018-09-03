package com.bsaldevs.bsalarmer.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.R;
import com.bsaldevs.bsalarmer.Services.AlarmService;
import com.bsaldevs.bsalarmer.Services.MainService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainMenuActivity extends AppCompatActivity {

    private Button b1;
    private Button b2;

    private static final int requestCodeForSongChoose = 222;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private String song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        song = "";

        b1 = findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServicesOK()) {
                    startMainService();
                    Intent maps = new Intent(MainMenuActivity.this, MapsActivity.class);
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
            song = data.getData().toString();
            Intent alarm = new Intent(Constants.ALARM_ACTION)
                    .putExtra("song", song);
            sendBroadcast(alarm);
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

    public void startMainService() {
        Log.d(Constants.TAG, "startMainService");
        Intent mainService = new Intent(this, MainService.class);
        mainService.putExtra("song", song);
        startService(mainService);
    }
}