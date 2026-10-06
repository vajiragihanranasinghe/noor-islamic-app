package com.noor.islamicapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DuasFragment extends Fragment {

    private RecyclerView rvDuas;
    private EditText etSearch;
    private TextView tvStatus;
    private LinearLayout llCategories;

    private DuasAdapter adapter;
    private List<Dua> all = new ArrayList<>();

    private String selectedCategory = "All";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_duas,
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

        rvDuas =
                v.findViewById(R.id.rvDuas);

        etSearch =
                v.findViewById(R.id.etSearchDua);

        tvStatus =
                v.findViewById(R.id.tvDuasStatus);

        llCategories =
                v.findViewById(R.id.llCategories);

        adapter =
                new DuasAdapter(
                        this::applyFilters
                );

        rvDuas.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        rvDuas.setAdapter(adapter);

        new Thread(() -> {

            final List<Dua> loaded =
                    DuasRepository.getDuas(
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

                updateStatus();

                buildCategoryButtons();
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

    private void buildCategoryButtons() {

        llCategories.removeAllViews();

        List<String> cats =
                new ArrayList<>();

        cats.add("All");
        cats.add("★ Favorites");
        cats.addAll(
                DuasRepository.getCategories(
                        requireContext()
                )
        );

        for (String cat : cats) {

            final String c = cat;

            TextView btn =
                    new TextView(
                            requireContext()
                    );

            btn.setText(c);
            btn.setTextSize(13f);
            btn.setPadding(
                    32,
                    16,
                    32,
                    16
            );

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            lp.setMarginEnd(8);

            btn.setLayoutParams(lp);

            boolean selected =
                    c.equals(selectedCategory);

            btn.setBackgroundResource(
                    selected
                            ? R.drawable.bg_preset_selected
                            : R.drawable.bg_preset_normal
            );

            btn.setTextColor(
                    getResources().getColor(
                            selected
                                    ? R.color.white
                                    : R.color.primary
                    )
            );

            btn.setOnClickListener(x -> {

                selectedCategory = c;

                buildCategoryButtons();

                applyFilters();
            });

            llCategories.addView(btn);
        }
    }

    private void applyFilters() {

        if (etSearch == null
                || adapter == null) {
            return;
        }

        String query =
                etSearch
                        .getText()
                        .toString()
                        .toLowerCase()
                        .trim();

        boolean favoritesOnly =
                selectedCategory.equals(
                        "★ Favorites"
                );

        List<Dua> filtered =
                new ArrayList<>();

        for (Dua d : all) {

            boolean favorite =
                    FavoritesPrefs.isDuaFavorite(
                            requireContext(),
                            d
                    );

            boolean matchesCategory;

            if (favoritesOnly) {

                matchesCategory =
                        favorite;

            } else {

                matchesCategory =
                        selectedCategory.equals("All")
                                || d.category.equals(
                                selectedCategory
                        );
            }

            boolean matchesQuery =
                    query.isEmpty()
                            || safe(d.title)
                            .toLowerCase()
                            .contains(query)
                            || safe(d.transliteration)
                            .toLowerCase()
                            .contains(query)
                            || safe(d.meaning)
                            .toLowerCase()
                            .contains(query)
                            || safe(d.arabic)
                            .contains(query);

            if (matchesCategory
                    && matchesQuery) {

                filtered.add(d);
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
                    "Failed to load duas"
            );

            tvStatus.setVisibility(
                    View.VISIBLE
            );

        } else if (visibleCount == 0) {

            tvStatus.setText(
                    "No matching duas found"
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
