package com.noor.islamicapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class TasbihFragment extends Fragment {

    private static final String PREFS = "tasbih_prefs";
    private static final String KEY_INDEX = "preset_index";
    private static final String KEY_COUNT = "count";
    private static final String KEY_VIBRATE = "vibrate";

    private TextView tvArabic, tvMeaning, tvCount, tvTarget;
    private ProgressBar progressBar;
    private TextView btnTap, btnReset, btnVibrate;
    private LinearLayout llPresets;

    private List<DhikrPreset> presets = new ArrayList<>();
    private int selectedIndex = 0;
    private int count = 0;
    private boolean vibrateEnabled = true;

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasbih, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tvArabic = v.findViewById(R.id.tvDhikrArabic);
        tvMeaning = v.findViewById(R.id.tvDhikrMeaning);
        tvCount = v.findViewById(R.id.tvCount);
        tvTarget = v.findViewById(R.id.tvTarget);
        progressBar = v.findViewById(R.id.progressBar);
        btnTap = v.findViewById(R.id.btnTap);
        btnReset = v.findViewById(R.id.btnReset);
        btnVibrate = v.findViewById(R.id.btnVibrate);
        llPresets = v.findViewById(R.id.llPresets);

        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        selectedIndex = prefs.getInt(KEY_INDEX, 0);
        count = prefs.getInt(KEY_COUNT, 0);
        vibrateEnabled = prefs.getBoolean(KEY_VIBRATE, true);

        presets.add(new DhikrPreset("سُبْحَانَ اللَّهِ", "SubhanAllah", "Glory be to Allah", 33));
        presets.add(new DhikrPreset("الْحَمْدُ لِلَّهِ", "Alhamdulillah", "All praise is for Allah", 33));
        presets.add(new DhikrPreset("اللَّهُ أَكْبَرُ", "Allahu Akbar", "Allah is the Greatest", 34));
        presets.add(new DhikrPreset("لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "There is no god but Allah", 100));
        presets.add(new DhikrPreset("أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "I seek forgiveness from Allah", 100));
        presets.add(new DhikrPreset("صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ", "Salawat", "Peace be upon him (PBUH)", 100));

        buildPresetButtons();
        updateUI();

        btnTap.setOnClickListener(x -> {
            count++;
            vibrate();
            prefs.edit().putInt(KEY_COUNT, count).apply();
            updateUI();
        });

        btnReset.setOnClickListener(x -> {
            count = 0;
            prefs.edit().putInt(KEY_COUNT, count).apply();
            updateUI();
        });

        btnVibrate.setOnClickListener(x -> {
            vibrateEnabled = !vibrateEnabled;
            prefs.edit().putBoolean(KEY_VIBRATE, vibrateEnabled).apply();
            updateVibrateButton();
        });

        updateVibrateButton();
    }

    private void buildPresetButtons() {
        llPresets.removeAllViews();
        for (int i = 0; i < presets.size(); i++) {
            final int idx = i;
            TextView btn = new TextView(requireContext());
            btn.setText(presets.get(i).transliteration);
            btn.setTextSize(13f);
            btn.setPadding(32, 16, 32, 16);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(8);
            btn.setLayoutParams(lp);

            btn.setBackgroundResource(i == selectedIndex
                    ? R.drawable.bg_preset_selected
                    : R.drawable.bg_preset_normal);
            btn.setTextColor(getResources().getColor(
                    i == selectedIndex ? R.color.white : R.color.primary));

            btn.setOnClickListener(x -> {
                selectedIndex = idx;
                count = 0;
                prefs.edit()
                        .putInt(KEY_INDEX, selectedIndex)
                        .putInt(KEY_COUNT, count)
                        .apply();
                buildPresetButtons();
                updateUI();
            });

            llPresets.addView(btn);
        }
    }

    private void updateUI() {
        DhikrPreset p = presets.get(selectedIndex);
        tvArabic.setText(p.arabic);
        tvMeaning.setText(p.meaning);
        tvCount.setText(String.valueOf(count));
        tvTarget.setText(" / " + p.target);
        progressBar.setMax(p.target);
        progressBar.setProgress(Math.min(count, p.target));
    }

    private void updateVibrateButton() {
        btnVibrate.setText(vibrateEnabled ? "Vibrate: ON" : "Vibrate: OFF");
    }

    private void vibrate() {
        if (!vibrateEnabled) return;
        Vibrator vib = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vib == null || !vib.hasVibrator()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vib.vibrate(50);
        }
    }
}
