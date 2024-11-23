package com.example.fitnesstrackerapp;

public class WalkSession {
    private String email;
    private int totalSteps;
    private double caloriesBurned;
    private double distance;
    private int stepGoal;
    private long duration;

    public WalkSession(String email, int totalSteps, double caloriesBurned, double distance, int stepGoal, long duration) {
        this.email = email;
        this.totalSteps = totalSteps;
        this.caloriesBurned = caloriesBurned;
        this.distance = distance;
        this.stepGoal = stepGoal;
        this.duration = duration;
    }

    // Getters for the properties
    public String getEmail() {
        return email;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public double getDistance() {
        return distance;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public long getDuration() {
        return duration;
    }
}
