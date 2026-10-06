package com.noor.islamicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AyahAdapter extends RecyclerView.Adapter<AyahAdapter.VH> {

    private final Context context;
    private final int surahId;
    private List<Ayah> items = new ArrayList<>();

    public AyahAdapter(Context context, int surahId) {
        this.context = context;
        this.surahId = surahId;
    }

    public void setItems(List<Ayah> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ayah, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH h,
            int position
    ) {
        Ayah a = items.get(position);

        h.tvAyahNumber.setText(String.valueOf(a.id));
        h.tvArabic.setText(a.textAr);
        h.tvEnglish.setText(
                a.textEn != null ? a.textEn : ""
        );

        updateBookmarkIcon(h, a);

        h.itemView.setOnClickListener(v -> {
            QuranProgressPrefs.setLastRead(
                    context,
                    surahId,
                    a.id
            );
        });

        h.tvAyahBookmark.setOnClickListener(v -> {
            QuranProgressPrefs.toggleBookmark(
                    context,
                    surahId,
                    a.id
            );

            updateBookmarkIcon(h, a);
        });
    }

    private void updateBookmarkIcon(
            VH h,
            Ayah a
    ) {
        boolean bookmarked =
                QuranProgressPrefs.isBookmarked(
                        context,
                        surahId,
                        a.id
                );

        h.tvAyahBookmark.setText(
                bookmarked ? "★" : "☆"
        );

        h.tvAyahBookmark.setContentDescription(
                bookmarked
                        ? "Remove bookmark"
                        : "Add bookmark"
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvAyahNumber;
        TextView tvArabic;
        TextView tvEnglish;
        TextView tvAyahBookmark;

        VH(@NonNull View v) {
            super(v);

            tvAyahNumber =
                    v.findViewById(R.id.tvAyahNumber);

            tvArabic =
                    v.findViewById(R.id.tvArabic);

            tvEnglish =
                    v.findViewById(R.id.tvEnglish);

            tvAyahBookmark =
                    v.findViewById(R.id.tvAyahBookmark);
        }
    }
}
