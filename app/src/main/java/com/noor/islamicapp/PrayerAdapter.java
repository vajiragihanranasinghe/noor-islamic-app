package com.noor.islamicapp;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.VH> {

    private List<Prayer> items;
    private String nextPrayerName = null;

    public PrayerAdapter(List<Prayer> items) {
        this.items = items;
    }

    public void setItems(List<Prayer> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setNextPrayer(String prayerName) {
        this.nextPrayerName = prayerName;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prayer, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH h,
            int position) {

        Prayer p = items.get(position);

        h.tvIcon.setText(p.icon);
        h.tvName.setText(p.name);
        h.tvTime.setText(p.time);

        boolean isNext =
                nextPrayerName != null &&
                nextPrayerName.equals(p.name);

        if (isNext) {

            h.tvName.setText(
                    String.format(
                            Locale.getDefault(),
                            "⭐ %s",
                            p.name
                    )
            );

            h.tvName.setTextColor(
                    ContextCompat.getColor(
                            h.itemView.getContext(),
                            R.color.primary
                    )
            );

            h.tvTime.setTextColor(
                    ContextCompat.getColor(
                            h.itemView.getContext(),
                            R.color.primary
                    )
            );

            h.tvName.setTypeface(
                    null,
                    Typeface.BOLD
            );

            h.tvTime.setTypeface(
                    null,
                    Typeface.BOLD
            );

            h.itemView.setAlpha(1.0f);

        } else {

            h.tvName.setText(p.name);

            h.tvName.setTextColor(
                    ContextCompat.getColor(
                            h.itemView.getContext(),
                            R.color.text_primary
                    )
            );

            h.tvTime.setTextColor(
                    ContextCompat.getColor(
                            h.itemView.getContext(),
                            R.color.primary
                    )
            );

            h.tvName.setTypeface(
                    null,
                    Typeface.BOLD
            );

            h.tvTime.setTypeface(
                    null,
                    Typeface.BOLD
            );

            h.itemView.setAlpha(0.92f);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvIcon;
        TextView tvName;
        TextView tvTime;

        VH(@NonNull View v) {
            super(v);

            tvIcon = v.findViewById(R.id.tvIcon);
            tvName = v.findViewById(R.id.tvName);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
