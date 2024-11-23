package com.example.fitnesstrackerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;

public class WalkProgressFragment extends Fragment {

    private BarChart barChartWalk;

    private int todayTotalSteps;
    private int weeklyTotalSteps;
    private int monthlyTotalSteps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk_progress, container, false);
        barChartWalk = view.findViewById(R.id.barChartWalk);

        // Replace with actual user email
        String userEmail = getUserEmail();
        displayWalkingStats(userEmail);

        return view;
    }

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", ""); // Default email if not found
    }

    private void displayWalkingStats(String email) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        // Calculate today's total steps
        todayTotalSteps = calculateTodayTotalSteps(dbHelper, email);
        weeklyTotalSteps = calculateWeeklyTotalSteps(dbHelper, email);
        monthlyTotalSteps = calculateMonthlyTotalSteps(dbHelper, email);


        // Prepare data for the bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, todayTotalSteps)); // Today
        entries.add(new BarEntry(1, weeklyTotalSteps)); // Weekly
        entries.add(new BarEntry(2, monthlyTotalSteps)); // Monthly

        BarDataSet dataSet = new BarDataSet(entries, "Total Steps");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.4f);

        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);  // Format as integer
            }
        });

        // Set Y-axis range
        configureYAxis(barChartWalk);
        barChartWalk.getAxisRight().setEnabled(false); // Disable the right Y-axis

        barChartWalk.setData(barData);
        barChartWalk.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Today", "Weekly", "Monthly"}));
        barChartWalk.getDescription().setEnabled(false);
        barChartWalk.animateY(1000);
        barChartWalk.invalidate(); // Refresh the chart

        Log.d("BarChartData", "Today's Steps: " + todayTotalSteps);
        Log.d("BarChartData", "Weekly Steps: " + weeklyTotalSteps);
        Log.d("BarChartData", "Monthly Steps: " + monthlyTotalSteps);
    }

    private int calculateTodayTotalSteps(DatabaseHelper dbHelper, String email) {
        List<WalkSession> todaySessions = dbHelper.getTodayWalkSessions(email);
        int totalSteps = 0;

        for (WalkSession session : todaySessions) {
            totalSteps += session.getTotalSteps();
        }

        Log.d("TodayWalk", "Steps: " + totalSteps); // Check the log for total steps
        return totalSteps; // Return the total steps for today
    }

    private int calculateWeeklyTotalSteps(DatabaseHelper dbHelper, String email) {
        List<WalkSession> weekSessions = dbHelper.getThisWeekWalkSessions( email);
        int totalSteps = 0;

        for (WalkSession session : weekSessions) {
            totalSteps += session.getTotalSteps();
        }

        Log.d("ThisWeekWalk", "Steps: " + totalSteps); // Check the log for total steps
        return totalSteps; // Return the total steps for today
    }

    private int calculateMonthlyTotalSteps(DatabaseHelper dbHelper, String email) {
        List<WalkSession> monthSessions = dbHelper.getThisMonthWalkSessions(email);
        int totalSteps = 0;

        for (WalkSession session : monthSessions) {
            totalSteps += session.getTotalSteps();
        }

        Log.d("ThisMonthWalk", "Steps: " + totalSteps); // Check the log for total steps
        return totalSteps; // Return the total steps for today
    }

    private void configureYAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Start from 0
        yAxis.setGranularity(1f); // Spacing

        // Optional: Set a maximum limit based on your maximum steps
        int maxSteps = Math.max(todayTotalSteps, Math.max(weeklyTotalSteps, monthlyTotalSteps));
        yAxis.setAxisMaximum(maxSteps + 100); // Add some buffer above the max steps

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });
    }
}
