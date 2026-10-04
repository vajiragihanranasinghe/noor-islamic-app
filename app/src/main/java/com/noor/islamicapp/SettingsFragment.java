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

    private static final String PREFS = "noor_settings";
    private static final String KEY_DARK = "dark_mode";
    private static final String KEY_NOTIF = "notifications";
    private static final String KEY_METHOD = "method";

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

        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        Switch swDark = v.findViewById(R.id.swDark);
        Switch swNotif = v.findViewById(R.id.swNotif);
        TextView tvMethod = v.findViewById(R.id.tvMethodValue);
        TextView tvVersion = v.findViewById(R.id.tvVersion);
        TextView tvReset = v.findViewById(R.id.tvReset);

        swDark.setChecked(prefs.getBoolean(KEY_DARK, false));
        swNotif.setChecked(prefs.getBoolean(KEY_NOTIF, true));

        int method = prefs.getInt(KEY_METHOD, 3);
        tvMethod.setText(methodName(method));
        tvVersion.setText("Noor Islamic Companion v1.0.0");

        // Prevent toggle from firing on initial setup
        swDark.setOnCheckedChangeListener(null);
        swDark.setChecked(prefs.getBoolean(KEY_DARK, false));
        swDark.setOnCheckedChangeListener((b, checked) -> {
            prefs.edit().putBoolean(KEY_DARK, checked).apply();
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
            // Activity recreates automatically — no toast needed
        });

        swNotif.setOnCheckedChangeListener((b, checked) -> {
            prefs.edit().putBoolean(KEY_NOTIF, checked).apply();
            Toast.makeText(requireContext(),
                    checked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();
        });

        final int[] methods = {3, 2, 4, 5, 1};
        final String[] methodNames = {
                "Muslim World League", "ISNA (North America)",
                "Umm al-Qura (Makkah)", "Egyptian General Authority",
                "University of Islamic Sciences, Karachi"
        };
        tvMethod.setOnClickListener(x -> {
            int current = prefs.getInt(KEY_METHOD, 3);
            int idx = 0;
            for (int i = 0; i < methods.length; i++) {
                if (methods[i] == current) { idx = i; break; }
            }
            idx = (idx + 1) % methods.length;
            prefs.edit().putInt(KEY_METHOD, methods[idx]).apply();
            tvMethod.setText(methodNames[idx]);
            Toast.makeText(requireContext(), "Calculation: " + methodNames[idx], Toast.LENGTH_SHORT).show();
        });

        tvReset.setOnClickListener(x -> {
            prefs.edit().clear().apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(requireContext(), "Settings reset", Toast.LENGTH_SHORT).show();
        });
    }

    private String methodName(int id) {
        switch (id) {
            case 2: return "ISNA (North America)";
            case 3: return "Muslim World League";
            case 4: return "Umm al-Qura (Makkah)";
            case 5: return "Egyptian General Authority";
            case 1: return "University of Islamic Sciences, Karachi";
            default: return "Muslim World League";
        }
    }
}
