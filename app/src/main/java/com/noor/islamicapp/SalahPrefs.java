package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SalahPrefs {

    private static final String PREFS = "salah_tracker";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    private static String today() {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());
    }

    private static String keyForDate(String date) {
        return "completed_" + date;
    }

    private static Set<String> getCompletedForDate(
            Context context,
            String date
    ) {
        return new HashSet<>(
                prefs(context).getStringSet(
                        keyForDate(date),
                        new HashSet<>()
                )
        );
    }

    public static boolean isCompleted(
            Context context,
            String prayerName
    ) {
        return getCompletedForDate(
                context,
                today()
        ).contains(prayerName);
    }

    public static void setCompleted(
            Context context,
            String prayerName,
            boolean completed
    ) {
        SharedPreferences p = prefs(context);

        String date = today();

        Set<String> current =
                getCompletedForDate(context, date);

        if (completed) {
            current.add(prayerName);
        } else {
            current.remove(prayerName);
        }

        p.edit()
                .putStringSet(
                        keyForDate(date),
                        current
                )
                .apply();
    }

    public static int completedCount(
            Context context
    ) {
        return getCompletedForDate(
                context,
                today()
        ).size();
    }

    public static int completedCountForDate(
            Context context,
            String date
    ) {
        return getCompletedForDate(
                context,
                date
        ).size();
    }

    public static boolean isCompletedForDate(
            Context context,
            String date,
            String prayerName
    ) {
        return getCompletedForDate(
                context,
                date
        ).contains(prayerName);
    }
}
