package com.example.fitnesstrackerapp;

import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText emailInput, passwordInput, confirmPasswordInput;
    Button signupButton;
    TextView loginText;
    DatabaseHelper db;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = new DatabaseHelper(this);

        signupButton = findViewById(R.id.signupButton);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        loginText = findViewById(R.id.loginText);

        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Position of the eye icon

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle password visibility
                        if (isPasswordVisible) {
                            // Hide password
                            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        } else {
                            // Show password
                            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        }
                        isPasswordVisible = !isPasswordVisible;
                        passwordInput.setSelection(passwordInput.length()); // Move cursor to the end
                        return true;
                    }
                }
                return false;
            }
        });

        confirmPasswordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Position of the eye icon

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirmPasswordInput.getRight() - confirmPasswordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle confirm password visibility
                        if (isConfirmPasswordVisible) {
                            // Hide confirm password
                            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            confirmPasswordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        } else {
                            // Show confirm password
                            confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            confirmPasswordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_password_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                        }
                        isConfirmPasswordVisible = !isConfirmPasswordVisible;
                        confirmPasswordInput.setSelection(confirmPasswordInput.length()); // Move cursor to the end
                        return true;
                    }
                }
                return false;
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (password.equals(confirmPassword)) {
                    User user = new User(email, password);
                    if (db.addUser(user)) {
                        Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Login Activity
                finish(); // Close signup activity
            }
        });
    }
}