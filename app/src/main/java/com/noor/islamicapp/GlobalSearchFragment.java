package com.noor.islamicapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GlobalSearchFragment extends Fragment {

    private EditText etSearch;
    private TextView tvStatus;
    private RecyclerView rvResults;

    private final List<Result> allResults = new ArrayList<>();
    private SearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_global_search,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View v,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(v, savedInstanceState);

        etSearch = v.findViewById(R.id.etGlobalSearch);
        tvStatus = v.findViewById(R.id.tvGlobalSearchStatus);
        rvResults = v.findViewById(R.id.rvGlobalSearchResults);

        adapter = new SearchAdapter(
                result -> openResult(result)
        );

        rvResults.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        rvResults.setAdapter(adapter);

        loadAllContent();

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {
                        filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );
    }

    private void loadAllContent() {

        tvStatus.setText("Loading Noor content…");
        tvStatus.setVisibility(View.VISIBLE);

        new Thread(() -> {

            List<Surah> surahs =
                    QuranRepository.getSurahs(
                            requireContext()
                    );

            List<Dua> duas =
                    DuasRepository.getDuas(
                            requireContext()
                    );

            List<NameOfAllah> names =
                    NamesRepository.getNames(
                            requireContext()
                    );

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(() -> {

                allResults.clear();

                if (surahs != null) {
                    for (Surah s : surahs) {

                        allResults.add(
                                Result.quran(
                                        s.id,
                                        safe(s.nameEn),
                                        safe(s.nameAr),
                                        safe(s.translation),
                                        s.getTypeDisplay()
                                )
                        );
                    }
                }

                if (duas != null) {
                    for (Dua d : duas) {

                        allResults.add(
                                Result.dua(
                                        d,
                                        safe(d.title),
                                        safe(d.transliteration),
                                        safe(d.meaning)
                                )
                        );
                    }
                }

                if (names != null) {
                    for (NameOfAllah n : names) {

                        allResults.add(
                                Result.name(
                                        n,
                                        String.valueOf(n.number),
                                        safe(n.transliteration),
                                        safe(n.meaning)
                                )
                        );
                    }
                }

                filter(
                        etSearch.getText()
                                .toString()
                );
            });

        }).start();
    }

    private void filter(String text) {

        String query =
                text == null
                        ? ""
                        : text.toLowerCase(
                                Locale.ROOT
                        ).trim();

        List<Result> filtered =
                new ArrayList<>();

        if (query.isEmpty()) {

            adapter.setItems(filtered);

            tvStatus.setText(
                    "Search Quran, Duas and 99 Names"
            );

            tvStatus.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        for (Result r : allResults) {

            if (r.matches(query)) {
                filtered.add(r);
            }
        }

        adapter.setItems(filtered);

        if (filtered.isEmpty()) {

            tvStatus.setText(
                    "No matching content found"
            );

            tvStatus.setVisibility(
                    View.VISIBLE
            );

        } else {

            tvStatus.setText(
                    filtered.size()
                            + " result"
                            + (filtered.size() == 1
                            ? ""
                            : "s")
            );

            tvStatus.setVisibility(
                    View.VISIBLE
            );
        }
    }

    private void openResult(Result r) {

        if (r.type == Result.TYPE_QURAN) {

            androidx.fragment.app.FragmentTransaction ft =
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();

            ft.replace(
                    R.id.contentFrame,
                    new QuranFragment()
            );

            ft.addToBackStack(null);
            ft.commit();

        } else if (r.type == Result.TYPE_DUA) {

            androidx.fragment.app.FragmentTransaction ft =
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();

            ft.replace(
                    R.id.contentFrame,
                    new DuasFragment()
            );

            ft.addToBackStack(null);
            ft.commit();

        } else if (r.type == Result.TYPE_NAME) {

            androidx.fragment.app.FragmentTransaction ft =
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();

            ft.replace(
                    R.id.contentFrame,
                    new NamesFragment()
            );

            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static class Result {

        static final int TYPE_QURAN = 1;
        static final int TYPE_DUA = 2;
        static final int TYPE_NAME = 3;

        int type;
        int number;

        String title;
        String subtitle;
        String detail;
        String extra;

        Result() {
        }

        static Result quran(
                int number,
                String title,
                String arabic,
                String translation,
                String type
        ) {
            Result r = new Result();

            r.type = TYPE_QURAN;
            r.number = number;
            r.title = number + ". " + title;
            r.subtitle = arabic;
            r.detail = translation;
            r.extra = type;

            return r;
        }

        static Result dua(
                Dua dua,
                String title,
                String transliteration,
                String meaning
        ) {
            Result r = new Result();

            r.type = TYPE_DUA;
            r.title = title;
            r.subtitle = transliteration;
            r.detail = meaning;
            r.extra = safeStatic(dua.category);

            return r;
        }

        static Result name(
                NameOfAllah name,
                String number,
                String transliteration,
                String meaning
        ) {
            Result r = new Result();

            r.type = TYPE_NAME;
            r.number = name.number;
            r.title = number + ". " + transliteration;
            r.subtitle = safeStatic(name.arabic);
            r.detail = meaning;
            r.extra = "99 Names";

            return r;
        }

        boolean matches(String query) {

            return contains(title, query)
                    || contains(subtitle, query)
                    || contains(detail, query)
                    || contains(extra, query)
                    || String.valueOf(number)
                    .equals(query);
        }

        private static boolean contains(
                String value,
                String query
        ) {
            return value != null
                    && value.toLowerCase(
                    Locale.ROOT
            ).contains(query);
        }

        private static String safeStatic(
                String value
        ) {
            return value == null ? "" : value;
        }
    }

    private static class SearchAdapter
            extends RecyclerView.Adapter<SearchAdapter.VH> {

        interface OnClick {
            void onClick(Result result);
        }

        private final OnClick onClick;
        private List<Result> items =
                new ArrayList<>();

        SearchAdapter(OnClick onClick) {
            this.onClick = onClick;
        }

        void setItems(List<Result> items) {
            this.items =
                    items != null
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
            View v =
                    LayoutInflater.from(
                            parent.getContext()
                    ).inflate(
                            R.layout.item_global_search,
                            parent,
                            false
                    );

            return new VH(v);
        }

        @Override
        public void onBindViewHolder(
                @NonNull VH h,
                int position
        ) {
            Result r = items.get(position);

            h.tvType.setText(
                    r.type == Result.TYPE_QURAN
                            ? "📖 Quran"
                            : r.type == Result.TYPE_DUA
                            ? "🤲 Dua"
                            : "⭐ 99 Names"
            );

            h.tvTitle.setText(r.title);
            h.tvSubtitle.setText(r.subtitle);
            h.tvDetail.setText(r.detail);

            h.itemView.setOnClickListener(
                    v -> onClick.onClick(r)
            );
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH
                extends RecyclerView.ViewHolder {

            TextView tvType;
            TextView tvTitle;
            TextView tvSubtitle;
            TextView tvDetail;

            VH(@NonNull View v) {
                super(v);

                tvType =
                        v.findViewById(
                                R.id.tvSearchType
                        );

                tvTitle =
                        v.findViewById(
                                R.id.tvSearchTitle
                        );

                tvSubtitle =
                        v.findViewById(
                                R.id.tvSearchSubtitle
                        );

                tvDetail =
                        v.findViewById(
                                R.id.tvSearchDetail
                        );
            }
        }
    }
}
