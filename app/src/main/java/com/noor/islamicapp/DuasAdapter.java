package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DuasAdapter extends RecyclerView.Adapter<DuasAdapter.VH> {

    private List<Dua> items = new ArrayList<>();

    public void setItems(List<Dua> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dua, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Dua d = items.get(position);
        h.tvCategory.setText(d.category);
        h.tvTitle.setText(d.title);
        h.tvArabic.setText(d.arabic);
        h.tvTranslit.setText(d.transliteration);
        h.tvMeaning.setText(d.meaning);
        h.tvRef.setText("— " + d.reference);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvArabic, tvTranslit, tvMeaning, tvRef;
        VH(@NonNull View v) {
            super(v);
            tvCategory  = v.findViewById(R.id.tvCategory);
            tvTitle     = v.findViewById(R.id.tvDuaTitle);
            tvArabic    = v.findViewById(R.id.tvDuaArabic);
            tvTranslit  = v.findViewById(R.id.tvDuaTranslit);
            tvMeaning   = v.findViewById(R.id.tvDuaMeaning);
            tvRef       = v.findViewById(R.id.tvDuaRef);
        }
    }
}
