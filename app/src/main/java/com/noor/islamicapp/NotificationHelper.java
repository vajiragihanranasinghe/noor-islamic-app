package com.noor.islamicapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class NotificationHelper {

    private static final String TAG = "NoorNotif";

    public static final String CHANNEL_NAME = "Prayer Notifications";

    public static final String PREF_SOUND_MODE = "prayer_sound_mode";
    public static final String PREF_SOUND_URI = "prayer_sound_uri";

    private static final String MODE_DEFAULT = "default";
    private static final String MODE_CUSTOM = "custom";
    private static final String MODE_SILENT = "silent";

    private static final String DEFAULT_CHANNEL_ID = "noor_adhan_default";
    private static final String SILENT_CHANNEL_ID = "noor_adhan_silent";
    private static final String CUSTOM_CHANNEL_PREFIX = "noor_adhan_custom_";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager nm =
                context.getSystemService(NotificationManager.class);

        if (nm == null) return;

        String channelId = getChannelId(context);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.setDescription("Adhan notifications for the 5 daily prayers");
        channel.enableVibration(true);
        channel.setShowBadge(true);

        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

        String mode = getSoundMode(context);

        if (MODE_SILENT.equals(mode)) {
            channel.setSound(null, audioAttributes);
        } else {
            Uri soundUri;

            if (MODE_CUSTOM.equals(mode)) {
                String uriString = context
                        .getSharedPreferences("noor_settings", Context.MODE_PRIVATE)
                        .getString(PREF_SOUND_URI, "");

                soundUri = !uriString.isEmpty()
                        ? Uri.parse(uriString)
                        : getDefaultSound();
            } else {
                soundUri = Uri.parse(
                        "android.resource://" +
                                context.getPackageName() +
                                "/" +
                                R.raw.azan_holy_makkah
                );
            }

            channel.setSound(soundUri, audioAttributes);
        }

        nm.createNotificationChannel(channel);
    }

    private static Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
        );
    }

    public static String getSoundMode(Context context) {
        return context
                .getSharedPreferences("noor_settings", Context.MODE_PRIVATE)
                .getString(PREF_SOUND_MODE, MODE_DEFAULT);
    }

    public static String getChannelId(Context context) {
        String mode = getSoundMode(context);

        if (MODE_SILENT.equals(mode)) {
            return SILENT_CHANNEL_ID;
        }

        if (MODE_CUSTOM.equals(mode)) {
            String uri = context
                    .getSharedPreferences("noor_settings", Context.MODE_PRIVATE)
                    .getString(PREF_SOUND_URI, "");

            return CUSTOM_CHANNEL_PREFIX +
                    Integer.toHexString(uri.hashCode());
        }

        return DEFAULT_CHANNEL_ID;
    }

    public static void showPrayerNotification(
            Context context,
            String prayer,
            String time) {

        createChannel(context);

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(
                context,
                prayer.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(
                        context,
                        getChannelId(context)
                )
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Prayer Time: " + prayer)
                        .setContentText(
                                "It's time for " +
                                        prayer +
                                        " (" +
                                        time +
                                        ")"
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setAutoCancel(true)
                        .setContentIntent(pi);

        try {
            NotificationManagerCompat
                    .from(context)
                    .notify(prayer.hashCode(), b.build());

            Log.d(TAG, "Notification shown: " + prayer);

        } catch (SecurityException e) {
            Log.e(TAG,
                    "Notification permission denied: " +
                            e.getMessage());
        }
    }

    public static void scheduleTestNotification(Context context) {

        AlarmManager am =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE);

        if (am == null) return;

        Intent intent =
                new Intent(context, PrayerReceiver.class);

        intent.putExtra("prayer", "Test");
        intent.putExtra("time", "Now");

        PendingIntent pi =
                PendingIntent.getBroadcast(
                        context,
                        999999,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        long when =
                System.currentTimeMillis() + 5000;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            when,
                            pi
                    );
                } else {
                    am.set(
                            AlarmManager.RTC_WAKEUP,
                            when,
                            pi
                    );
                }

            } else {
                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        when,
                        pi
                );
            }

            Log.d(TAG,
                    "Test notification scheduled for +5s");

        } catch (SecurityException e) {
            Log.e(TAG,
                    "Cannot schedule exact alarm: " +
                            e.getMessage());
        }
    }

    public static void schedulePrayer(
            Context context,
            String prayer,
            int hour,
            int minute) {

        AlarmManager am =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE);

        if (am == null) return;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis()
                <= System.currentTimeMillis() + 1000) {

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent =
                new Intent(context, PrayerReceiver.class);

        intent.putExtra("prayer", prayer);
        intent.putExtra(
                "time",
                String.format("%02d:%02d", hour, minute)
        );

        PendingIntent pi =
                PendingIntent.getBroadcast(
                        context,
                        prayer.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (am.canScheduleExactAlarms()) {

                    am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(),
                            pi
                    );

                } else {

                    am.set(
                            AlarmManager.RTC_WAKEUP,
                            cal.getTimeInMillis(),
                            pi
                    );
                }

            } else {

                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        pi
                );
            }

        } catch (SecurityException e) {

            Log.e(TAG,
                    "Cannot schedule alarm for " +
                            prayer +
                            ": " +
                            e.getMessage());
        }
    }

    public static class PrayerReceiver
            extends BroadcastReceiver {

        @Override
        public void onReceive(
                Context context,
                Intent intent) {

            String prayer =
                    intent.getStringExtra("prayer");

            String time =
                    intent.getStringExtra("time");

            if (prayer != null) {

                showPrayerNotification(
                        context,
                        prayer,
                        time != null ? time : ""
                );

                if (!"Test".equals(prayer)
                        && time != null
                        && time.contains(":")) {

                    try {

                        String[] parts =
                                time.split(":");

                        schedulePrayer(
                                context,
                                prayer,
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1])
                        );

                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
