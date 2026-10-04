package com.noor.islamicapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanApi {

    /**
     * Aladhan Prayer Times API — free, no key required.
     * https://aladhan.com/prayer-times-api
     *
     * @param method       calculation method (1..23)
     * @param school       0 = Shafi, 1 = Hanafi (affects Asr only)
     * @param latAdj       high-latitude rule (0..3), 0 = none
     * @param timezone     e.g. "auto" or "Asia/Colombo"
     */
    @GET("v1/timings")
    Call<PrayerTimesResponse> getTimings(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("method")    int method,
            @Query("school")    int school,
            @Query("latitudeAdjustmentMethod") int latAdj,
            @Query("timezonestring") String timezone
    );
}
