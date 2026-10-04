package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.VH> {

    public static class Month {
        public final int number;
        public final String name;
        public final String arabic;
        public final String meaning;

        public Month(int number, String name, String arabic, String meaning) {
            this.number = number;
            this.name = name;
            this.arabic = arabic;
            this.meaning = meaning;
        }
    }

    private final List<Month> items;

    public MonthAdapter(List<Month> items) { this.items = items; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_month, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Month m = items.get(position);
        h.tvNumber.setText(String.valueOf(m.number));
        h.tvName.setText(m.name);
        h.tvArabic.setText(m.arabic);
        h.tvMeaning.setText(m.meaning);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNumber, tvName, tvArabic, tvMeaning;
        VH(@NonNull View v) {
            super(v);
            tvNumber = v.findViewById(R.id.tvMonthNumber);
            tvName = v.findViewById(R.id.tvMonthName);
            tvArabic = v.findViewById(R.id.tvMonthArabic);
            tvMeaning = v.findViewById(R.id.tvMonthMeaning);
        }
    }
}
