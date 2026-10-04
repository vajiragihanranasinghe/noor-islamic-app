package com.noor.islamicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SurahDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);

        int surahId = getIntent().getIntExtra("surah_id", 1);

        ImageView btnBack = findViewById(R.id.btnBack);
        TextView tvTitleEn = findViewById(R.id.tvSurahTitleEn);
        TextView tvTitleAr = findViewById(R.id.tvSurahTitleAr);
        TextView tvMeta = findViewById(R.id.tvSurahMeta);
        TextView tvBismillah = findViewById(R.id.tvBismillah);
        RecyclerView rvAyahs = findViewById(R.id.rvAyahs);

        btnBack.setOnClickListener(v -> finish());

        AyahAdapter ayahAdapter = new AyahAdapter();
        rvAyahs.setLayoutManager(new LinearLayoutManager(this));
        rvAyahs.setAdapter(ayahAdapter);

        // Load surah data on background thread
        new Thread(() -> {
            final Surah surah = QuranRepository.getSurah(this, surahId);
            runOnUiThread(() -> {
                if (surah == null) {
                    tvTitleEn.setText("Surah not found");
                    return;
                }

                tvTitleEn.setText(surah.id + ". " + surah.nameEn);
                tvTitleAr.setText(surah.nameAr);
                tvMeta.setText(surah.totalVerses + " verses · " + surah.getTypeDisplay());

                // Hide Bismillah for Surah 1 (already part of it) and Surah 9 (no Bismillah)
                if (surah.id == 1 || surah.id == 9) {
                    tvBismillah.setVisibility(View.GONE);
                } else {
                    tvBismillah.setVisibility(View.VISIBLE);
                }

                if (surah.verses != null) {
                    ayahAdapter.setItems(surah.verses);
                }
            });
        }).start();
    }
}
