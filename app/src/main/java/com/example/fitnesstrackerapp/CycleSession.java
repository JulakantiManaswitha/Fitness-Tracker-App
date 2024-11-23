package com.example.fitnesstrackerapp;

public class CycleSession {
    private String email;
    private double distance;
    private long duration;
    private double caloriesBurned;
    private  double caloriesGoal;

    public CycleSession(String email, double distance, long duration, double caloriesBurned, double caloriesGoal) {
        this.email = email;
        this.distance = distance;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.caloriesGoal = caloriesGoal;
    }

    public String getEmail() {
        return email;
    }

    public double getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public double getCaloriesGoal() {
        return caloriesGoal;
    }
}
