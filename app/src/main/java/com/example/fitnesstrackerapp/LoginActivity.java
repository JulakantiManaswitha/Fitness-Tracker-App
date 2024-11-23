package com.example.fitnesstrackerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView signupText;
    DatabaseHelper db;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        loginButton = findViewById(R.id.loginButton);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupText = findViewById(R.id.signupText);

        // Set up the eye icon toggle for the password field
        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                // Check if the eye icon (drawable right) is touched
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle password visibility
                        togglePasswordVisibility();
                        return true; // Consume the touch event
                    }
                }
                return false; // Allow other touch events to proceed
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (db.checkUser(email, password)) {
                    // Login successful
                    Toast.makeText(LoginActivity.this, "Login Successful!!", Toast.LENGTH_SHORT).show();
                    onLoginSuccess(email); // Pass email to the method
                } else {
                    // Login failed
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Signup Activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
        } else {
            // Show password
            passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
        }
        isPasswordVisible = !isPasswordVisible; // Toggle the flag
        passwordInput.setSelection(passwordInput.length()); // Set cursor to end
    }


    private void onLoginSuccess(String email) { // Accept email as a parameter

        // Save email to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.apply(); // Don't forget to apply the changes

        // Check if profile exists
        if (!db.isProfileCreated(email)) {
            // Redirect to profile creation page
            Intent intent = new Intent(LoginActivity.this, ProfileCreationActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
        } else {
            // Redirect to main page
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}