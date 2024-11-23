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

public class CycleProgressFragment extends Fragment {

    private BarChart barChartCycle;

    private float todayTotalCalories;
    private float weeklyTotalCalories;
    private float monthlyTotalCalories;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cycle_progress, container, false);
        barChartCycle = view.findViewById(R.id.barChartCycle);

        // Replace with actual user email
        String userEmail = getUserEmail();
        displayCyclingStats(userEmail);

        return view;
    }

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", ""); // Default email if not found
    }

    private void displayCyclingStats(String email) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        // Calculate today's total steps
        todayTotalCalories = calculateTodayTotalCalories(dbHelper);
        weeklyTotalCalories = calculateWeeklyTotalCalories(dbHelper);
        monthlyTotalCalories = calculateMonthlyTotalCalories(dbHelper);


        // Prepare data for the bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry((float) 0, (float) todayTotalCalories)); // Today
        entries.add(new BarEntry(1F, (float) weeklyTotalCalories)); // Weekly
        entries.add(new BarEntry(2F, (float) monthlyTotalCalories)); // Monthly

        BarDataSet dataSet = new BarDataSet(entries, "Total Calories Burned (cals)");
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
        configureYAxis(barChartCycle);
        barChartCycle.getAxisRight().setEnabled(false); // Disable the right Y-axis

        barChartCycle.setData(barData);
        barChartCycle.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Today", "Weekly", "Monthly"}));
        barChartCycle.getDescription().setEnabled(false);
        barChartCycle.animateY(1000);
        barChartCycle.invalidate(); // Refresh the chart

        Log.d("BarChartData", "Today's Calories Burned: " + todayTotalCalories);
        Log.d("BarChartData", "Weekly Calories Burned: " + weeklyTotalCalories);
        Log.d("BarChartData", "Monthly Calories Burned: " + monthlyTotalCalories);
    }

    private float calculateTodayTotalCalories(DatabaseHelper dbHelper) {
        List<CycleSession> todayCycleSessions = dbHelper.getTodayCycleSessions();
        float totalCalories = 0.0F;

        for (CycleSession sessionCycle : todayCycleSessions) {
            totalCalories += sessionCycle.getCaloriesBurned();
        }

        Log.d("TodayCycle", "Calories Burned: " + totalCalories); // Check the log for total calories
        return totalCalories; // Return the total calories for today
    }

    private float calculateWeeklyTotalCalories(DatabaseHelper dbHelper) {
        List<CycleSession> weekCycleSessions = dbHelper.getWeeklyCycleSessions();
        float totalCalories = 0.0F;

        for (CycleSession sessionCycle : weekCycleSessions) {
            totalCalories += sessionCycle.getCaloriesBurned();
        }

        Log.d("ThisWeekCycle", "Calories Burned: " + totalCalories); // Check the log for total calories
        return totalCalories; // Return the total calories for week
    }

    private float calculateMonthlyTotalCalories(DatabaseHelper dbHelper) {
        List<CycleSession> monthCycleSessions = dbHelper.getMonthlyCycleSessions();
        float totalCalories = 0.0F;

        for (CycleSession sessionCycle : monthCycleSessions) {
            totalCalories += sessionCycle.getCaloriesBurned();
        }

        Log.d("ThisMonthCycle", "Calories Burned: " + totalCalories); // Check the log for total calories
        return totalCalories; // Return the total calories for month
    }

    private void configureYAxis(BarChart barChart) {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Start from 0
        yAxis.setGranularity(1f); // Spacing

        // Optional: Set a maximum limit based on your maximum distance
        double maxCalories = Math.max(todayTotalCalories, Math.max(weeklyTotalCalories, monthlyTotalCalories));
        yAxis.setAxisMaximum((float) (maxCalories + 100.0)); // Add some buffer above the max calories

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return String.valueOf(value);
            }
        });
    }
}
