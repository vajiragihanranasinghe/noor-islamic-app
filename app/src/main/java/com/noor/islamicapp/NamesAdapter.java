package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NamesAdapter extends RecyclerView.Adapter<NamesAdapter.VH> {

    private List<NameOfAllah> items = new ArrayList<>();

    public void setItems(List<NameOfAllah> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_name, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NameOfAllah n = items.get(position);
        h.tvNumber.setText(String.valueOf(n.number));
        h.tvArabic.setText(n.arabic);
        h.tvTransliteration.setText(n.transliteration);
        h.tvMeaning.setText(n.meaning);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNumber, tvArabic, tvTransliteration, tvMeaning;
        VH(@NonNull View v) {
            super(v);
            tvNumber = v.findViewById(R.id.tvNumber);
            tvArabic = v.findViewById(R.id.tvArabic);
            tvTransliteration = v.findViewById(R.id.tvTransliteration);
            tvMeaning = v.findViewById(R.id.tvMeaning);
        }
    }
}
