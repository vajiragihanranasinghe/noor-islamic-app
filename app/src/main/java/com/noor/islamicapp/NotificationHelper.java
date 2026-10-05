package com.noor.islamicapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class NotificationHelper {

    public static final String CHANNEL_ID = "noor_adhan_channel";
    public static final String CHANNEL_NAME = "Prayer Notifications";
    private static final String TAG = "NoorNotif";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Adhan notifications for the 5 daily prayers");
            channel.enableVibration(true);
            channel.setShowBadge(true);
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    public static void showPrayerNotification(Context context, String prayer, String time) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, prayer.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Prayer Time: " + prayer)
                .setContentText("It's time for " + prayer + " (" + time + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(pi);

        try {
            NotificationManagerCompat.from(context).notify(prayer.hashCode(), b.build());
            Log.d(TAG, "Notification shown: " + prayer);
        } catch (SecurityException e) {
            Log.e(TAG, "Notification permission denied: " + e.getMessage());
        }
    }

    /** Immediately show a test notification (in ~5 seconds). */
    public static void scheduleTestNotification(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, PrayerReceiver.class);
        intent.putExtra("prayer", "Test");
        intent.putExtra("time", "Now");

        PendingIntent pi = PendingIntent.getBroadcast(
                context, 999999, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long when = System.currentTimeMillis() + 5000;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, when, pi);
                }
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            }
            Log.d(TAG, "Test notification scheduled for +5s");
        } catch (SecurityException e) {
            Log.e(TAG, "Cannot schedule exact alarm: " + e.getMessage());
        }
    }

    public static void schedulePrayer(Context context, String prayer, int hour, int minute) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() <= System.currentTimeMillis() + 1000) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, PrayerReceiver.class);
        intent.putExtra("prayer", prayer);
        intent.putExtra("time", String.format("%02d:%02d", hour, minute));

        PendingIntent pi = PendingIntent.getBroadcast(
                context, prayer.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                    Log.d(TAG, "Exact alarm set for " + prayer + " at " + hour + ":" + minute);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                    Log.w(TAG, "Inexact alarm (permission missing) for " + prayer);
                }
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Cannot schedule alarm for " + prayer + ": " + e.getMessage());
        }
    }

    public static class PrayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prayer = intent.getStringExtra("prayer");
            String time = intent.getStringExtra("time");
            if (prayer != null) {
                showPrayerNotification(context, prayer, time != null ? time : "");
                if (!"Test".equals(prayer) && time != null && time.contains(":")) {
                    try {
                        String[] parts = time.split(":");
                        schedulePrayer(context, prayer,
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]));
                    } catch (Exception ignored) { }
                }
            }
        }
    }
}
