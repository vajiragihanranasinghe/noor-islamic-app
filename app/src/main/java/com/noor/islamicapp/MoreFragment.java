package com.noor.islamicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        RecyclerView rv = v.findViewById(R.id.rvFeatures);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        List<FeatureAdapter.Feature> features = new ArrayList<>();
        features.add(new FeatureAdapter.Feature("📖", "99 Names",  "Asma ul-Husna",      "names"));
        features.add(new FeatureAdapter.Feature("🤲", "Duas",      "40+ supplications",  "duas"));
        features.add(new FeatureAdapter.Feature("📅", "Hijri",     "Islamic calendar",   "hijri"));
        features.add(new FeatureAdapter.Feature("⚙️", "Settings",  "Customize Noor",     "settings"));
        features.add(new FeatureAdapter.Feature("🕌", "Quran",     "114 surahs",         "quran"));
        features.add(new FeatureAdapter.Feature("🧭", "Qibla",     "Direction to Kaaba", "qibla"));
        features.add(new FeatureAdapter.Feature("📊", "Salah History", "Prayer progress", "salah_history"));
        features.add(new FeatureAdapter.Feature("📿", "Tasbih", "Digital dhikr counter", "tasbih"));
        features.add(new FeatureAdapter.Feature("📤", "Share",     "Share this app",     "share"));
        features.add(new FeatureAdapter.Feature("⭐", "Rate Us",   "Support the app",    "rate"));

        rv.setAdapter(new FeatureAdapter(features, f -> handleClick(f.key)));
    }

    private void handleClick(String key) {
        if (getActivity() == null) return;

        if ("names".equals(key))         replaceSelf(new NamesFragment());
        else if ("duas".equals(key))     replaceSelf(new DuasFragment());
        else if ("hijri".equals(key))    replaceSelf(new HijriFragment());
        else if ("settings".equals(key)) replaceSelf(new SettingsFragment());
        else if ("quran".equals(key))    replaceSelf(new QuranFragment());
        else if ("qibla".equals(key))    replaceSelf(new QiblaFragment());
        else if ("salah_history".equals(key)) replaceSelf(new SalahHistoryFragment());
        else if ("tasbih".equals(key))   replaceSelf(new TasbihFragment());
        else if ("share".equals(key))    shareApp();
        else if ("rate".equals(key))     rateApp();
    }

    private void shareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,
                "Check out Noor — Islamic Companion: Quran, Prayer Times, Qibla & more.");
        startActivity(Intent.createChooser(i, "Share Noor via"));
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    android.net.Uri.parse("market://details?id=" + requireContext().getPackageName())));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
        }
    }

    private void replaceSelf(Fragment fragment) {
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrame, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
