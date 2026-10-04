package com.noor.islamicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.VH> {

    public interface OnFeatureClick {
        void onClick(Feature feature);
    }

    public static class Feature {
        public final String icon;
        public final String title;
        public final String desc;
        public final String key;

        public Feature(String icon, String title, String desc, String key) {
            this.icon = icon;
            this.title = title;
            this.desc = desc;
            this.key = key;
        }
    }

    private final List<Feature> items;
    private final OnFeatureClick listener;

    public FeatureAdapter(List<Feature> items, OnFeatureClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Feature f = items.get(position);
        h.tvIcon.setText(f.icon);
        h.tvTitle.setText(f.title);
        h.tvDesc.setText(f.desc);
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(f);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvDesc;
        VH(@NonNull View v) {
            super(v);
            tvIcon = v.findViewById(R.id.tvFeatureIcon);
            tvTitle = v.findViewById(R.id.tvFeatureTitle);
            tvDesc = v.findViewById(R.id.tvFeatureDesc);
        }
    }
}
