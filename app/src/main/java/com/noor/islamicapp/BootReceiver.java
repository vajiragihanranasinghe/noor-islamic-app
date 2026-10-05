package com.noor.islamicapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        Log.d("NoorBoot", "Phone rebooted — prayer alarms will re-schedule on next app open");
        // Alarms reschedule automatically next time MainActivity runs.
        // For a stronger version, we'd read cached times from PrayerPrefs here.
    }
}
