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

    public interface OnFavoriteChanged {
        void onChanged();
    }

    private final OnFavoriteChanged favoriteChanged;
    private List<Dua> items = new ArrayList<>();

    public DuasAdapter(OnFavoriteChanged favoriteChanged) {
        this.favoriteChanged = favoriteChanged;
    }

    public void setItems(List<Dua> items) {
        this.items = items != null
                ? items
                : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dua, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH h,
            int position
    ) {
        Dua d = items.get(position);

        h.tvCategory.setText(d.category);
        h.tvTitle.setText(d.title);
        h.tvArabic.setText(d.arabic);
        h.tvTranslit.setText(d.transliteration);
        h.tvMeaning.setText(d.meaning);
        h.tvRef.setText("— " + d.reference);

        updateFavoriteIcon(h, d);

        h.tvFavorite.setOnClickListener(v -> {

            FavoritesPrefs.toggleDuaFavorite(
                    v.getContext(),
                    d
            );

            updateFavoriteIcon(h, d);

            if (favoriteChanged != null) {
                favoriteChanged.onChanged();
            }
        });
    }

    private void updateFavoriteIcon(
            VH h,
            Dua d
    ) {
        boolean favorite =
                FavoritesPrefs.isDuaFavorite(
                        h.itemView.getContext(),
                        d
                );

        h.tvFavorite.setText(
                favorite ? "★" : "☆"
        );

        h.tvFavorite.setContentDescription(
                favorite
                        ? "Remove from favorites"
                        : "Add to favorites"
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvCategory;
        TextView tvTitle;
        TextView tvArabic;
        TextView tvTranslit;
        TextView tvMeaning;
        TextView tvRef;
        TextView tvFavorite;

        VH(@NonNull View v) {
            super(v);

            tvCategory =
                    v.findViewById(R.id.tvCategory);

            tvTitle =
                    v.findViewById(R.id.tvDuaTitle);

            tvArabic =
                    v.findViewById(R.id.tvDuaArabic);

            tvTranslit =
                    v.findViewById(R.id.tvDuaTranslit);

            tvMeaning =
                    v.findViewById(R.id.tvDuaMeaning);

            tvRef =
                    v.findViewById(R.id.tvDuaRef);

            tvFavorite =
                    v.findViewById(R.id.tvDuaFavorite);
        }
    }
}
