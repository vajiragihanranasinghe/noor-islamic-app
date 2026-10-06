package com.noor.islamicapp;

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

public class NamesFragment extends Fragment {

    private RecyclerView rvNames;
    private EditText etSearch;
    private TextView tvStatus;
    private TextView tvFavoritesFilter;

    private NamesAdapter adapter;
    private List<NameOfAllah> all =
            new ArrayList<>();

    private boolean favoritesOnly = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_names,
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

        rvNames =
                v.findViewById(R.id.rvNames);

        etSearch =
                v.findViewById(R.id.etSearch);

        tvStatus =
                v.findViewById(R.id.tvNamesStatus);

        tvFavoritesFilter =
                v.findViewById(
                        R.id.tvFavoritesFilter
                );

        adapter =
                new NamesAdapter(
                        this::applyFilters
                );

        rvNames.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        rvNames.setAdapter(adapter);

        tvFavoritesFilter.setOnClickListener(v1 -> {

            favoritesOnly =
                    !favoritesOnly;

            updateFavoritesButton();

            applyFilters();
        });

        new Thread(() -> {

            final List<NameOfAllah> loaded =
                    NamesRepository.getNames(
                            requireContext()
                    );

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(() -> {

                all = loaded != null
                        ? loaded
                        : new ArrayList<>();

                adapter.setItems(all);

                updateFavoritesButton();

                updateStatus();

            });

        }).start();

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
                        applyFilters();
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

        if (adapter != null) {
            applyFilters();
        }
    }

    private void updateFavoritesButton() {

        int count =
                FavoritesPrefs.getNameFavoriteCount(
                        requireContext()
                );

        if (favoritesOnly) {

            tvFavoritesFilter.setText(
                    "★ Favorites (" + count + ")"
            );

            tvFavoritesFilter.setBackgroundResource(
                    R.drawable.bg_preset_selected
            );

            tvFavoritesFilter.setTextColor(
                    getResources().getColor(
                            R.color.white
                    )
            );

        } else {

            tvFavoritesFilter.setText(
                    "☆ Favorites (" + count + ")"
            );

            tvFavoritesFilter.setBackgroundResource(
                    R.drawable.bg_preset_normal
            );

            tvFavoritesFilter.setTextColor(
                    getResources().getColor(
                            R.color.primary
                    )
            );
        }
    }

    private void applyFilters() {

        if (etSearch == null
                || adapter == null) {
            return;
        }

        String q =
                etSearch
                        .getText()
                        .toString()
                        .toLowerCase()
                        .trim();

        List<NameOfAllah> filtered =
                new ArrayList<>();

        for (NameOfAllah n : all) {

            boolean favorite =
                    FavoritesPrefs.isNameFavorite(
                            requireContext(),
                            n
                    );

            if (favoritesOnly
                    && !favorite) {
                continue;
            }

            boolean matchesSearch =
                    q.isEmpty()
                            || safe(n.transliteration)
                            .toLowerCase()
                            .contains(q)
                            || safe(n.meaning)
                            .toLowerCase()
                            .contains(q)
                            || safe(n.arabic)
                            .contains(q)
                            || String.valueOf(
                            n.number
                    ).equals(q);

            if (matchesSearch) {
                filtered.add(n);
            }
        }

        adapter.setItems(filtered);

        updateStatus(filtered.size());
    }

    private String safe(String value) {
        return value == null
                ? ""
                : value;
    }

    private void updateStatus() {
        updateStatus(all.size());
    }

    private void updateStatus(int visibleCount) {

        if (all.isEmpty()) {

            tvStatus.setText(
                    "Failed to load names"
            );

            tvStatus.setVisibility(
                    View.VISIBLE
            );

        } else if (visibleCount == 0) {

            tvStatus.setText(
                    favoritesOnly
                            ? "No favorite names yet"
                            : "No matching names found"
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
