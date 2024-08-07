package com.example.loanbuddycalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2500;
    private static final String PREFS_NAME = "LoanBuddy";
    private static final String FIRST_TIME_KEY = "firstTime";
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Initialize and start playing the soundtrack
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_soundtrack);
        mediaPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Stop the music when navigating away from the splash screen
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isFirstTime = sharedPreferences.getBoolean(FIRST_TIME_KEY, true);

                Intent intent;
                if (isFirstTime) {
                    intent = new Intent(SplashScreen.this, BirthYear.class);
                } else {
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
