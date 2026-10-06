package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public final class FavoritesPrefs {

    private static final String PREFS = "noor_favorites";
    private static final String DUA_FAVORITES = "dua_favorites";
    private static final String NAME_FAVORITES = "name_favorites";

    private FavoritesPrefs() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    private static Set<String> getSet(
            Context context,
            String key
    ) {
        return new HashSet<>(
                prefs(context).getStringSet(
                        key,
                        new HashSet<>()
                )
        );
    }

    private static String duaKey(Dua dua) {
        String value =
                String.valueOf(dua.category) + "|" +
                String.valueOf(dua.title) + "|" +
                String.valueOf(dua.arabic);

        return Integer.toHexString(value.hashCode());
    }

    private static String nameKey(int number) {
        return String.valueOf(number);
    }

    public static boolean isDuaFavorite(
            Context context,
            Dua dua
    ) {
        return getSet(
                context,
                DUA_FAVORITES
        ).contains(duaKey(dua));
    }

    public static boolean toggleDuaFavorite(
            Context context,
            Dua dua
    ) {
        SharedPreferences p = prefs(context);

        Set<String> set =
                getSet(context, DUA_FAVORITES);

        String key = duaKey(dua);

        boolean favorite;

        if (set.contains(key)) {
            set.remove(key);
            favorite = false;
        } else {
            set.add(key);
            favorite = true;
        }

        p.edit()
                .putStringSet(DUA_FAVORITES, set)
                .apply();

        return favorite;
    }

    public static boolean isNameFavorite(
            Context context,
            NameOfAllah name
    ) {
        return getSet(
                context,
                NAME_FAVORITES
        ).contains(nameKey(name.number));
    }

    public static boolean toggleNameFavorite(
            Context context,
            NameOfAllah name
    ) {
        SharedPreferences p = prefs(context);

        Set<String> set =
                getSet(context, NAME_FAVORITES);

        String key = nameKey(name.number);

        boolean favorite;

        if (set.contains(key)) {
            set.remove(key);
            favorite = false;
        } else {
            set.add(key);
            favorite = true;
        }

        p.edit()
                .putStringSet(NAME_FAVORITES, set)
                .apply();

        return favorite;
    }

    public static int getDuaFavoriteCount(
            Context context
    ) {
        return getSet(
                context,
                DUA_FAVORITES
        ).size();
    }

    public static int getNameFavoriteCount(
            Context context
    ) {
        return getSet(
                context,
                NAME_FAVORITES
        ).size();
    }
}
