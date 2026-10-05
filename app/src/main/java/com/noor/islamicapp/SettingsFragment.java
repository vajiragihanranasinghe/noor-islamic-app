package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        prefs = requireContext().getSharedPreferences("noor_settings", Context.MODE_PRIVATE);

        Switch swDark   = v.findViewById(R.id.swDark);
        Switch swNotif  = v.findViewById(R.id.swNotif);
        Switch swAuto   = v.findViewById(R.id.swAuto);
        TextView tvMethod  = v.findViewById(R.id.tvMethodValue);
        TextView tvMadhab  = v.findViewById(R.id.tvMadhab);
        TextView tvHighLat = v.findViewById(R.id.tvHighLat);
        TextView tvExact   = v.findViewById(R.id.tvExactAlarm);
        TextView tvTest    = v.findViewById(R.id.tvTestNotif);
        TextView tvVersion = v.findViewById(R.id.tvVersion);
        TextView tvReset   = v.findViewById(R.id.tvReset);

        swDark.setChecked(prefs.getBoolean("dark_mode", false));
        swDark.setOnCheckedChangeListener((b, checked) -> {
            prefs.edit().putBoolean("dark_mode", checked).apply();
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
        });

        swNotif.setChecked(prefs.getBoolean("notifications", true));
        swNotif.setOnCheckedChangeListener((b, checked) -> {
            prefs.edit().putBoolean("notifications", checked).apply();
            Toast.makeText(requireContext(),
                    checked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();
        });

        if (swAuto != null) {
            swAuto.setChecked(prefs.getBoolean(PrayerPrefs.KEY_AUTO, true));
            swAuto.setOnCheckedChangeListener((b, checked) -> {
                prefs.edit().putBoolean(PrayerPrefs.KEY_AUTO, checked).apply();
                Toast.makeText(requireContext(),
                        checked ? "Auto method enabled" : "Manual mode",
                        Toast.LENGTH_SHORT).show();
            });
        }

        int method = prefs.getInt(PrayerPrefs.KEY_METHOD, 3);
        if (tvMethod != null) {
            tvMethod.setText(MethodResolver.describeMethod(method));
            tvMethod.setOnClickListener(x -> {
                int cur = prefs.getInt(PrayerPrefs.KEY_METHOD, 3);
                int[] methods = {1, 2, 3, 4, 5, 7, 13, 16, 17};
                int idx = 0;
                for (int i = 0; i < methods.length; i++) if (methods[i] == cur) { idx = i; break; }
                idx = (idx + 1) % methods.length;
                prefs.edit().putInt(PrayerPrefs.KEY_METHOD, methods[idx]).apply();
                tvMethod.setText(MethodResolver.describeMethod(methods[idx]));
            });
        }

        if (tvMadhab != null) {
            int school = prefs.getInt(PrayerPrefs.KEY_SCHOOL, 0);
            tvMadhab.setText(MethodResolver.describeMadhab(school));
            tvMadhab.setOnClickListener(x -> {
                int s = prefs.getInt(PrayerPrefs.KEY_SCHOOL, 0);
                s = (s == 0) ? 1 : 0;
                prefs.edit().putInt(PrayerPrefs.KEY_SCHOOL, s).apply();
                tvMadhab.setText(MethodResolver.describeMadhab(s));
            });
        }

        if (tvHighLat != null) {
            int hl = prefs.getInt(PrayerPrefs.KEY_HIGHLAT, 0);
            tvHighLat.setText(MethodResolver.describeHighLat(hl));
            tvHighLat.setOnClickListener(x -> {
                int h = prefs.getInt(PrayerPrefs.KEY_HIGHLAT, 0);
                h = (h + 1) % 4;
                prefs.edit().putInt(PrayerPrefs.KEY_HIGHLAT, h).apply();
                tvHighLat.setText(MethodResolver.describeHighLat(h));
            });
        }

        if (tvExact != null) {
            tvExact.setOnClickListener(x -> {
                if (!AlarmPermissionHelper.canScheduleExact(requireContext())) {
                    AlarmPermissionHelper.openExactAlarmSettings(requireContext());
                } else {
                    Toast.makeText(requireContext(),
                            "Exact alarms already allowed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (tvTest != null) {
            tvTest.setOnClickListener(x -> {
                NotificationHelper.createChannel(requireContext());
                NotificationHelper.scheduleTestNotification(requireContext());
                Toast.makeText(requireContext(),
                        "Test notification in 5 seconds...", Toast.LENGTH_SHORT).show();
            });
        }

        if (tvVersion != null) tvVersion.setText("Noor Islamic Companion v1.4.0");

        if (tvReset != null) {
            tvReset.setOnClickListener(x -> {
                prefs.edit().clear().apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(requireContext(), "Settings reset", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
