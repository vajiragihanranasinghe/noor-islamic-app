package com.noor.islamicapp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuranRepository {

    private static final String TAG = "QuranRepository";
    private static List<Surah> cachedSurahs;
    private static List<Surah> cachedEnSurahs;

    /**
     * Load all surahs with Arabic text + basic metadata.
     * Merges in English translations from the second file.
     */
    public static List<Surah> getSurahs(Context context) {
        if (cachedSurahs != null) return cachedSurahs;

        List<Surah> arList = loadJson(context, "quran_ar.json");
        List<Surah> enList = loadJson(context, "quran_en.json");

        // Merge English translations into the Arabic list
        if (arList != null && enList != null) {
            for (int i = 0; i < arList.size() && i < enList.size(); i++) {
                Surah ar = arList.get(i);
                Surah en = enList.get(i);

                // English metadata (translation of surah name)
                ar.translation = en.translation;

                // Merge English translation into each ayah
                if (ar.verses != null && en.verses != null) {
                    for (int j = 0; j < ar.verses.size() && j < en.verses.size(); j++) {
                        ar.verses.get(j).textEn = en.verses.get(j).textEn;
                    }
                }
            }
        }

        cachedSurahs = arList != null ? arList : new ArrayList<>();
        Log.d(TAG, "Loaded " + cachedSurahs.size() + " surahs");
        return cachedSurahs;
    }

    public static Surah getSurah(Context context, int surahId) {
        List<Surah> all = getSurahs(context);
        for (Surah s : all) {
            if (s.id == surahId) return s;
        }
        return null;
    }

    private static List<Surah> loadJson(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Surah>>() {}.getType();
            List<Surah> result = new Gson().fromJson(reader, listType);
            reader.close();
            is.close();
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error loading " + filename, e);
            return null;
        }
    }
}
