package com.noor.islamicapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanApi {

    /**
     * Aladhan Prayer Times API — free, no key required.
     * Method 3 = Muslim World League
     * https://aladhan.com/prayer-times-api
     */
    @GET("v1/timings")
    Call<PrayerTimesResponse> getTimings(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("method")    int method
    );
}
