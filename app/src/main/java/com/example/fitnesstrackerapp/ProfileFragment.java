package com.example.fitnesstrackerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_EMAIL = "user_email";

    private TextView userName, userEmail;
    private TextView dobTextView, ageTextView, weightTextView, heightTextView, genderTextView, bmiTextView;
    private ImageView editProfileArrow, settingsArrow, profilePic;
    private LinearLayout logout;
    private ImageView backArrow;
    private DatabaseHelper dbHelper;
    private String userEmailStr;
    private boolean isProfilePicSet = false;

    private static final int EDIT_PROFILE_REQUEST = 1; // Define request code for activity result
    private static final int PICK_IMAGE_REQUEST = 2; // Request code for picking image



    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String userEmailStr) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_EMAIL, userEmailStr);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmailStr = getArguments().getString(ARG_USER_EMAIL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        dobTextView = view.findViewById(R.id.dob);
        ageTextView = view.findViewById(R.id.age);
        weightTextView = view.findViewById(R.id.weight);
        heightTextView = view.findViewById(R.id.height);
        genderTextView = view.findViewById(R.id.gender);
        bmiTextView = view.findViewById(R.id.bmi);
        editProfileArrow = view.findViewById(R.id.arrow_edit_profile);
        settingsArrow = view.findViewById(R.id.arrow_settings);
        profilePic = view.findViewById(R.id.profilePic); // Initialize profilePic
        logout = view.findViewById(R.id.logoutLayout);
        backArrow = view.findViewById(R.id.backArrow);

        dbHelper = new DatabaseHelper(getContext());

        // Fetch and display user details
        loadProfileDetails();

        // Handle back arrow click
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear stack
            startActivity(intent);
            requireActivity().finish(); // Finish current activity if needed
        });

        // Handle Edit Profile click
        editProfileArrow.setOnClickListener(v -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("email", userEmailStr);
            startActivity(intent);
        });

        // Handle Settings click
        settingsArrow.setOnClickListener(v -> {
            // Navigate to SettingsActivity
            Toast.makeText(getActivity(), "Settings Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        // Handle Logout click
        logout.setOnClickListener(v -> {
            // Show confirmation dialog before logout
            showLogoutConfirmationDialog();
        });

        // Handle Profile Picture Click
        profilePic.setOnClickListener(v -> {
            // Open gallery to select image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Handle long click on profile picture for deletion confirmation
        profilePic.setOnLongClickListener(v -> {
            showDeleteConfirmationDialog(); // Show confirmation dialog to delete profile picture
            return true; // Indicate that the long click was handled
        });

        return view;
    }

    // Method to load profile details from the database
    private void loadProfileDetails() {
        // Check if the email is passed correctly
        if (userEmailStr == null || userEmailStr.isEmpty()) {
            Toast.makeText(getActivity(), "Email is null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get profile details using the user's email
        Profile profile = dbHelper.getProfile(userEmailStr);

        if (profile != null) {
            // Set the details on the UI
            userName.setText(profile.getName());
            userEmail.setText(userEmailStr);

            // Display additional profile details
            dobTextView.setText("DOB : " + profile.getDob());
            ageTextView.setText("Age : " + profile.getAge());
            weightTextView.setText("Weight : " + profile.getWeight() + " kg");
            heightTextView.setText("Height : " + profile.getHeight() + " cm");
            genderTextView.setText("Gender : " + profile.getGender());
            double bmi = calculateBMI(profile.getWeight(), profile.getHeight());
            bmiTextView.setText(String.format("BMI: %.2f", bmi));

            Log.d("ProfileFragment", "Fetching profile for email: " + userEmailStr);
        } else {
            // Show a message if profile not found
            Toast.makeText(getActivity(), "Profile not found", Toast.LENGTH_SHORT).show();
            // Handle case if no profile data is found (e.g., show default or empty text)
            userName.setText("Name: N/A");
            userEmail.setText("Email: N/A");
            dobTextView.setText("DOB: ");
            ageTextView.setText("Age: ");
            weightTextView.setText("Weight: ");
            heightTextView.setText("Height: ");
            genderTextView.setText("Gender: ");
            bmiTextView.setText("BMI: ");
        }
    }

    // Calculate BMI
    private double calculateBMI(double weight, double height) {
        return weight / ((height / 100) * (height / 100)); // height in meters
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload profile details to reflect any updates made
        loadProfileDetails();
    }

    // Method to show a confirmation dialog for logout
    private void showLogoutConfirmationDialog() {
        // Create an AlertDialog for logout confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        // Set positive button for "Yes"
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Handle logout
            logoutUser();
        });

        // Set negative button for "No"
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss(); // Dismiss dialog
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to handle user logout
    private void logoutUser() {
        // Get user email for deletion
        String email = userEmailStr; // Use the email stored in the fragment

        try {
            // Clear the user's session (e.g., remove email from SharedPreferences)
            SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Clear all user data
            editor.apply();

            // Navigate to LoginActivity after logout
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);

            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle any exceptions and show a toast message
            Toast.makeText(getActivity(), "Error logging out. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            // End the current activity
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                profilePic.setImageURI(imageUri); // Set the selected image to ImageView
                isProfilePicSet = true;
                // Optionally save this image URI to the database
                // dbHelper.updateProfilePic(userEmailStr, imageUri.toString());
            }
        } else if (requestCode == EDIT_PROFILE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                double bmi = data.getDoubleExtra("bmi", 0); // Get the updated BMI
                bmiTextView.setText(String.format("BMI: %.2f", bmi)); // Update the BMI TextView
            }
            loadProfileDetails(); // Optionally reload other profile details
        }
    }

    // Method to show a confirmation dialog for deleting the profile picture
    private void showDeleteConfirmationDialog() {
        if (!isProfilePicSet) {
            // Show a message indicating that there's no profile picture to delete
            Toast.makeText(getActivity(), "No profile picture to delete.", Toast.LENGTH_SHORT).show();
            return; // Exit the method as there's nothing to delete
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Profile Picture");
        builder.setMessage("Are you sure you want to delete your profile picture?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Delete the profile picture
            profilePic.setImageResource(R.drawable.baseline_person_24); // Reset to default picture
            isProfilePicSet = false;
            Toast.makeText(getActivity(), "Profile picture deleted successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss(); // Dismiss dialog
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
