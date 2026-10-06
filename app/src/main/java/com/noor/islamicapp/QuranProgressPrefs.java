package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class QuranProgressPrefs {

    private static final String PREFS = "quran_progress";
    private static final String LAST_SURAH = "last_surah";
    private static final String LAST_AYAH = "last_ayah";
    private static final String BOOKMARKS = "bookmarks";

    private QuranProgressPrefs() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }

    private static Set<String> getBookmarks(
            Context context
    ) {
        return new HashSet<>(
                prefs(context).getStringSet(
                        BOOKMARKS,
                        new HashSet<>()
                )
        );
    }

    private static String bookmarkKey(
            int surahId,
            int ayahId
    ) {
        return surahId + ":" + ayahId;
    }

    public static void setLastRead(
            Context context,
            int surahId,
            int ayahId
    ) {
        prefs(context)
                .edit()
                .putInt(LAST_SURAH, surahId)
                .putInt(LAST_AYAH, ayahId)
                .apply();
    }

    public static int getLastSurah(
            Context context
    ) {
        return prefs(context)
                .getInt(LAST_SURAH, 0);
    }

    public static int getLastAyah(
            Context context
    ) {
        return prefs(context)
                .getInt(LAST_AYAH, 0);
    }

    public static boolean hasLastRead(
            Context context
    ) {
        return getLastSurah(context) > 0
                && getLastAyah(context) > 0;
    }

    public static boolean isBookmarked(
            Context context,
            int surahId,
            int ayahId
    ) {
        return getBookmarks(context)
                .contains(
                        bookmarkKey(
                                surahId,
                                ayahId
                        )
                );
    }

    public static boolean toggleBookmark(
            Context context,
            int surahId,
            int ayahId
    ) {
        SharedPreferences p = prefs(context);

        Set<String> set =
                getBookmarks(context);

        String key =
                bookmarkKey(
                        surahId,
                        ayahId
                );

        boolean bookmarked;

        if (set.contains(key)) {
            set.remove(key);
            bookmarked = false;
        } else {
            set.add(key);
            bookmarked = true;
        }

        p.edit()
                .putStringSet(BOOKMARKS, set)
                .apply();

        return bookmarked;
    }

    public static int bookmarkCount(
            Context context
    ) {
        return getBookmarks(context).size();
    }

    public static List<Integer> getBookmarkedAyahs(
            Context context,
            int surahId
    ) {
        List<Integer> result =
                new ArrayList<>();

        String prefix = surahId + ":";

        for (String key :
                getBookmarks(context)) {

            if (!key.startsWith(prefix)) {
                continue;
            }

            try {
                result.add(
                        Integer.parseInt(
                                key.substring(
                                        prefix.length()
                                )
                        )
                );
            } catch (Exception ignored) {
            }
        }

        Collections.sort(result);
        return result;
    }

    public static int[] getFirstBookmark(
            Context context
    ) {
        Set<String> set =
                getBookmarks(context);

        int[] best = null;

        for (String key : set) {
            String[] parts =
                    key.split(":");

            if (parts.length != 2) {
                continue;
            }

            try {
                int surah =
                        Integer.parseInt(parts[0]);

                int ayah =
                        Integer.parseInt(parts[1]);

                if (best == null
                        || surah < best[0]
                        || (surah == best[0]
                        && ayah < best[1])) {

                    best =
                            new int[]{
                                    surah,
                                    ayah
                            };
                }

            } catch (Exception ignored) {
            }
        }

        return best;
    }
}
