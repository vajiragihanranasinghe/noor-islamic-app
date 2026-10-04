package com.noor.islamicapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class NotificationHelper {

    public static final String CHANNEL_ID = "noor_adhan_channel";
    public static final String CHANNEL_NAME = "Prayer Notifications";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Adhan notifications for the 5 daily prayers");
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
                .setContentTitle("🕌 " + prayer + " Prayer Time")
                .setContentText("It's time for " + prayer + " (" + time + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pi);

        try {
            NotificationManagerCompat.from(context).notify(prayer.hashCode(), b.build());
        } catch (SecurityException ignored) { }
    }

    // Schedule a prayer notification at a specific hour:minute (24-hour)
    public static void schedulePrayer(Context context, String prayer, int hour, int minute) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // If time has passed today, schedule for tomorrow
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, PrayerReceiver.class);
        intent.putExtra("prayer", prayer);
        intent.putExtra("time", String.format("%02d:%02d", hour, minute));

        PendingIntent pi = PendingIntent.getBroadcast(
                context, prayer.hashCode(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
        }
    }

    public static class PrayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prayer = intent.getStringExtra("prayer");
            String time = intent.getStringExtra("time");
            if (prayer != null) {
                showPrayerNotification(context, prayer, time != null ? time : "");
                // Reschedule for tomorrow
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
