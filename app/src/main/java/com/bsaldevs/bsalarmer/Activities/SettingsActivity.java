package com.bsaldevs.bsalarmer.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bsaldevs.bsalarmer.BroadcastActions;
import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.R;
import com.bsaldevs.bsalarmer.Services.MainService;

public class SettingsActivity extends AppCompatActivity {

    private Button selectMusicButton;
    private static final int requestCodeForSongChoose = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initGUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeForSongChoose && resultCode == RESULT_OK && data != null) {
            Uri song = data.getData();
            Intent alarm = new Intent(Constants.ALARM_ACTION)
                    .putExtra("task", BroadcastActions.SET_SONG)
                    .putExtra("song", song);
            sendBroadcast(alarm);
        }
        Intent returning = new Intent(SettingsActivity.this, MapsActivity.class);
        startActivity(returning);
    }
    public void initGUI()
    {

        selectMusicButton =findViewById(R.id.buttonsetMusic);
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, requestCodeForSongChoose);


            }
        });
        selectMusicButton=findViewById(R.id.buttonOpenPointList);
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opener = new Intent(SettingsActivity.this,PointListActivity.class);
                startActivity(opener);
            }
        });
    }
}
