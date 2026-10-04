package com.noor.islamicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HijriFragment extends Fragment {

    private static final double DEFAULT_LAT = 6.9271;
    private static final double DEFAULT_LON = 79.8612;

    private TextView tvHijriToday, tvGregToday;
    private RecyclerView rvMonths;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hijri, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tvHijriToday = v.findViewById(R.id.tvHijriToday);
        tvGregToday = v.findViewById(R.id.tvGregToday);
        rvMonths = v.findViewById(R.id.rvMonths);

        // Gregorian today
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        tvGregToday.setText(sdf.format(new Date()));

        // Load Hijri via Aladhan API
        loadHijriDate();

        // Month list
        List<MonthAdapter.Month> months = new ArrayList<>();
        months.add(new MonthAdapter.Month(1,  "Muharram",     "مُحَرَّم",       "The Sacred Month"));
        months.add(new MonthAdapter.Month(2,  "Safar",        "صَفَر",          "The Empty Month"));
        months.add(new MonthAdapter.Month(3,  "Rabi' al-Awwal","رَبِيع الأَوَّل",  "First Spring"));
        months.add(new MonthAdapter.Month(4,  "Rabi' al-Thani","رَبِيع الثَّانِي",  "Second Spring"));
        months.add(new MonthAdapter.Month(5,  "Jumada al-Ula","جُمَادَى الأُولَى", "First of Dryness"));
        months.add(new MonthAdapter.Month(6,  "Jumada al-Akhirah","جُمَادَى الآخِرَة", "Second of Dryness"));
        months.add(new MonthAdapter.Month(7,  "Rajab",        "رَجَب",          "The Honored Month"));
        months.add(new MonthAdapter.Month(8,  "Sha'ban",      "شَعْبَان",       "The Month of Separation"));
        months.add(new MonthAdapter.Month(9,  "Ramadan",      "رَمَضَان",       "The Month of Fasting"));
        months.add(new MonthAdapter.Month(10, "Shawwal",      "شَوَّال",         "The Month of Rising"));
        months.add(new MonthAdapter.Month(11, "Dhul-Qa'dah",  "ذُو القَعْدَة",   "The Month of Truce"));
        months.add(new MonthAdapter.Month(12, "Dhul-Hijjah",  "ذُو الحِجَّة",    "The Month of Hajj"));

        rvMonths.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMonths.setAdapter(new MonthAdapter(months));

        // Get Hijri date from same Aladhan API
        RetrofitClient.getApi()
                .getTimings(DEFAULT_LAT, DEFAULT_LON, 3)
                .enqueue(new Callback<PrayerTimesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PrayerTimesResponse> call,
                                           @NonNull Response<PrayerTimesResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null
                                && response.body().data != null
                                && response.body().data.date != null
                                && response.body().data.date.hijri != null) {
                            PrayerTimesResponse.Hijri h = response.body().data.date.hijri;
                            String hijri = h.day + " " +
                                    (h.month != null ? h.month.en : "") + " " +
                                    h.year + " AH";
                            tvHijriToday.setText(hijri);
                        } else {
                            tvHijriToday.setText("—");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PrayerTimesResponse> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        tvHijriToday.setText("—");
                    }
                });
    }

    private void loadHijriDate() {
        tvHijriToday.setText("Loading…");
    }
}
