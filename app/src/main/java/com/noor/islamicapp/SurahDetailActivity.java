package com.noor.islamicapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SurahDetailActivity extends AppCompatActivity {

    private RecyclerView rvAyahs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);

        int surahId =
                getIntent().getIntExtra(
                        "surah_id",
                        1
                );

        ImageView btnBack =
                findViewById(R.id.btnBack);

        TextView tvTitleEn =
                findViewById(R.id.tvSurahTitleEn);

        TextView tvTitleAr =
                findViewById(R.id.tvSurahTitleAr);

        TextView tvMeta =
                findViewById(R.id.tvSurahMeta);

        TextView tvBismillah =
                findViewById(R.id.tvBismillah);

        TextView tvBookmarkJump =
                findViewById(R.id.tvBookmarkJump);

        rvAyahs =
                findViewById(R.id.rvAyahs);

        btnBack.setOnClickListener(v -> finish());

        AyahAdapter ayahAdapter =
                new AyahAdapter(
                        this,
                        surahId
                );

        rvAyahs.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvAyahs.setAdapter(ayahAdapter);

        new Thread(() -> {

            final Surah surah =
                    QuranRepository.getSurah(
                            this,
                            surahId
                    );

            runOnUiThread(() -> {

                if (surah == null) {
                    tvTitleEn.setText(
                            "Surah not found"
                    );
                    return;
                }

                tvTitleEn.setText(
                        surah.id + ". " +
                        surah.nameEn
                );

                tvTitleAr.setText(
                        surah.nameAr
                );

                int bookmarkCount =
                        QuranProgressPrefs
                                .bookmarkCountForSurah(
                                        this,
                                        surahId
                                );

                tvMeta.setText(
                        surah.totalVerses +
                        " verses · " +
                        surah.getTypeDisplay() +
                        " · " +
                        bookmarkCount +
                        " bookmarks"
                );

                if (surah.id == 1
                        || surah.id == 9) {

                    tvBismillah.setVisibility(
                            View.GONE
                    );

                } else {

                    tvBismillah.setVisibility(
                            View.VISIBLE
                    );
                }

                if (surah.verses != null) {
                    ayahAdapter.setItems(
                            surah.verses
                    );

                    scrollToSavedAyah(
                            surahId
                    );
                }

                int bookmarkAyah =
                        QuranProgressPrefs
                                .getFirstBookmarkAyahForSurah(
                                        this,
                                        surahId
                                );

                if (bookmarkAyah > 0) {

                    tvBookmarkJump.setText(
                            "★ Jump to first bookmark (Ayah "
                                    + bookmarkAyah
                                    + ")"
                    );

                    tvBookmarkJump.setVisibility(
                            View.VISIBLE
                    );

                    tvBookmarkJump.setOnClickListener(
                            v -> scrollToAyah(
                                    bookmarkAyah
                            )
                    );

                } else {

                    tvBookmarkJump.setVisibility(
                            View.GONE
                    );
                }
            });

        }).start();
    }

    private void scrollToSavedAyah(
            int surahId
    ) {
        int savedSurah =
                QuranProgressPrefs.getLastSurah(
                        this
                );

        int savedAyah =
                QuranProgressPrefs.getLastAyah(
                        this
                );

        if (savedSurah != surahId
                || savedAyah <= 0) {
            return;
        }

        scrollToAyah(savedAyah);
    }

    private void scrollToAyah(
            int ayahId
    ) {
        rvAyahs.postDelayed(
                () -> rvAyahs.scrollToPosition(
                        Math.max(0, ayahId - 1)
                ),
                250
        );
    }
}
