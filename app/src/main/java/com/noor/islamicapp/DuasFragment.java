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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_duas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rvDuas = v.findViewById(R.id.rvDuas);
        etSearch = v.findViewById(R.id.etSearchDua);
        tvStatus = v.findViewById(R.id.tvDuasStatus);
        llCategories = v.findViewById(R.id.llCategories);

        adapter = new DuasAdapter();
        rvDuas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDuas.setAdapter(adapter);

        new Thread(() -> {
            final List<Dua> loaded = DuasRepository.getDuas(requireContext());
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                all = loaded;
                adapter.setItems(loaded);
                tvStatus.setVisibility(loaded.isEmpty() ? View.VISIBLE : View.GONE);
                if (loaded.isEmpty()) tvStatus.setText("Failed to load duas");
                buildCategoryButtons();
            });
        }).start();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void buildCategoryButtons() {
        llCategories.removeAllViews();

        List<String> cats = new ArrayList<>();
        cats.add("All");
        cats.addAll(DuasRepository.getCategories(requireContext()));

        for (String cat : cats) {
            final String c = cat;
            TextView btn = new TextView(requireContext());
            btn.setText(c);
            btn.setTextSize(13f);
            btn.setPadding(32, 16, 32, 16);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(8);
            btn.setLayoutParams(lp);

            boolean isSelected = c.equals(selectedCategory);
            btn.setBackgroundResource(isSelected
                    ? R.drawable.bg_preset_selected
                    : R.drawable.bg_preset_normal);
            btn.setTextColor(getResources().getColor(
                    isSelected ? R.color.white : R.color.primary));

            btn.setOnClickListener(x -> {
                selectedCategory = c;
                buildCategoryButtons();
                applyFilters();
            });

            llCategories.addView(btn);
        }
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        List<Dua> filtered = new ArrayList<>();

        for (Dua d : all) {
            boolean matchesCategory = selectedCategory.equals("All")
                    || d.category.equals(selectedCategory);

            boolean matchesQuery = query.isEmpty()
                    || d.title.toLowerCase().contains(query)
                    || d.transliteration.toLowerCase().contains(query)
                    || d.meaning.toLowerCase().contains(query)
                    || d.arabic.contains(query);

            if (matchesCategory && matchesQuery) filtered.add(d);
        }
        adapter.setItems(filtered);
    }
}
