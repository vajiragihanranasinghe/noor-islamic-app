package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.VH> {

    public interface OnSurahClick {
        void onSurahClick(Surah surah);
    }

    private List<Surah> items = new ArrayList<>();
    private final OnSurahClick listener;

    public SurahAdapter(OnSurahClick listener) {
        this.listener = listener;
    }

    public void setItems(List<Surah> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_surah, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Surah s = items.get(position);

        h.tvNumber.setText(String.valueOf(s.id));
        h.tvNameEn.setText(s.nameEn);
        h.tvNameAr.setText(s.nameAr);

        String meta = s.totalVerses + " verses · " + s.getTypeDisplay();
        if (s.translation != null && !s.translation.isEmpty()) {
            meta = s.translation + " · " + meta;
        }
        h.tvMeta.setText(meta);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSurahClick(s);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNumber, tvNameEn, tvNameAr, tvMeta;
        VH(@NonNull View v) {
            super(v);
            tvNumber = v.findViewById(R.id.tvNumber);
            tvNameEn = v.findViewById(R.id.tvNameEn);
            tvNameAr = v.findViewById(R.id.tvNameAr);
            tvMeta = v.findViewById(R.id.tvMeta);
        }
    }
}
