package com.noor.islamicapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Surah {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String nameAr;

    @SerializedName("transliteration")
    public String nameEn;

    @SerializedName("translation")
    public String translation;

    @SerializedName("type")
    public String type;  // "meccan" or "medinan"

    @SerializedName("total_verses")
    public int totalVerses;

    @SerializedName("verses")
    public List<Ayah> verses;

    // Helper: is this surah Meccan?
    public boolean isMeccan() {
        return "meccan".equalsIgnoreCase(type);
    }

    // Helper: type display text
    public String getTypeDisplay() {
        return isMeccan() ? "Meccan" : "Medinan";
    }
}
