package com.noor.islamicapp;

import com.google.gson.annotations.SerializedName;

public class Dua {

    @SerializedName("category")
    public String category;

    @SerializedName("title")
    public String title;

    @SerializedName("arabic")
    public String arabic;

    @SerializedName("transliteration")
    public String transliteration;

    @SerializedName("meaning")
    public String meaning;

    @SerializedName("reference")
    public String reference;
}
