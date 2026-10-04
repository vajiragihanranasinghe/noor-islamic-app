package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AyahAdapter extends RecyclerView.Adapter<AyahAdapter.VH> {

    private List<Ayah> items = new ArrayList<>();

    public void setItems(List<Ayah> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ayah, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Ayah a = items.get(position);
        h.tvAyahNumber.setText(String.valueOf(a.id));
        h.tvArabic.setText(a.textAr);
        h.tvEnglish.setText(a.textEn != null ? a.textEn : "");
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAyahNumber, tvArabic, tvEnglish;
        VH(@NonNull View v) {
            super(v);
            tvAyahNumber = v.findViewById(R.id.tvAyahNumber);
            tvArabic = v.findViewById(R.id.tvArabic);
            tvEnglish = v.findViewById(R.id.tvEnglish);
        }
    }
}
