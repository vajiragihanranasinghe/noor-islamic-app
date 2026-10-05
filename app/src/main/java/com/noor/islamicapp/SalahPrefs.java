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
    private static final String KEY_DATE = "date";
    private static final String KEY_COMPLETED = "completed";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String today() {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());
    }

    private static void ensureToday(Context context) {
        SharedPreferences p = prefs(context);
        String savedDate = p.getString(KEY_DATE, "");

        if (!today().equals(savedDate)) {
            p.edit()
                    .putString(KEY_DATE, today())
                    .remove(KEY_COMPLETED)
                    .apply();
        }
    }

    public static boolean isCompleted(Context context, String prayerName) {
        ensureToday(context);

        Set<String> completed =
                prefs(context).getStringSet(
                        KEY_COMPLETED,
                        new HashSet<>()
                );

        return completed.contains(prayerName);
    }

    public static void setCompleted(
            Context context,
            String prayerName,
            boolean completed
    ) {
        ensureToday(context);

        SharedPreferences p = prefs(context);

        Set<String> current =
                new HashSet<>(
                        p.getStringSet(
                                KEY_COMPLETED,
                                new HashSet<>()
                        )
                );

        if (completed) {
            current.add(prayerName);
        } else {
            current.remove(prayerName);
        }

        p.edit()
                .putStringSet(KEY_COMPLETED, current)
                .apply();
    }

    public static int completedCount(Context context) {
        ensureToday(context);

        return prefs(context)
                .getStringSet(
                        KEY_COMPLETED,
                        new HashSet<>()
                )
                .size();
    }
}
