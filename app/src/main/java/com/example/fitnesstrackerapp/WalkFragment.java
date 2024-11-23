package com.example.fitnesstrackerapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;

public class WalkFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private TextView stepCountView, caloriesCountView, distanceCountView, stepGoalTextView, caloriesGoalTextView, distanceGoalTextView, durationTextView;
    private ProgressBar stepsProgressBar, caloriesProgressBar, distanceProgressBar;
    private Button startButton, setTargetButton;
    private boolean isTimerRunning = false;
    private int totalSteps = 0;
    private int previousSteps = 0;
    private int stepGoal = 5000; // Default step goal
    private double caloriesGoal = 250; // Default calories goal in calories
    private double distanceGoal = 4000; // Default distance goal in meters
    private long startTime;     //start time of the walk in milliseconds
    private long elapsedTime;
    private Thread timerThread; // Thread for counting up time
    private boolean isFragmentActive = false;


    // List to store walk session logs
    private ArrayList<WalkSession> walkSessionLogs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk, container, false);

        // Initialize UI elements
        stepCountView = view.findViewById(R.id.tvSteps);
        caloriesCountView = view.findViewById(R.id.tvCalories);
        distanceCountView = view.findViewById(R.id.tvDistance);
        stepsProgressBar = view.findViewById(R.id.progress_steps);
        caloriesProgressBar = view.findViewById(R.id.progress_calories);
        distanceProgressBar = view.findViewById(R.id.progress_distance);
        durationTextView = view.findViewById(R.id.durationTextView);
        startButton = view.findViewById(R.id.btnStart);
        setTargetButton = view.findViewById(R.id.btnSetTarget);
        stepGoalTextView = view.findViewById(R.id.StepGoal); // TextView for displaying step goal
        caloriesGoalTextView = view.findViewById(R.id.CaloriesGoal);
        distanceGoalTextView = view.findViewById(R.id.DistanceGoal);

        LottieAnimationView animationView = view.findViewById(R.id.lottieAnimationView);
        animationView.setAnimation("walkinggif.json"); // Set animation file
        animationView.playAnimation(); // Start animation

        // Initialize Sensor Manager
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            isSensorPresent = false;
            Toast.makeText(getActivity(), "Step Counter Sensor not available!", Toast.LENGTH_LONG).show();
        }

        // Start button click listener
        startButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer(); // If timer is running, stop it
            } else {
                startTimer(); // If timer is not running, start it
            }
        });

        // Set target button click listener
        setTargetButton.setOnClickListener(v -> showSetTargetDialog());

        // Request permission for activity recognition
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
        }

        // Set initial progress for goals
        stepsProgressBar.setMax(stepGoal);
        caloriesProgressBar.setMax((int) caloriesGoal);
        distanceProgressBar.setMax((int) distanceGoal);
        stepGoalTextView.setText("Step Goal: " + stepGoal); // Display default step goal
        caloriesGoalTextView.setText("Calories Goal: " + caloriesGoal + " cals");
        distanceGoalTextView.setText("Distance Goal: " + distanceGoal + " meters");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentActive = true; // Fragment is active
        if (isSensorPresent && isTimerRunning) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentActive = false; // Fragment is no longer active
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int currentSteps = (int) event.values[0];
            if (previousSteps == 0) {
                // Set previousSteps to currentSteps when starting
                previousSteps = currentSteps;
            }
            totalSteps = currentSteps - previousSteps; // Steps since last reset
            updateUI();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    /**
     * Starts the timer and the step counter.
     */
    private void startTimer() {
        totalSteps = 0; // Reset total steps when the timer starts
        previousSteps = 0; // Reset previous steps when timer starts
        isTimerRunning = true; // Set flag to indicate the timer is running
        startTime = System.currentTimeMillis(); // Record start time
        startButton.setText("Stop"); // Change button text to "Stop"

        // Register step sensor listener
        startStepCounting();

        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isTimerRunning) {
                    try {
                        Thread.sleep(1000); // Sleep for a second
                        updateTimer(); // Update timer UI
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timerThread.start();

        // Check if the step goal has been reached
        if (totalSteps >= stepGoal) {
            Toast.makeText(requireContext(), "Congratulations! Step goal reached!", Toast.LENGTH_SHORT).show();
            stopTimer(); // Stop the timer when step goal is reached
        }
    }

    /**
     * Stops the timer and the step counter, and saves the walking session details.
     */
    private void stopTimer() {
        isTimerRunning = false; // Set flag to indicate the timer has stopped
        startButton.setText("Start"); // Change button text back to "Start"


        // Wait for the timer thread to finish
        if (timerThread != null) {
            try {
                timerThread.join(); // Wait for the timer thread to complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Now reset the elapsed time and update UI
        elapsedTime = 0; // Reset elapsed time to 0
        // Now update the UI to show 00:00
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                durationTextView.setText("Duration: 00:00"); // Update the UI to show 00:00
            }
        });

        stopStepCounting(); // Stop listening to step sensor

        // Calculate the duration of the walk session and save
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // Calculate elapsed time in milliseconds
        long durationInSeconds = duration / 1000; // Convert duration to seconds

        // Save the walking session when the timer stops
        saveWalkSession(durationInSeconds);
        resetTimer(); // Optionally reset the timer
    }


    /**
     * Resets the timer to its initial value.
     */
    private void resetTimer() {
        totalSteps = 0; // Reset total steps
        previousSteps = 0; // Reset previous steps

        // Reset the UI elements
        durationTextView.setText("Duration: 00:00"); // Reset duration display
        stepCountView.setText("0"); // Reset step count display
        caloriesCountView.setText(String.format("%.2f cals", 0.0)); // Reset calories display
        distanceCountView.setText(String.format("%.2f meters", 0.0)); // Reset distance display

        // Reset progress bars
        stepsProgressBar.setProgress(0);
        caloriesProgressBar.setProgress(0);
        distanceProgressBar.setProgress(0);

        // Reset goals to their initial values
        stepGoal = 5000; // Default step goal
        caloriesGoal = 250; // Default calories goal in calories
        distanceGoal = 4000; // Default distance goal in meters

        // Reset goal TextViews
        stepGoalTextView.setText("Step Goal: " + stepGoal);
        caloriesGoalTextView.setText("Calories Goal: " + caloriesGoal + " cals");
        distanceGoalTextView.setText("Distance Goal: " + distanceGoal + " meters");

        // Update progress max values if necessary
        stepsProgressBar.setMax(stepGoal);
        caloriesProgressBar.setMax((int) caloriesGoal);
        distanceProgressBar.setMax((int) distanceGoal);
    }


    /**
     * Starts counting steps by registering the sensor listener.
     */
    private void startStepCounting() {
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Stops counting steps by unregistering the sensor listener.
     */
    private void stopStepCounting() {
        if (isSensorPresent) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * Updates the timer display in the UI.
     */
    private void updateTimer() {
        if (isTimerRunning && isFragmentActive) { // Check if the timer is still running and fragment is active
            long elapsedTime = System.currentTimeMillis() - startTime; // Calculate elapsed time
            long seconds = (elapsedTime / 1000) % 60; // Get elapsed seconds
            long minutes = (elapsedTime / 1000) / 60; // Get elapsed minutes

            // Update duration display on the UI thread
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    durationTextView.setText(String.format("Duration: %02d:%02d", minutes, seconds));
                }
            });
        }
    }

    /**
     * Updates the UI elements like steps count and progress bars.
     */
    private void updateUI() {
        stepCountView.setText(String.valueOf(totalSteps));
        double caloriesBurned = calculateCalories(totalSteps);
        caloriesCountView.setText(String.format("%.2f cals", caloriesBurned));

        double distance = calculateDistance(totalSteps);
        distanceCountView.setText(String.format("%.2f meters", distance));

        // Update progress bars
        stepsProgressBar.setProgress(Math.min(totalSteps, stepGoal));
        caloriesProgressBar.setProgress((int) Math.min(caloriesBurned, caloriesGoal));
        distanceProgressBar.setProgress((int) Math.min(distance, distanceGoal));

        if(totalSteps >= stepGoal || caloriesBurned >= caloriesGoal || distance >= distanceGoal) {
            showGoalAchievedMessage();
        }
    }

    private void showGoalAchievedMessage() {
        Toast.makeText(getActivity(), "Congratulations! You've reached your step goal!", Toast.LENGTH_SHORT).show();

        // Optionally log this achievement
        Log.d("WalkFragment", "User reached step goal: " + totalSteps + " steps.");

        // Optionally reset steps for continued tracking
        previousSteps = totalSteps; // Reset to current count
    }

    /**
     * Calculates the distance walked in kilometers based on the number of steps.
     * Assumes an average step length of 0.78 meters.
     *
     * @param steps The total number of steps walked.
     * @return The distance in kilometers.
     */
    private double calculateDistance(int steps) {
        double stepLengthInMeters = 0.78; // Average step length in meters
        return (steps * stepLengthInMeters) ; // Convert to meters
    }

    /**
     * Calculates the calories burned based on the number of steps.
     * Assumes an average of 0.05 kcal burned per step.
     *
     * @param steps The total number of steps walked.
     * @return The calories burned in kilocalories.
     */
    private double calculateCalories(int steps) {
        return steps * 0.05; // Calculate calories burned in kilocalories
    }

    /**
     * Shows a dialog for setting the target step goal.
     */
    /**
     * Shows a dialog for setting the target goals.
     */
    private void showSetTargetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Target Goals");

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_target_walk, null);
        EditText stepsInput = dialogView.findViewById(R.id.editTextSteps);
        EditText caloriesInput = dialogView.findViewById(R.id.editTextCalories);
        EditText distanceInput = dialogView.findViewById(R.id.editTextDistance);

        builder.setView(dialogView);
        builder.setPositiveButton("Set", (dialog, which) -> {
            boolean isGoalSet = false; // Track if any goal is set

            // Get values from input fields
            try {
                if (!stepsInput.getText().toString().isEmpty()) {
                    stepGoal = Integer.parseInt(stepsInput.getText().toString());
                    stepsProgressBar.setMax(stepGoal); // Max step goal
                    stepGoalTextView.setText("Step Goal: " + stepGoal);
                    Toast.makeText(getActivity(), "Step goal set to " + stepGoal, Toast.LENGTH_SHORT).show();
                    Log.d("WalkFragment", "Step goal set to " + stepGoal);
                    isGoalSet = true; // Mark that at least one goal is set
                }

                if (!caloriesInput.getText().toString().isEmpty()) {
                    caloriesGoal = Double.parseDouble(caloriesInput.getText().toString());
                    caloriesProgressBar.setMax((int) caloriesGoal); // Max calorie goal
                    caloriesGoalTextView.setText("Calories Goal: " + caloriesGoal + " cals");
                    Toast.makeText(getActivity(), "Calories goal set to " + caloriesGoal + " cals", Toast.LENGTH_SHORT).show();
                    Log.d("WalkFragment", "Calories goal set to " + caloriesGoal);
                    isGoalSet = true; // Mark that at least one goal is set
                }

                if (!distanceInput.getText().toString().isEmpty()) {
                    distanceGoal = Double.parseDouble(distanceInput.getText().toString());
                    distanceProgressBar.setMax((int) distanceGoal); // Max distance in meters
                    distanceGoalTextView.setText("Distance Goal: " + distanceGoal + " meters");
                    Toast.makeText(getActivity(), "Distance goal set to " + distanceGoal + " meters", Toast.LENGTH_SHORT).show();
                    Log.d("WalkFragment", "Distance goal set to " + distanceGoal);
                    isGoalSet = true; // Mark that at least one goal is set
                }

                // If no goals were set, show a message
                if (!isGoalSet) {
                    Toast.makeText(getActivity(), "No goals were set!", Toast.LENGTH_SHORT).show();
                    Log.d("WalkFragment", "No goals were set.");
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                Log.e("WalkFragment", "Error parsing input: " + e.getMessage());
            }
        });

        builder.setNegativeButton("Cancel", null); // Handle cancel button
        builder.show(); // Show the dialog
    }

    /**
     * Saves the walk session details to the list and database.
     */
    private void saveWalkSession(long duration) {
        // Retrieve email from SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", null);

        if (email == null || email.isEmpty()) {
            Toast.makeText(getActivity(), "User email not found. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }

        double caloriesBurned = calculateCalories(totalSteps);
        double distance = calculateDistance(totalSteps);


        // Create the WalkSession object
        WalkSession walkSession = new WalkSession(email, totalSteps, caloriesBurned, distance, stepGoal, duration);

        // Save to the in-memory list
        walkSessionLogs.add(walkSession);
        Toast.makeText(getActivity(), "Session logged successfully", Toast.LENGTH_SHORT).show();


        // Save to the database
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        dbHelper.insertWalkSession(walkSession);
    }

}