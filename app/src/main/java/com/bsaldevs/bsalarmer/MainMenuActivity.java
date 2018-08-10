package com.bsaldevs.bsalarmer;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;

public class MainMenuActivity extends AppCompatActivity {
    private Button b1;
    private Button b2;
    private MediaPlayer mediaPlayer;
    private Uri myFile;
    public static final int ABC=222;
    private TextView tv1;
    MyLocation myLocation = new MyLocation();

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        tv1=findViewById(R.id.textView);
        b1=findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainMenuActivity.this, MapsActivity.class);
                startActivity(in);
            }
        });
        b2=findViewById(R.id.button2);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);

                i.setType("*/*");
                startActivityForResult(i, ABC);
            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ((requestCode==ABC) && (resultCode == RESULT_OK ) && (data!=null))
        {
             myFile = data.getData();
            tv1.setText(String.valueOf(myLocation.getX())+ " " + String.valueOf(myLocation.getY()));
             myFile = data.getData();

            mediaPlayer=MediaPlayer.create(this,myFile);
            mplayergo(myFile);




        }
    }
    public void mplayergo (Uri sound)
    {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(this, sound);
             mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri getMyFile()
    {
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
}
