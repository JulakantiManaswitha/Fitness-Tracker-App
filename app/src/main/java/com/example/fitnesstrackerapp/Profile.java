package com.example.fitnesstrackerapp;

public class Profile {
    private String name;
    private String dob;
    private int age;
    private double weight;
    private double height;
    private String gender;

    // Constructor
    public Profile(String name, String dob, int age, double weight, double height, String gender) {
        this.name = name;
        this.dob = dob;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
