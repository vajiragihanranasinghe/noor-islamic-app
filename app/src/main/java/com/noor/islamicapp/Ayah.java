package com.noor.islamicapp;

import com.google.gson.annotations.SerializedName;

public class Ayah {

    @SerializedName("id")
    public int id;

    @SerializedName("text")
    public String textAr;

    @SerializedName("translation")
    public String textEn;
}
