package com.noor.islamicapp;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final double DEFAULT_LAT = 6.9271;
    private static final double DEFAULT_LON = 79.8612;
    private static final String DEFAULT_CITY = "Colombo, Sri Lanka";

    private TextView tvLocation, tvHijriDate, tvGregorianDate;
    private TextView tvNextPrayerName, tvNextPrayerTime, tvNextPrayerCountdown;
    private TextView tvStatus, tvMethod;
    private ImageView btnRefresh;
    private RecyclerView rvPrayers;

    private LocationHelper locationHelper;
    private PrayerAdapter adapter;

    private final Handler ticker = new Handler(Looper.getMainLooper());
    private Prayer nextPrayer;
    private Runnable tickRunnable;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Boolean.TRUE.equals(fine)) loadLocationAndPrayers();
                else loadPrayersWithFallback();
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tvLocation           = v.findViewById(R.id.tvLocation);
        tvHijriDate          = v.findViewById(R.id.tvHijriDate);
        tvGregorianDate      = v.findViewById(R.id.tvGregorianDate);
        tvNextPrayerName     = v.findViewById(R.id.tvNextPrayerName);
        tvNextPrayerTime     = v.findViewById(R.id.tvNextPrayerTime);
        tvNextPrayerCountdown= v.findViewById(R.id.tvNextPrayerCountdown);
        tvStatus             = v.findViewById(R.id.tvStatus);
        tvMethod             = v.findViewById(R.id.tvMethod);
        btnRefresh           = v.findViewById(R.id.btnRefresh);
        rvPrayers            = v.findViewById(R.id.rvPrayers);

        adapter = new PrayerAdapter(new ArrayList<>());
        rvPrayers.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPrayers.setAdapter(adapter);

        btnRefresh.setOnClickListener(x -> loadLocationAndPrayers());

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
        tvGregorianDate.setText(sdf.format(new Date()));

        NotificationHelper.createChannel(requireContext());
        locationHelper = new LocationHelper(requireActivity());

        // start the 1-second ticker
        tickRunnable = new Runnable() {
            @Override public void run() {
                updateCountdown();
                ticker.postDelayed(this, 1000);
            }
        };

        if (locationHelper.hasPermission()) loadLocationAndPrayers();
        else permissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @Override public void onResume() {
        super.onResume();
        if (tickRunnable != null) ticker.post(tickRunnable);
        // reload if user changed settings
        loadLocationAndPrayers();
    }

    @Override public void onPause() {
        super.onPause();
        if (tickRunnable != null) ticker.removeCallbacks(tickRunnable);
    }

    private void loadLocationAndPrayers() {
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("Detecting location…");
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallbackListener() {
            @Override public void onLocationReceived(double lat, double lon, String cityName) {
                if (!isAdded()) return;
                tvLocation.setText("📍 " + cityName);
                PrayerPrefs.save(requireContext(), cityName, lat, lon);
                fetchPrayerTimes(lat, lon);
            }
            @Override public void onLocationFailed(String reason) {
                if (!isAdded()) return;
                loadPrayersWithFallback();
            }
        });
    }

    private void loadPrayersWithFallback() {
        String label = PrayerPrefs.cityLabel(requireContext());
        double lat = PrayerPrefs.lat(requireContext());
        double lon = PrayerPrefs.lon(requireContext());
        if (label.equals("Colombo, Sri Lanka")) label = DEFAULT_CITY + " (default)";
        tvLocation.setText("📍 " + label);
        fetchPrayerTimes(lat, lon);
    }

    private void fetchPrayerTimes(double lat, double lon) {
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("Loading prayer times…");

        int method   = PrayerPrefs.method(requireContext());
        int school   = PrayerPrefs.school(requireContext());
        int highLat  = PrayerPrefs.highLat(requireContext());

        RetrofitClient.getApi()
                .getTimings(lat, lon, method, school, highLat)
                .enqueue(new Callback<PrayerTimesResponse>() {
                    @Override public void onResponse(@NonNull Call<PrayerTimesResponse> call,
                                                     @NonNull Response<PrayerTimesResponse> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null) {
                            PrayerPrefs.get(requireContext()).edit()
                                    .putString(PrayerPrefs.KEY_LAST_JSON,
                                            new Gson().toJson(response.body())).apply();
                            bindData(response.body());
                        } else {
                            tvStatus.setText("Failed: HTTP " + response.code() + " — " + response.message());
                        }
                    }
                    @Override public void onFailure(@NonNull Call<PrayerTimesResponse> call,
                                                    @NonNull Throwable t) {
                        if (!isAdded()) return;
                        // try cache
                        String cached = PrayerPrefs.get(requireContext())
                                .getString(PrayerPrefs.KEY_LAST_JSON, null);
                        if (cached != null) {
                            try {
                                PrayerTimesResponse r = new Gson().fromJson(cached, PrayerTimesResponse.class);
                                bindData(r);
                                tvStatus.setText("Offline — showing cached times");
                                return;
                            } catch (Exception ignored) { }
                        }
                        tvStatus.setText("Network error: " + t.getMessage());
                        Toast.makeText(requireContext(), "Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bindData(PrayerTimesResponse r) {
        if (r.data == null) { tvStatus.setText("No data received"); return; }

        if (r.data.date != null) {
            if (r.data.date.hijri != null) {
                String h = r.data.date.hijri.day + " "
                        + (r.data.date.hijri.month != null ? r.data.date.hijri.month.en : "")
                        + " " + r.data.date.hijri.year + " AH";
                tvHijriDate.setText(h);
            }
            if (r.data.date.gregorian != null) {
                String g = (r.data.date.gregorian.weekday != null ? r.data.date.gregorian.weekday.en + ", " : "")
                        + r.data.date.gregorian.day + " "
                        + (r.data.date.gregorian.month != null ? r.data.date.gregorian.month.en : "")
                        + " " + r.data.date.gregorian.year;
                tvGregorianDate.setText(g);
            }
        }

        String methodLabel = "Method: MWL · " +
                (PrayerPrefs.school(requireContext()) == 1 ? "Hanafi" : "Shafi") +
                (PrayerPrefs.highLat(requireContext()) > 0 ? " · HighLat " + PrayerPrefs.highLat(requireContext()) : "");
        tvMethod.setText(methodLabel);

        List<Prayer> list = new ArrayList<>();
        PrayerTimesResponse.Timings t = r.data.timings;
        if (t != null) {
            list.add(new Prayer("Fajr",    clean(t.fajr),    "🌅"));
            list.add(new Prayer("Sunrise", clean(t.sunrise), "☀️"));
            list.add(new Prayer("Dhuhr",   clean(t.dhuhr),   "🕛"));
            list.add(new Prayer("Asr",     clean(t.asr),     "🌤️"));
            list.add(new Prayer("Maghrib", clean(t.maghrib), "🌆"));
            list.add(new Prayer("Isha",    clean(t.isha),    "🌙"));
        }
        adapter.setItems(list);
        tvStatus.setVisibility(View.GONE);

        calculateNextPrayer(list);
        scheduleNotifications(list);
    }

    private void scheduleNotifications(List<Prayer> list) {
        if (!PrayerPrefs.notifOn(requireContext())) return;
        String[] realPrayers = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
        for (String name : realPrayers) {
            for (Prayer p : list) {
                if (p.name.equals(name)) {
                    int[] hm = parseTime24(p.time);
                    if (hm != null) NotificationHelper.schedulePrayer(requireContext(), name, hm[0], hm[1]);
                }
            }
        }
    }

    private int[] parseTime24(String time12) {
        try {
            String t = time12.trim().split(" ")[0];
            String[] parts = t.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            if (time12.toUpperCase().contains("PM") && h != 12) h += 12;
            if (time12.toUpperCase().contains("AM") && h == 12) h = 0;
            return new int[]{h, m};
        } catch (Exception e) { return null; }
    }

    private void calculateNextPrayer(List<Prayer> list) {
        Calendar now = Calendar.getInstance();
        int nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);

        String[] realNames = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
        Prayer next = null;

        for (String name : realNames) {
            for (Prayer p : list) {
                if (p.name.equals(name)) {
                    int pMin = toMinutes24(p.time);
                    if (pMin > nowMin) { next = p; break; }
                }
            }
            if (next != null) break;
        }

        if (next == null) {
            for (Prayer p : list) if (p.name.equals("Fajr")) { next = p; break; }
        }

        nextPrayer = next;
        if (next != null) {
            tvNextPrayerName.setText(next.icon + "  " + next.name);
            tvNextPrayerTime.setText(next.time);
            updateCountdown();
        }
    }

    private void updateCountdown() {
        if (nextPrayer == null) return;
        Calendar now = Calendar.getInstance();
        int nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        int nowSec = now.get(Calendar.SECOND);

        int pMin = toMinutes24(nextPrayer.time);
        int diffSec = (pMin * 60) - (nowMin * 60 + nowSec);
        if (diffSec < 0) diffSec += 24 * 3600;

        int h = diffSec / 3600;
        int m = (diffSec % 3600) / 60;
        int s = diffSec % 60;
        tvNextPrayerCountdown.setText(String.format(Locale.getDefault(),
                "in %02dh %02dm %02ds", h, m, s));
    }

    private int toMinutes24(String time12) {
        try {
            String t = time12.trim().split(" ")[0];
            String[] parts = t.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            if (time12.toUpperCase().contains("PM") && h != 12) h += 12;
            if (time12.toUpperCase().contains("AM") && h == 12) h = 0;
            return h * 60 + m;
        } catch (Exception e) { return 0; }
    }

    private String clean(String s) {
        if (s == null) return "--:--";
        return s.split(" ")[0];
    }
}
