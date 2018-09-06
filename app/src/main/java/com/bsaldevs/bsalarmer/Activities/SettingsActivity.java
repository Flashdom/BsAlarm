package com.bsaldevs.bsalarmer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bsaldevs.bsalarmer.Constants;
import com.bsaldevs.bsalarmer.R;
import com.bsaldevs.bsalarmer.Services.MainService;

public class SettingsActivity extends AppCompatActivity {
    Button musicButton;
    Button pointListButton;
    private String song = " ";
    private static boolean isChosen=false;
    public static boolean isChosen() {
        return isChosen;
    }


    private static final int requestCodeForSongChoose = 222;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

       initGUI();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == requestCodeForSongChoose) && (resultCode == RESULT_OK ) && (data!=null)) {
            song = data.getData().toString();
            Intent alarm = new Intent(SettingsActivity.this, MainService.class);
                    alarm.putExtra("song", song);
            Intent returning = new Intent(SettingsActivity.this, MainMenuActivity.class);
            startActivity(returning);
        }
    }
    public void initGUI()
    {

        musicButton =findViewById(R.id.buttonsetMusic);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChosen=true;
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, requestCodeForSongChoose);

            }
        });
        pointListButton=findViewById(R.id.buttonOpenPointList);
        pointListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opener = new Intent(SettingsActivity.this,PointListActivity.class);
                startActivity(opener);
            }
        });
    }
}
