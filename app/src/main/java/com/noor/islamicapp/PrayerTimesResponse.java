package com.noor.islamicapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PrayerTimesResponse {

    @SerializedName("code")
    public int code;

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public Data data;

    public static class Data {
        @SerializedName("timings")
        public Timings timings;

        @SerializedName("date")
        public DateInfo date;

        @SerializedName("meta")
        public Meta meta;
    }

    public static class Timings {
        @SerializedName("Fajr")     public String fajr;
        @SerializedName("Sunrise")  public String sunrise;
        @SerializedName("Dhuhr")    public String dhuhr;
        @SerializedName("Asr")      public String asr;
        @SerializedName("Sunset")   public String sunset;
        @SerializedName("Maghrib")  public String maghrib;
        @SerializedName("Isha")     public String isha;
        @SerializedName("Imsak")    public String imsak;
        @SerializedName("Midnight") public String midnight;
    }

    public static class DateInfo {
        @SerializedName("readable")  public String readable;
        @SerializedName("timestamp") public String timestamp;

        @SerializedName("hijri")
        public Hijri hijri;

        @SerializedName("gregorian")
        public Gregorian gregorian;
    }

    public static class Hijri {
        @SerializedName("date")     public String date;
        @SerializedName("day")      public String day;
        @SerializedName("month")    public MonthInfo month;
        @SerializedName("year")     public String year;
    }

    public static class Gregorian {
        @SerializedName("date")     public String date;
        @SerializedName("day")      public String day;
        @SerializedName("month")    public MonthInfo month;
        @SerializedName("year")     public String year;
        @SerializedName("weekday")  public Weekday weekday;
    }

    public static class MonthInfo {
        @SerializedName("number")   public int number;
        @SerializedName("en")       public String en;
    }

    public static class Weekday {
        @SerializedName("en")       public String en;
    }

    public static class Meta {
        @SerializedName("method")   public Method method;
        @SerializedName("timezone") public String timezone;
    }

    public static class Method {
        @SerializedName("id")       public int id;
        @SerializedName("name")     public String name;
    }
}
