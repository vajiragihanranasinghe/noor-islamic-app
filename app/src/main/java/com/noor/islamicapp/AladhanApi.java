package com.noor.islamicapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladhanApi {

    /** Full version — method, school, high-lat rule. Timezone auto-detected by API. */
    @GET("v1/timings")
    Call<PrayerTimesResponse> getTimings(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("method")    int method,
            @Query("school")    int school,
            @Query("latitudeAdjustmentMethod") int latAdj
    );

    /** Legacy 3-arg version — kept for backwards compatibility. */
    @GET("v1/timings")
    Call<PrayerTimesResponse> getTimings(
            @Query("latitude")  double latitude,
            @Query("longitude") double longitude,
            @Query("method")    int method
    );
}
