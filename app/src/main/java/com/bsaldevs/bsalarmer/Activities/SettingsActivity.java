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

public class SettingsActivity extends AppCompatActivity {

    private Button selectMusicButton;
    private static final int requestCodeForSongChoose = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        selectMusicButton = findViewById(R.id.buttonsetMusic);
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/*");
                    startActivityForResult(intent, requestCodeForSongChoose);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("audio/*");
                    startActivityForResult(intent, requestCodeForSongChoose);
                }
            }
        });
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
    }
}
