package com.example.fitnesstrackerapp;

public class RunSession {
        private String email;
        private double caloriesBurned;
        private double distance;
        private long duration; // in seconds
        private double distanceGoal;

    public RunSession(String email, double caloriesBurned, double distance, long duration, double distanceGoal) {
        this.email = email;
        this.caloriesBurned = caloriesBurned;
        this.distance = distance;
        this.duration = duration;
        this.distanceGoal = distanceGoal;
    }

    public String getEmail() {
        return email;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public double getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public double getDistanceGoal() {
        return distanceGoal;
    }
}
