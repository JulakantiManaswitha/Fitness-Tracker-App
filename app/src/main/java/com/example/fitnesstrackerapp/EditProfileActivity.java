package com.example.fitnesstrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextDob, editTextAge, editTextWeight, editTextHeight;
    private Spinner spinnerGender;
    private Button buttonSave, buttonCancel;
    private FloatingActionButton fabDeleteAccount;
    private TextView bmiTextView;
    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI components
        editTextName = findViewById(R.id.nameInput);
        editTextDob = findViewById(R.id.dobInput);
        editTextAge = findViewById(R.id.ageInput);
        editTextWeight = findViewById(R.id.weightInput);
        editTextHeight = findViewById(R.id.heightInput);
        spinnerGender = findViewById(R.id.spinnerGender);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        fabDeleteAccount = findViewById(R.id.fabDeleteAccount); // Floating delete button
        ImageView backArrow = findViewById(R.id.backArrow);
        bmiTextView = findViewById(R.id.bmiTextView);

        backArrow.setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        userEmail = getUserEmail(); // Retrieve the email from SharedPreferences or intent

        setupGenderSpinner(); // Populate the spinner
        loadProfileDetails(); // Load current profile details

        // Set click listeners for the buttons
        buttonSave.setOnClickListener(v -> saveProfile());
        buttonCancel.setOnClickListener(v -> cancelEdit());
        fabDeleteAccount.setOnClickListener(v -> confirmDeleteProfile());
    }

    // Method to populate the gender spinner
    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    // Method to load existing profile details
    private void loadProfileDetails() {
        Profile profile = dbHelper.getProfile(userEmail); // Get the profile from the database
        if (profile != null) {
            // Populate the fields with existing data
            editTextName.setText(profile.getName());
            editTextDob.setText(profile.getDob());
            editTextAge.setText(String.valueOf(profile.getAge()));
            editTextWeight.setText(String.valueOf(profile.getWeight()));
            editTextHeight.setText(String.valueOf(profile.getHeight()));
            // Set the selected gender in the spinner
            if (profile.getGender() != null) {
                int spinnerPosition = ((ArrayAdapter) spinnerGender.getAdapter()).getPosition(profile.getGender());
                spinnerGender.setSelection(spinnerPosition);
            }
        } else {
            Toast.makeText(this, "Profile not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Save profile updates
    private void saveProfile() {
        String name = editTextName.getText().toString().trim();
        String dob = editTextDob.getText().toString().trim();
        int age;
        double weight;
        double height;

        // Validate and parse inputs
        try {
            age = Integer.parseInt(editTextAge.getText().toString().trim());
            weight = Double.parseDouble(editTextWeight.getText().toString().trim());
            height = Double.parseDouble(editTextHeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid age, weight, and height", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = null;
        if (spinnerGender.getSelectedItem() != null) {
            gender = spinnerGender.getSelectedItem().toString(); // Get selected gender
        } else {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return; // Early return to prevent null pointer exception
        }

        // Create a Profile object and update the profile in the database
        Profile profile = new Profile(name, dob, age, weight, height, gender);
        dbHelper.updateProfile(profile, userEmail); // Update the profile in the database

        // Calculate BMI
        double bmi = calculateBMI(weight, height);
        bmiTextView.setText(String.format("BMI: %.2f", bmi));

        // Prepare the result to send back to ProfileFragment
        Intent intent = new Intent();
        intent.putExtra("bmi", bmi);
        setResult(RESULT_OK, intent); // Set the result to be received in ProfileFragment

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity
    }

    // Method to calculate BMI
    private double calculateBMI(double weight, double height) {
        return weight / ((height / 100) * (height / 100)); // height in meters
    }

    // Cancel editing and close the activity
    private void cancelEdit() {
        finish(); // Simply finish the activity
    }

    // Confirm delete profile action
    private void confirmDeleteProfile() {
        // Show confirmation dialog before deleting
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteProfile(); // Call delete profile method
                })
                .setNegativeButton("No", null) // Do nothing on "No"
                .show();
    }

    // Delete profile from the database
    private void deleteProfile() {
        // Call the deleteProfile method from DatabaseHelper
        dbHelper.deleteProfile(userEmail);
        Toast.makeText(EditProfileActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();

        // Optionally navigate to another activity or finish this one
        finish(); // Close the activity
    }

    // Method to retrieve user email from SharedPreferences or pass via intent
    private String getUserEmail() {
        // This could be from SharedPreferences or passed through intent
        return getIntent().getStringExtra("email"); // Adjust based on how you pass email
    }
}
