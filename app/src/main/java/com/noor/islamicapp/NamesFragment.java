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
    private NamesAdapter adapter;
    private List<NameOfAllah> all = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_names, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rvNames = v.findViewById(R.id.rvNames);
        etSearch = v.findViewById(R.id.etSearch);
        tvStatus = v.findViewById(R.id.tvNamesStatus);

        adapter = new NamesAdapter();
        rvNames.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNames.setAdapter(adapter);

        new Thread(() -> {
            final List<NameOfAllah> loaded = NamesRepository.getNames(requireContext());
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                all = loaded;
                adapter.setItems(loaded);
                tvStatus.setVisibility(loaded.isEmpty() ? View.VISIBLE : View.GONE);
                if (loaded.isEmpty()) tvStatus.setText("Failed to load names");
            });
        }).start();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String q) {
        if (q == null || q.trim().isEmpty()) {
            adapter.setItems(all);
            return;
        }
        String query = q.toLowerCase().trim();
        List<NameOfAllah> filtered = new ArrayList<>();
        for (NameOfAllah n : all) {
            if (n.transliteration.toLowerCase().contains(query)
                    || n.meaning.toLowerCase().contains(query)
                    || n.arabic.contains(q)) {
                filtered.add(n);
            }
        }
        adapter.setItems(filtered);
    }
}
