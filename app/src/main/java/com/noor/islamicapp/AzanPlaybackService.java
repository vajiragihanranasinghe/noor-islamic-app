package com.noor.islamicapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class AzanPlaybackService extends Service {

    private static final String CHANNEL_ID = "noor_azan_playback";
    private static final int NOTIFICATION_ID = 7001;

    private MediaPlayer player;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        createChannel();
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId) {

        String prayer = "Prayer";

        if (intent != null) {
            String value = intent.getStringExtra("prayer");

            if (value != null && !value.isEmpty()) {
                prayer = value;
            }
        }

        startForeground(
                NOTIFICATION_ID,
                buildNotification(prayer)
        );

        playAzan();

        return START_NOT_STICKY;
    }

    private void playAzan() {

        releasePlayer();

        try {
            requestAudioFocus();

            player = new MediaPlayer();

            AudioAttributes attributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_MUSIC
                            )
                            .build();

            player.setAudioAttributes(attributes);

            player.setWakeMode(
                    getApplicationContext(),
                    android.os.PowerManager.PARTIAL_WAKE_LOCK
            );

            Uri azanUri = Uri.parse(
                    "android.resource://" +
                            getPackageName() +
                            "/raw/azan_holy_makkah"
            );

            player.setDataSource(
                    getApplicationContext(),
                    azanUri
            );

            player.setOnPreparedListener(mp -> {
                mp.start();
            });

            player.setOnCompletionListener(mp -> {
                releasePlayer();
                stopForeground(true);
                stopSelf();
            });

            player.setOnErrorListener((mp, what, extra) -> {
                releasePlayer();
                stopForeground(true);
                stopSelf();
                return true;
            });

            player.prepareAsync();

        } catch (Exception e) {
            releasePlayer();
            stopForeground(true);
            stopSelf();
        }
    }

    private void requestAudioFocus() {

        if (audioManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            audioFocusRequest =
                    new AudioFocusRequest.Builder(
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                    )
                            .setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setUsage(
                                                    AudioAttributes.USAGE_ALARM
                                            )
                                            .setContentType(
                                                    AudioAttributes.CONTENT_TYPE_MUSIC
                                            )
                                            .build()
                            )
                            .setAcceptsDelayedFocusGain(false)
                            .build();

            audioManager.requestAudioFocus(
                    audioFocusRequest
            );

        } else {

            audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            );
        }
    }

    private void abandonAudioFocus() {

        if (audioManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(
                        audioFocusRequest
                );
            }

        } else {

            audioManager.abandonAudioFocus(null);
        }
    }

    private void releasePlayer() {

        if (player != null) {

            try {
                if (player.isPlaying()) {
                    player.stop();
                }
            } catch (Exception ignored) {
            }

            try {
                player.reset();
            } catch (Exception ignored) {
            }

            player.release();
            player = null;
        }

        abandonAudioFocus();
    }

    private Notification buildNotification(String prayer) {

        return new NotificationCompat.Builder(
                this,
                CHANNEL_ID
        )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Azan")
                .setContentText(
                        "Azan playing for " + prayer
                )
                .setOngoing(true)
                .setCategory(
                        NotificationCompat.CATEGORY_SERVICE
                )
                .setPriority(
                        NotificationCompat.PRIORITY_LOW
                )
                .build();
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager nm =
                getSystemService(NotificationManager.class);

        if (nm == null) {
            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "Azan Playback",
                        NotificationManager.IMPORTANCE_LOW
                );

        channel.setSound(null, null);
        channel.enableVibration(false);

        nm.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {

        releasePlayer();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
