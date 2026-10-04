package com.noor.islamicapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationHelper {

    public interface LocationCallbackListener {
        void onLocationReceived(double lat, double lon, String cityName);
        void onLocationFailed(String reason);
    }

    private final Context context;
    private final FusedLocationProviderClient fusedClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LocationHelper(Context context) {
        this.context = context;
        this.fusedClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("MissingPermission")
    public void getCurrentLocation(LocationCallbackListener listener) {
        if (!hasPermission()) {
            listener.onLocationFailed("Permission not granted");
            return;
        }

        // Try last known first
        fusedClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                reverseGeocode(location.getLatitude(), location.getLongitude(), listener);
            } else {
                requestFreshLocation(listener);
            }
        }).addOnFailureListener(e -> requestFreshLocation(listener));
    }

    @SuppressWarnings("MissingPermission")
    private void requestFreshLocation(LocationCallbackListener listener) {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
                .setMaxUpdates(1)
                .build();

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                fusedClient.removeLocationUpdates(this);
                Location loc = result.getLastLocation();
                if (loc != null) {
                    reverseGeocode(loc.getLatitude(), loc.getLongitude(), listener);
                } else {
                    listener.onLocationFailed("No location received");
                }
            }
        };

        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper());
    }

    private void reverseGeocode(double lat, double lon, LocationCallbackListener listener) {
        executor.execute(() -> {
            String city = "Your location";
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address a = addresses.get(0);
                    String locality = a.getLocality();
                    String country  = a.getCountryName();
                    if (locality != null && country != null) city = locality + ", " + country;
                    else if (country != null) city = country;
                }
            } catch (Exception ignored) { }

            final String finalCity = city;
            ((android.app.Activity) context).runOnUiThread(() ->
                    listener.onLocationReceived(lat, lon, finalCity));
        });
    }
}
