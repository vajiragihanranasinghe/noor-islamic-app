package com.noor.islamicapp;

public class MethodResolver {

    public static int methodFor(double lat, double lon) {
        if (lat >= 12 && lat <= 32 && lon >= 34 && lon <= 60) {
            if (lon >= 44 && lon <= 63 && lat >= 25 && lat <= 40) return 3;
            return 4;
        }
        if (lat >= 24 && lat <= 40 && lon >= 44 && lon <= 64) return 7;
        if (lat >= 35 && lat <= 43 && lon >= 25 && lon <= 45) return 13;
        if (lat >= 5 && lat <= 38 && lon >= 60 && lon <= 98) return 1;
        if (lat >= -11 && lat <= 25 && lon >= 95 && lon <= 142) return 17;
        if (lat >= 15 && lat <= 72 && lon >= -170 && lon <= -50) return 2;
        if (lat >= 35 && lat <= 72 && lon >= -15 && lon <= 40) {
            if (lon >= -10 && lon <= -6 && lat >= 36 && lat <= 42) return 22;
            return 3;
        }
        if (lat >= -35 && lat <= 37 && lon >= -20 && lon <= 52) {
            if (lon >= 24 && lon <= 36 && lat >= 20 && lat <= 32) return 5;
            if (lon >= 8 && lon <= 12 && lat >= 30 && lat <= 38) return 18;
            if (lon >= -17 && lon <= 9 && lat >= 20 && lat <= 38) return 21;
            if (lon >= -9 && lon <= 12 && lat >= 18 && lat <= 38) return 19;
            return 5;
        }
        if (lat >= -50 && lat <= -10 && lon >= 110 && lon <= 180) return 3;
        return 3;
    }

    public static int madhabFor(double lat, double lon) {
        if (lat >= 5 && lat <= 38 && lon >= 60 && lon <= 98) return 1;
        if (lat >= 35 && lat <= 55 && lon >= 25 && lon <= 90) return 1;
        if (lat >= 20 && lat <= 55 && lon >= 95 && lon <= 135) return 1;
        return 0;
    }

    public static int highLatFor(double lat) {
        double abs = Math.abs(lat);
        if (abs >= 48) return 3;
        return 0;
    }

    public static String describeMethod(int id) {
        switch (id) {
            case 1:  return "Karachi";
            case 2:  return "ISNA";
            case 3:  return "MWL";
            case 4:  return "Umm al-Qura";
            case 5:  return "Egyptian";
            case 7:  return "Tehran";
            case 8:  return "Gulf";
            case 13: return "Diyanet";
            case 16: return "Dubai";
            case 17: return "JAKIM";
            case 18: return "Tunisia";
            case 19: return "Algeria";
            case 20: return "KEMENAG";
            case 21: return "Morocco";
            case 22: return "Lisbon";
            case 23: return "Jordan";
            default: return "MWL";
        }
    }

    public static String describeMadhab(int id) {
        return id == 1 ? "Hanafi" : "Shafi";
    }

    public static String describeHighLat(int id) {
        switch (id) {
            case 1: return "Mid-Night";
            case 2: return "One-Seventh";
            case 3: return "Angle-Based";
            default: return "None";
        }
    }
}
