package com.example.fitnesstrackerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ProfileCreationActivity extends AppCompatActivity {

    private EditText nameInput, dobInput, ageInput, weightInput, heightInput;
    private RadioGroup genderGroup;
    private TextView bmiResult;
    private Button saveProfileButton;
    private DatabaseHelper databaseHelper;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        nameInput = findViewById(R.id.nameInput);
        dobInput = findViewById(R.id.dobInput);
        ageInput = findViewById(R.id.ageInput);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        genderGroup = findViewById(R.id.genderGroup);
        bmiResult = findViewById(R.id.bmiResult);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        databaseHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("email");

        // Set DatePicker for DOB
        dobInput.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        // Save profile button click listener
        saveProfileButton.setOnClickListener(v -> {
            saveProfile();
        });

        // Calculate BMI whenever weight or height changes
        weightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateBMI();
            }
            // Other overridden methods can be left empty
        });

        heightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateBMI();
            }
            // Other overridden methods can be left empty
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dobInput.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateBMI() {
        String weightStr = weightInput.getText().toString();
        String heightStr = heightInput.getText().toString();

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr) / 100; // Convert cm to meters
            float bmi = weight / (height * height);
            bmiResult.setText("BMI: " + String.format("%.2f", bmi));
        } else {
            bmiResult.setText("BMI: ");
        }
    }

    private void saveProfile() {
        String name = nameInput.getText().toString();
        String dob = dobInput.getText().toString();
        int age = Integer.parseInt(ageInput.getText().toString());
        float weight = Float.parseFloat(weightInput.getText().toString());
        float height = Float.parseFloat(heightInput.getText().toString());
        String gender = genderGroup.getCheckedRadioButtonId() == R.id.radioMale ? "Male" : "Female";

        Profile profile = new Profile(name, dob, age, weight, height, gender);
        databaseHelper.insertProfile(profile, userEmail);

        // Redirect to main page after saving profile
        Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}