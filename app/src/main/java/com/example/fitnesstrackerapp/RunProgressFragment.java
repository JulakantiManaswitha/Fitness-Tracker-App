package com.example.fitnesstrackerapp;

import android.content.Context;
import android.content.Intent;
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

public class RunProgressFragment extends Fragment {

    private BarChart barChartRun;

    private float todayTotalDistance;
    private float weeklyTotalDistance;
    private float monthlyTotalDistance;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_progress, container, false);
        barChartRun = view.findViewById(R.id.barChartRun);

        // Replace with actual user email
        String userEmail = getUserEmail();
        displayRunningStats(userEmail);

        return view;
    }

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", ""); // Default email if not found
    }

    private void displayRunningStats(String email) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        // Calculate today's total steps
        todayTotalDistance = calculateTodayTotalDistance(dbHelper);
        weeklyTotalDistance = calculateWeeklyTotalDistance(dbHelper);
        monthlyTotalDistance = calculateMonthlyTotalDistance(dbHelper);


        // Prepare data for the bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry((float) 0, (float) todayTotalDistance)); // Today
        entries.add(new BarEntry(1F, (float) weeklyTotalDistance)); // Weekly
        entries.add(new BarEntry(2F, (float) monthlyTotalDistance)); // Monthly

        BarDataSet dataSet = new BarDataSet(entries, "Total Distance Covered (m)");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        dataSet.setValueTextSize(10f); // Set text size for values on bars

        // Set custom ValueFormatter for displaying floats
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.format("%.2f", barEntry.getY()); // Format as float with 2 decimal places
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.4f);

        // Set Y-axis range
        configureYAxis(barChartRun);
        barChartRun.getAxisRight().setEnabled(false); // Disable the right Y-axis

        barChartRun.setData(barData);
        barChartRun.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Today", "Weekly", "Monthly"}));
        barChartRun.getDescription().setEnabled(false);
        barChartRun.animateY(1000);
        barChartRun.invalidate(); // Refresh the chart

        Log.d("BarChartData", "Today's Distance Covered: " + todayTotalDistance);
        Log.d("BarChartData", "Weekly Distance Covered: " + weeklyTotalDistance);
        Log.d("BarChartData", "Monthly Distance Covered: " + monthlyTotalDistance);
    }

    private float calculateTodayTotalDistance(DatabaseHelper dbHelper) {
        List<RunSession> todayRunSessions = dbHelper.getTodayRunSessions();
        float totalDistance = 0.0F;

        for (RunSession sessionRun : todayRunSessions) {
            totalDistance += sessionRun.getDistance();
        }

        Log.d("TodayRun", "Distance: " + totalDistance); // Check the log for total distance
        return totalDistance; // Return the total distance for today
    }

    private float calculateWeeklyTotalDistance(DatabaseHelper dbHelper) {
        List<RunSession> weekRunSessions = dbHelper.getWeeklyRunSessions();
        float totalDistance = 0.0F;

        for (RunSession sessionRun : weekRunSessions) {
            totalDistance += sessionRun.getDistance();
        }

        Log.d("ThisWeekRun", "Distance: " + totalDistance); // Check the log for total distance
        return totalDistance; // Return the total distance for week
    }

    private float calculateMonthlyTotalDistance(DatabaseHelper dbHelper) {
        List<RunSession> monthRunSessions = dbHelper.getMonthlyRunSessions();
        float totalDistance = 0.0F;

        for (RunSession sessionRun : monthRunSessions) {
            totalDistance += sessionRun.getDistance();
        }

        Log.d("ThisMonthRun", "Distance: " + totalDistance); // Check the log for total distance
        return totalDistance; // Return the total distance for month
    }

    private void configureYAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Start from 0
        yAxis.setGranularity(1f); // Spacing

        // Optional: Set a maximum limit based on your maximum distance
        double maxDistance = Math.max(todayTotalDistance, Math.max(weeklyTotalDistance, monthlyTotalDistance));
        yAxis.setAxisMaximum((float) (maxDistance + 100.0)); // Add some buffer above the max distance

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.valueOf(value);
            }
        });
    }
}
