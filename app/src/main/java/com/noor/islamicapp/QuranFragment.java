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
    private SurahAdapter adapter;
    private List<Surah> allSurahs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quran, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rvSurahs = v.findViewById(R.id.rvSurahs);
        etSearch = v.findViewById(R.id.etSearch);
        tvStatus = v.findViewById(R.id.tvQuranStatus);

        adapter = new SurahAdapter(surah -> {
            Intent intent = new Intent(requireContext(), SurahDetailActivity.class);
            intent.putExtra("surah_id", surah.id);
            startActivity(intent);
        });

        rvSurahs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSurahs.setAdapter(adapter);

        loadQuran();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadQuran() {
        tvStatus.setText("Loading Quran…");
        tvStatus.setVisibility(View.VISIBLE);

        // Load on background thread
        new Thread(() -> {
            final List<Surah> loaded = QuranRepository.getSurahs(requireContext());
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                allSurahs = loaded;
                adapter.setItems(loaded);
                if (loaded.isEmpty()) {
                    tvStatus.setText("Failed to load Quran data");
                } else {
                    tvStatus.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setItems(allSurahs);
            return;
        }
        String q = query.toLowerCase().trim();
        List<Surah> filtered = new ArrayList<>();
        for (Surah s : allSurahs) {
            if (s.nameEn.toLowerCase().contains(q)
                    || s.nameAr.contains(query)
                    || String.valueOf(s.id).equals(q)) {
                filtered.add(s);
            }
        }
        adapter.setItems(filtered);
    }
}
