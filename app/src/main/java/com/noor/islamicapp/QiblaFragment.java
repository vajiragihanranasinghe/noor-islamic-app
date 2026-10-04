package com.noor.islamicapp;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class QiblaFragment extends Fragment implements SensorEventListener {

    private ImageView ivCompass, ivQiblaNeedle, ivKaabaIcon;
    private TextView tvHeading, tvQiblaDir, tvDistance, tvStatus, tvAligned;

    private SensorManager sensorManager;
    private Sensor rotationSensor;

    private float currentAzimuth = 0f;
    private float qiblaBearing = 0f;
    private double userLat = 0, userLon = 0;
    private boolean hasLocation = false;

    private LocationHelper locationHelper;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Boolean.TRUE.equals(fine)) {
                    getLocation();
                } else {
                    tvStatus.setText("Location permission denied. Using default location.");
                    useFallbackLocation();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qibla, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        ivCompass      = v.findViewById(R.id.ivCompass);
        ivQiblaNeedle  = v.findViewById(R.id.ivQiblaNeedle);
        ivKaabaIcon    = v.findViewById(R.id.ivKaabaIcon);
        tvHeading      = v.findViewById(R.id.tvHeading);
        tvQiblaDir     = v.findViewById(R.id.tvQiblaDir);
        tvDistance     = v.findViewById(R.id.tvDistance);
        tvStatus       = v.findViewById(R.id.tvStatus);
        tvAligned      = v.findViewById(R.id.tvAligned);

        tvAligned.setVisibility(View.GONE);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        locationHelper = new LocationHelper(requireActivity());

        // Fallback for phones without compass
        if (rotationSensor == null) {
            tvStatus.setText("⚠️ This device has no compass sensor");
            tvQiblaDir.setText(String.format(Locale.getDefault(),
                    "Qibla direction: %.1f° from North", qiblaBearing));
        }

        if (locationHelper.hasPermission()) {
            getLocation();
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getLocation() {
        tvStatus.setText("Detecting location…");
        locationHelper.getCurrentLocation(new LocationHelper.LocationCallbackListener() {
            @Override
            public void onLocationReceived(double lat, double lon, String cityName) {
                if (!isAdded()) return;
                userLat = lat;
                userLon = lon;
                hasLocation = true;
                updateQiblaData(cityName);
            }

            @Override
            public void onLocationFailed(String reason) {
                if (!isAdded()) return;
                useFallbackLocation();
            }
        });
    }

    private void useFallbackLocation() {
        // Colombo default
        userLat = 6.9271;
        userLon = 79.8612;
        hasLocation = true;
        updateQiblaData("Colombo (default)");
    }

    private void updateQiblaData(String cityName) {
        qiblaBearing = (float) QiblaCalculator.getQiblaBearing(userLat, userLon);
        double distKm = QiblaCalculator.getDistanceToKaabaKm(userLat, userLon);

        tvStatus.setText("📍 " + cityName);
        tvQiblaDir.setText(String.format(Locale.getDefault(),
                "Qibla: %.1f° from North", qiblaBearing));

        if (distKm >= 1000) {
            tvDistance.setText(String.format(Locale.getDefault(),
                    "🕋 %.0f km to Makkah", distKm));
        } else {
            tvDistance.setText(String.format(Locale.getDefault(),
                    "🕋 %.0f km to Makkah", distKm));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;

        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        float azimuthDeg = (float) Math.toDegrees(orientation[0]);
        azimuthDeg = QiblaCalculator.normalize(azimuthDeg);

        // Rotate compass so that N always points to real North
        float rotate = -azimuthDeg;

        RotateAnimation compassAnim = new RotateAnimation(
                currentAzimuth, rotate,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        compassAnim.setDuration(150);
        compassAnim.setFillAfter(true);
        ivCompass.startAnimation(compassAnim);

        // Qibla needle rotation: relative to device's current heading
        float qiblaRelative = QiblaCalculator.normalize(qiblaBearing - azimuthDeg);
        RotateAnimation qiblaAnim = new RotateAnimation(
                currentQiblaRotation, qiblaRelative,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        qiblaAnim.setDuration(150);
        qiblaAnim.setFillAfter(true);
        ivQiblaNeedle.startAnimation(qiblaAnim);

        currentQiblaRotation = qiblaRelative;

        // Update heading text
        tvHeading.setText(String.format(Locale.getDefault(), "%.0f°", azimuthDeg));

        // Check if aligned with Qibla (within 5 degrees)
        float diff = Math.abs(QiblaCalculator.normalize(azimuthDeg - qiblaBearing));
        if (diff > 180) diff = 360 - diff;
        if (diff <= 5f) {
            tvAligned.setVisibility(View.VISIBLE);
            tvAligned.setText("✅  Aligned with Qibla");
        } else {
            tvAligned.setVisibility(View.GONE);
        }

        currentAzimuth = rotate;
    }

    private float currentQiblaRotation = 0f;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
