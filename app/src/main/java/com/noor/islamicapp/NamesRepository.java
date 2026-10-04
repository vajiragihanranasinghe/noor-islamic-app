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

public class NamesRepository {

    private static final String TAG = "NamesRepository";
    private static List<NameOfAllah> cached;

    public static List<NameOfAllah> getNames(Context context) {
        if (cached != null) return cached;
        try {
            InputStream is = context.getAssets().open("names_99.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<NameOfAllah>>() {}.getType();
            List<NameOfAllah> list = new Gson().fromJson(reader, listType);
            reader.close();
            is.close();
            cached = list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error loading names", e);
            cached = new ArrayList<>();
        }
        return cached;
    }
}
