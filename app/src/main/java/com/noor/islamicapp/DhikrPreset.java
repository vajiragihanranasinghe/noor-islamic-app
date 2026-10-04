package com.noor.islamicapp;

public class DhikrPreset {
    public final String arabic;
    public final String transliteration;
    public final String meaning;
    public final int target;

    public DhikrPreset(String arabic, String transliteration, String meaning, int target) {
        this.arabic = arabic;
        this.transliteration = transliteration;
        this.meaning = meaning;
        this.target = target;
    }
}
