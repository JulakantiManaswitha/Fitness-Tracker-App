package com.example.fitnesstrackerapp;

public class ActivityRecord {
    private String date; // e.g., "2024-10-22"
    private int steps;
    private double distance; // in kilometers or miles
    private double calories;

    public ActivityRecord(String date, int steps, double distance, double calories) {
        this.date = date;
        this.steps = steps;
        this.distance = distance;
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public int getSteps() {
        return steps;
    }

    public double getDistance() {
        return distance;
    }

    public double getCalories() {
        return calories;
    }
}

