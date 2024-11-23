package com.example.fitnesstrackerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;

public class ExerciseDetailFragment extends Fragment {

    private TextView exerciseNameTV, caloriesTV, timeTV, descTV;
    private LottieAnimationView exerciseLAV;
    private ImageView backArrowIV;

    // Method to create a new instance of ExerciseDetailFragment with necessary arguments
    public static ExerciseDetailFragment newInstance(String exerciseName, String imgUrl, int time, int calories, String desc) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString("exerciseName", exerciseName);
        args.putString("imgUrl", imgUrl);
        args.putInt("calories", calories);
        args.putInt("time", time);
        args.putString("desc", desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);

        // Initialize views
        exerciseNameTV = view.findViewById(R.id.idTVExerciseName);
        caloriesTV = view.findViewById(R.id.idTVCalories);
        timeTV = view.findViewById(R.id.idTVTime);
        descTV = view.findViewById(R.id.idTVDescription);
        exerciseLAV = view.findViewById(R.id.idExerciseLAV);
        backArrowIV = view.findViewById(R.id.idIVArrowLeft);

        // Retrieve and display data passed via Bundle
        if (getArguments() != null) {
            String exerciseName = getArguments().getString("exerciseName");
            String imgUrl = getArguments().getString("imgUrl");
            int calories = getArguments().getInt("calories", 0);
            int time = getArguments().getInt("time", 0);
            String desc = getArguments().getString("desc");

            // Set the retrieved values to the corresponding views
            exerciseNameTV.setText(exerciseName);
            caloriesTV.setText("Calories: " + calories);
            timeTV.setText("Time: " + time + " Min");
            descTV.setText(desc);
            exerciseLAV.setAnimationFromUrl(imgUrl);  // Load the Lottie animation
        }

        // Handle back arrow click to navigate back to ExercisesFragment
        backArrowIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to navigate back
                navigateToExercisesFragment();
            }
        });

        return view;
    }

    // Method to navigate back to ExercisesFragment
    private void navigateToExercisesFragment() {
        getParentFragmentManager().popBackStack();  // Navigate back to the previous fragment (ExercisesFragment)
    }
}
