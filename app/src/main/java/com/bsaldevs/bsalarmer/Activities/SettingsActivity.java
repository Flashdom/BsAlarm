package com.bsaldevs.bsalarmer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.R;
import com.bsaldevs.bsalarmer.Services.MainService;

public class SettingsActivity extends AppCompatActivity {
    Button musicButoon;
    private String song = " ";
    private static final int requestCodeForSongChoose = 222;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        musicButoon=findViewById(R.id.buttonsetMusic);
        musicButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*"); //???
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, requestCodeForSongChoose);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == requestCodeForSongChoose) && (resultCode == RESULT_OK ) && (data!=null)) {
            song = data.getData().toString();
            Intent alarm = new Intent(Constants.NOTIFICATION_ACTION)
                    .putExtra("song", song);
            sendBroadcast(alarm);
            startMainService();
            Intent returning = new Intent(SettingsActivity.this, MainMenuActivity.class);
            startActivity(returning);
        }
    }
    public void startMainService() {
        Log.d(Constants.TAG, "startMainService");
        Intent mainService = new Intent(this, MainService.class);
        mainService.putExtra("song", song);
        startService(mainService);
    }
}
