package com.noor.islamicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SalahHistoryFragment extends Fragment {

    private TextView tvToday;
    private TextView tvYesterday;
    private TextView tvSevenDays;
    private TextView tvPercentage;
    private TextView tvStreak;
    private TextView tvFajr;
    private TextView tvDhuhr;
    private TextView tvAsr;
    private TextView tvMaghrib;
    private TextView tvIsha;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_salah_history,
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

        tvToday = v.findViewById(R.id.tvHistoryToday);
        tvYesterday = v.findViewById(R.id.tvHistoryYesterday);
        tvSevenDays = v.findViewById(R.id.tvHistorySevenDays);
        tvPercentage = v.findViewById(R.id.tvHistoryPercentage);
        tvStreak = v.findViewById(R.id.tvHistoryStreak);

        tvFajr = v.findViewById(R.id.tvHistoryFajr);
        tvDhuhr = v.findViewById(R.id.tvHistoryDhuhr);
        tvAsr = v.findViewById(R.id.tvHistoryAsr);
        tvMaghrib = v.findViewById(R.id.tvHistoryMaghrib);
        tvIsha = v.findViewById(R.id.tvHistoryIsha);

        updateHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvToday != null) {
            updateHistory();
        }
    }

    private void updateHistory() {

        Calendar today = Calendar.getInstance();

        int todayCount =
                SalahPrefs.completedCountForDate(
                        requireContext(),
                        dateFor(today)
                );

        Calendar yesterday =
                (Calendar) today.clone();

        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        int yesterdayCount =
                SalahPrefs.completedCountForDate(
                        requireContext(),
                        dateFor(yesterday)
                );

        int totalCompleted = 0;

        for (int i = 0; i < 7; i++) {

            Calendar day =
                    (Calendar) today.clone();

            day.add(
                    Calendar.DAY_OF_YEAR,
                    -i
            );

            totalCompleted +=
                    SalahPrefs.completedCountForDate(
                            requireContext(),
                            dateFor(day)
                    );
        }

        int percentage =
                Math.round(
                        (totalCompleted / 35f) * 100f
                );

        int streak = calculateStreak();

        tvToday.setText(
                "Today  •  " + todayCount + " / 5"
        );

        tvYesterday.setText(
                "Yesterday  •  " + yesterdayCount + " / 5"
        );

        tvSevenDays.setText(
                "Last 7 days  •  "
                        + totalCompleted
                        + " / 35"
        );

        tvPercentage.setText(
                percentage + "%"
        );

        tvStreak.setText(
                "🔥 " + streak + " day streak"
        );

        String todayDate = dateFor(today);

        setPrayerStatus(
                tvFajr,
                "Fajr",
                todayDate
        );

        setPrayerStatus(
                tvDhuhr,
                "Dhuhr",
                todayDate
        );

        setPrayerStatus(
                tvAsr,
                "Asr",
                todayDate
        );

        setPrayerStatus(
                tvMaghrib,
                "Maghrib",
                todayDate
        );

        setPrayerStatus(
                tvIsha,
                "Isha",
                todayDate
        );
    }

    private void setPrayerStatus(
            TextView view,
            String prayer,
            String date
    ) {
        boolean completed =
                SalahPrefs.isCompletedForDate(
                        requireContext(),
                        date,
                        prayer
                );

        if (completed) {
            view.setText("✓ " + prayer + "  • PRAYED");
        } else {
            view.setText("○ " + prayer + "  • NOT PRAYED");
        }
    }

    private int calculateStreak() {

        int streak = 0;

        Calendar day =
                Calendar.getInstance();

        while (true) {

            int count =
                    SalahPrefs.completedCountForDate(
                            requireContext(),
                            dateFor(day)
                    );

            if (count == 5) {
                streak++;

                day.add(
                        Calendar.DAY_OF_YEAR,
                        -1
                );

            } else {
                break;
            }
        }

        return streak;
    }

    private String dateFor(Calendar calendar) {
        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(calendar.getTime());
    }
}
