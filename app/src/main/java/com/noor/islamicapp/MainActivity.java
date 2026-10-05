package com.noor.islamicapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1001;

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences(
                        "noor_settings",
                        Context.MODE_PRIVATE
                );

        boolean dark =
                prefs.getBoolean("dark_mode", false);

        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                dark
                        ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                        : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.activity_main);

        NotificationHelper.createChannel(this);

        requestStartupPermissions();

        bottomNav = findViewById(R.id.bottomNav);

        loadFragment("Home");

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment("Home");

            } else if (id == R.id.nav_quran) {
                loadFragment("Quran");

            } else if (id == R.id.nav_qibla) {
                loadFragment("Qibla");

            } else if (id == R.id.nav_tasbih) {
                loadFragment("Tasbih");

            } else if (id == R.id.nav_more) {
                loadFragment("More");
            }

            return true;
        });
    }

    /**
     * Ask permissions on first installation/startup.
     *
     * Android 13+:
     * - Notifications
     * - Location
     *
     * Android 12 and below:
     * - Location only
     *
     * Android itself decides the exact permission-dialog sequence.
     */
    private void requestStartupPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        PERMISSION_REQUEST
                );

                return;
            }
        }

        requestLocationPermissionIfNeeded();
    }

    private void requestLocationPermissionIfNeeded() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == PERMISSION_REQUEST) {

            /*
             * Notification permission may have been granted.
             * Location permission may also have been granted.
             *
             * If Android did not grant location in the first
             * permission request, ask for it separately.
             */
            requestLocationPermissionIfNeeded();
        }
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

            fragment =
                    PlaceholderFragment.newInstance(title);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.contentFrame,
                        fragment
                )
                .commit();
    }

    public static class PlaceholderFragment
            extends Fragment {

        private static final String ARG_TITLE = "title";

        public static PlaceholderFragment newInstance(
                String title
        ) {

            PlaceholderFragment f =
                    new PlaceholderFragment();

            Bundle b = new Bundle();

            b.putString(
                    ARG_TITLE,
                    title
            );

            f.setArguments(b);

            return f;
        }

        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState
        ) {

            String title =
                    getArguments() != null
                            ? getArguments()
                            .getString(ARG_TITLE)
                            : "Noor";

            TextView tv =
                    new TextView(requireContext());

            tv.setText(
                    title +
                            "\n\n" +
                            getString(
                                    R.string.coming_soon
                            )
            );

            tv.setTextSize(24f);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(
                    32,
                    32,
                    32,
                    32
            );

            return tv;
        }
    }
}
