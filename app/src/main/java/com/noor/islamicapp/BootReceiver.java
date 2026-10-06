package com.noor.islamicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "NoorBoot";

    @Override
    public void onReceive(
            Context context,
            Intent intent) {

        if (intent == null) return;

        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
                || Intent.ACTION_DATE_CHANGED.equals(action)) {

            Log.d(
                    TAG,
                    "System event: " + action +
                            " - restoring prayer alarms"
            );

            try {

                NotificationHelper
                        .restoreSavedPrayerAlarms(context);

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "Could not restore prayer alarms",
                        e
                );
            }
        }
    }
}
