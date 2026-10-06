package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JummahPrefs {

    private static final String PREFS = "jummah_tracker";

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

    private static String key(String action) {
        return "completed_" + today() + "_" + action;
    }

    public static boolean isCompleted(
            Context context,
            String action
    ) {
        return prefs(context).getBoolean(
                key(action),
                false
        );
    }

    public static void setCompleted(
            Context context,
            String action,
            boolean completed
    ) {
        prefs(context)
                .edit()
                .putBoolean(key(action), completed)
                .apply();
    }
}
