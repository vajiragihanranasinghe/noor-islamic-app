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

public class DuasRepository {

    private static final String TAG = "DuasRepository";
    private static List<Dua> cached;

    public static List<Dua> getDuas(Context context) {
        if (cached != null) return cached;
        try {
            InputStream is = context.getAssets().open("duas.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Dua>>() {}.getType();
            List<Dua> list = new Gson().fromJson(reader, listType);
            reader.close();
            is.close();
            cached = list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error loading duas", e);
            cached = new ArrayList<>();
        }
        return cached;
    }

    public static List<String> getCategories(Context context) {
        List<String> cats = new ArrayList<>();
        for (Dua d : getDuas(context)) {
            if (!cats.contains(d.category)) cats.add(d.category);
        }
        return cats;
    }
}
