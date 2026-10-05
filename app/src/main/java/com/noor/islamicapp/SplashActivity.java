package com.noor.islamicapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Create the welcome audio and start from the beginning
        mediaPlayer = MediaPlayer.create(
                this,
                R.raw.allahu_akbar_notify
        );

        if (mediaPlayer != null) {

            mediaPlayer.seekTo(0);

            mediaPlayer.setOnCompletionListener(mp -> {
                openMainActivity();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                openMainActivity();
                return true;
            });

            mediaPlayer.start();

        } else {
            // If audio cannot be loaded, don't keep the user
            // stuck on the welcome screen.
            openMainActivity();
        }
    }

    private void openMainActivity() {

        if (isFinishing()) {
            return;
        }

        Intent intent =
                new Intent(SplashActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }
}
