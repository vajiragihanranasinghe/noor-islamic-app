package com.noor.islamicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuranFragment extends Fragment {

    private RecyclerView rvSurahs;
    private EditText etSearch;
    private TextView tvStatus;
    private TextView tvContinueReading;
    private TextView tvQuranBookmarks;

    private SurahAdapter adapter;
    private List<Surah> allSurahs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_quran,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View v,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(v, savedInstanceState);

        rvSurahs = v.findViewById(R.id.rvSurahs);
        etSearch = v.findViewById(R.id.etSearch);
        tvStatus = v.findViewById(R.id.tvQuranStatus);
        tvContinueReading = v.findViewById(R.id.tvContinueReading);
        tvQuranBookmarks = v.findViewById(R.id.tvQuranBookmarks);

        adapter = new SurahAdapter(surah -> {
            openSurah(surah.id);
        });

        rvSurahs.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        rvSurahs.setAdapter(adapter);

        updateQuickActions();

        tvContinueReading.setOnClickListener(v1 -> {
            int surahId =
                    QuranProgressPrefs.getLastSurah(
                            requireContext()
                    );

            if (surahId <= 0) {
                surahId = 1;
            }

            openSurah(surahId);
        });

        tvQuranBookmarks.setOnClickListener(v1 -> {
            int[] bookmark =
                    QuranProgressPrefs.getFirstBookmark(
                            requireContext()
                    );

            if (bookmark != null
                    && bookmark.length >= 2) {

                openSurah(bookmark[0]);

            } else {

                tvQuranBookmarks.setText(
                        "⭐ No bookmarks yet — tap ☆ beside an Ayah to save it."
                );
            }
        });

        loadQuran();

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tvContinueReading != null) {
            updateQuickActions();
        }
    }

    private void openSurah(int surahId) {

        Intent intent =
                new Intent(
                        requireContext(),
                        SurahDetailActivity.class
                );

        intent.putExtra(
                "surah_id",
                surahId
        );

        startActivity(intent);
    }

    private void updateQuickActions() {

        if (tvContinueReading == null
                || tvQuranBookmarks == null) {
            return;
        }

        if (QuranProgressPrefs.hasLastRead(
                requireContext()
        )) {

            int surahId =
                    QuranProgressPrefs.getLastSurah(
                            requireContext()
                    );

            int ayahId =
                    QuranProgressPrefs.getLastAyah(
                            requireContext()
                    );

            tvContinueReading.setText(
                    "📖  Continue Reading\n"
                            + "Surah "
                            + surahId
                            + " · Ayah "
                            + ayahId
                            + "\nTap to continue where you stopped"
            );

        } else {

            tvContinueReading.setText(
                    "📖  Start Reading\n"
                            + "Begin with Surah Al-Fatihah\n"
                            + "Tap to open the Holy Quran"
            );
        }

        int bookmarkCount =
                QuranProgressPrefs.bookmarkCount(
                        requireContext()
                );

        if (bookmarkCount > 0) {

            tvQuranBookmarks.setText(
                    "⭐  Quran Bookmarks\n"
                            + bookmarkCount
                            + " saved Ayah"
                            + (bookmarkCount == 1
                            ? ""
                            : "s")
                            + "\nTap to jump to your first bookmark"
            );

        } else {

            tvQuranBookmarks.setText(
                    "⭐  Quran Bookmarks\n"
                            + "No saved Ayahs yet\n"
                            + "Tap ☆ beside an Ayah to bookmark it"
            );
        }
    }

    private void loadQuran() {

        tvStatus.setText(
                "Loading Quran…"
        );

        tvStatus.setVisibility(
                View.VISIBLE
        );

        new Thread(() -> {

            final List<Surah> loaded =
                    QuranRepository.getSurahs(
                            requireContext()
                    );

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(() -> {

                allSurahs =
                        loaded != null
                                ? loaded
                                : new ArrayList<>();

                adapter.setItems(
                        allSurahs
                );

                if (allSurahs.isEmpty()) {

                    tvStatus.setText(
                            "Failed to load Quran data"
                    );

                    tvStatus.setVisibility(
                            View.VISIBLE
                    );

                } else {

                    tvStatus.setVisibility(
                            View.GONE
                    );
                }

                updateQuickActions();
            });

        }).start();
    }

    private void filter(String query) {

        if (query == null
                || query.trim().isEmpty()) {

            adapter.setItems(
                    allSurahs
            );

            tvStatus.setVisibility(
                    allSurahs.isEmpty()
                            ? View.VISIBLE
                            : View.GONE
            );

            return;
        }

        String q =
                query.toLowerCase().trim();

        List<Surah> filtered =
                new ArrayList<>();

        for (Surah s : allSurahs) {

            boolean matchesName =
                    s.nameEn != null
                            && s.nameEn
                            .toLowerCase()
                            .contains(q);

            boolean matchesArabic =
                    s.nameAr != null
                            && s.nameAr
                            .contains(query);

            boolean matchesTranslation =
                    s.translation != null
                            && s.translation
                            .toLowerCase()
                            .contains(q);

            boolean matchesType =
                    s.getTypeDisplay() != null
                            && s.getTypeDisplay()
                            .toLowerCase()
                            .contains(q);

            boolean matchesNumber =
                    String.valueOf(s.id)
                            .equals(q);

            if (matchesName
                    || matchesArabic
                    || matchesTranslation
                    || matchesType
                    || matchesNumber) {

                filtered.add(s);
            }
        }

        adapter.setItems(
                filtered
        );

        if (filtered.isEmpty()) {

            tvStatus.setText(
                    "No matching Surahs found"
            );

            tvStatus.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvStatus.setVisibility(
                    View.GONE
            );
        }
    }
}
