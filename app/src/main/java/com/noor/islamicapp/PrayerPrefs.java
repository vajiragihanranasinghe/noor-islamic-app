package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Central place for all prayer-related user preferences.
 */
public class PrayerPrefs {

    private static final String PREFS = "noor_settings";

    public static final String KEY_DARK     = "dark_mode";
    public static final String KEY_NOTIF    = "notifications";
    public static final String KEY_METHOD   = "method";       // 1..23
    public static final String KEY_SCHOOL   = "school";       // 0 = Shafi, 1 = Hanafi
    public static final String KEY_HIGHLAT  = "high_lat";     // 0..3
    public static final String KEY_24H      = "use_24h";      // boolean
    public static final String KEY_CITY     = "city_label";   // "Colombo, Sri Lanka"
    public static final String KEY_LAT      = "lat";
    public static final String KEY_LON      = "lon";
    public static final String KEY_LAST_JSON = "last_json";   // cached API response

    public static SharedPreferences get(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static int method(Context c)   { return get(c).getInt(KEY_METHOD, 3); }
    public static int school(Context c)   { return get(c).getInt(KEY_SCHOOL, 0); }
    public static int highLat(Context c)  { return get(c).getInt(KEY_HIGHLAT, 0); }
    public static boolean use24h(Context c) { return get(c).getBoolean(KEY_24H, false); }
    public static boolean notifOn(Context c) { return get(c).getBoolean(KEY_NOTIF, true); }

    public static String cityLabel(Context c) {
        return get(c).getString(KEY_CITY, "Colombo, Sri Lanka");
    }

    public static double lat(Context c) { return Double.longBitsToDouble(get(c).getLong(KEY_LAT, Double.doubleToLongBits(6.9271))); }
    public static double lon(Context c) { return Double.longBitsToDouble(get(c).getLong(KEY_LON, Double.doubleToLongBits(79.8612))); }

    public static void save(Context c, String label, double lat, double lon) {
        get(c).edit()
                .putString(KEY_CITY, label)
                .putLong(KEY_LAT, Double.doubleToRawLongBits(lat))
                .putLong(KEY_LON, Double.doubleToRawLongBits(lon))
                .apply();
    }
}
