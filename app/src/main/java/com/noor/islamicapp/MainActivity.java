package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply saved dark mode preference
        SharedPreferences prefs = getSharedPreferences("noor_settings", Context.MODE_PRIVATE);
        boolean dark = prefs.getBoolean("dark_mode", false);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(dark
                ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);
        loadFragment("Home");

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home)         loadFragment("Home");
            else if (id == R.id.nav_quran)   loadFragment("Quran");
            else if (id == R.id.nav_qibla)   loadFragment("Qibla");
            else if (id == R.id.nav_tasbih)  loadFragment("Tasbih");
            else if (id == R.id.nav_more)    loadFragment("More");
            return true;
        });
    }

    private void loadFragment(String title) {
        Fragment fragment;
        if ("Home".equals(title)) {
            fragment = new HomeFragment();
        } else if ("Quran".equals(title)) {
            fragment = new QuranFragment();
        } else if ("Qibla".equals(title)) {
            fragment = new QiblaFragment();
        } else if ("Tasbih".equals(title)) {
            fragment = new TasbihFragment();
        } else if ("More".equals(title)) {
            fragment = new MoreFragment();
        } else {
            fragment = PlaceholderFragment.newInstance(title);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit();
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_TITLE = "title";

        public static PlaceholderFragment newInstance(String title) {
            PlaceholderFragment f = new PlaceholderFragment();
            Bundle b = new Bundle();
            b.putString(ARG_TITLE, title);
            f.setArguments(b);
            return f;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            String title = getArguments() != null ? getArguments().getString(ARG_TITLE) : "Noor";
            TextView tv = new TextView(requireContext());
            tv.setText(title + "\n\n" + getString(R.string.coming_soon));
            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(32, 32, 32, 32);
            return tv;
        }
    }
}
