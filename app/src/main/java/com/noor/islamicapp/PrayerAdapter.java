package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.VH> {

    private List<Prayer> items;

    public PrayerAdapter(List<Prayer> items) { this.items = items; }

    public void setItems(List<Prayer> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prayer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Prayer p = items.get(position);
        h.tvIcon.setText(p.icon);
        h.tvName.setText(p.name);
        h.tvTime.setText(p.time);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvTime;
        VH(@NonNull View v) {
            super(v);
            tvIcon = v.findViewById(R.id.tvIcon);
            tvName = v.findViewById(R.id.tvName);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
