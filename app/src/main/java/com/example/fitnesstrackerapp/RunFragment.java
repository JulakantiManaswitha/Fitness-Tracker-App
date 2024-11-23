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
import android.os.CountDownTimer;
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
import java.util.List;

public class RunFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private TextView distanceCountView, caloriesCountView, durationCountView, durationTextView, distanceGoalTextView, caloriesGoalTextView, durationGoalTextView;
    private ProgressBar distanceProgressBar, caloriesProgressBar, durationProgressBar;
    private Button startButton, setTargetButton;
    private CountDownTimer countDownTimer;
    private long timer = 0; // 10 minutes for the timer
    private boolean isTimerRunning = false;
    private int totalSteps = 0;
    private int previousSteps = 0;
    private double distanceGoal = 5000; // Default distance goal in meters
    private double caloriesGoal = 500; // Default calories goal in calories
    private int durationGoal = 1800;  //default duration goal in seconds
    private long startTime;     //start time of the walk in milliseconds

    // List to store run session logs
    private ArrayList<RunSession> runSessionLogs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        // Initialize UI elements
        distanceCountView = view.findViewById(R.id.tvDistanceRun);
        caloriesCountView = view.findViewById(R.id.tvCaloriesRun);
        durationCountView = view.findViewById(R.id.tvDurationRun);
        distanceProgressBar = view.findViewById(R.id.progress_distance_run);
        caloriesProgressBar = view.findViewById(R.id.progress_calories_run);
        durationProgressBar = view.findViewById(R.id.progress_duration_run);
        durationTextView = view.findViewById(R.id.durationTextViewRun);
        startButton = view.findViewById(R.id.btnStartRun);
        setTargetButton = view.findViewById(R.id.btnSetTargetRun);
        distanceGoalTextView = view.findViewById(R.id.DistanceGoalRun); // TextView for displaying distanceGoal
        caloriesGoalTextView = view.findViewById(R.id.CaloriesGoalRun);
        durationGoalTextView = view.findViewById(R.id.DurationGoalRun);

        LottieAnimationView animationView = view.findViewById(R.id.lottieAnimationViewRun);
        animationView.setAnimation("runninggif.json"); // Set animation file
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
        setTargetButton.setOnClickListener(v -> showSetTargetDialogRun());

        // Request permission for activity recognition
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
        }

        // Set initial progress for goals
        distanceProgressBar.setMax((int) distanceGoal);
        caloriesProgressBar.setMax((int) caloriesGoal);
        durationProgressBar.setMax(durationGoal);
        distanceGoalTextView.setText("Distance Goal: " + distanceGoal + " meters"); // Display default distance goal
        caloriesGoalTextView.setText("Calories Goal: " + caloriesGoal + " cals");
        durationGoalTextView.setText("Duration Goal: " + durationGoal + " seconds");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSensorPresent && isTimerRunning) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
        startTime = System.currentTimeMillis(); // Record start time
        isTimerRunning = true; // Set flag to indicate the timer is running
        startButton.setText("Stop"); // Change button text to "Stop"


        // Register step sensor listener
        startStepCounting();

        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer += 1000;
                updateTimer();
                updateUI(); // Update step count live

            }

            @Override
            public void onFinish() {
                durationTextView.setText("Timer: Finished!!");
                Toast.makeText(requireContext(), "Timer Finished!!", Toast.LENGTH_SHORT).show();
                stopTimer(); // Stop the timer when finished
            }
        }.start();
    }

    /**
     * Stops the timer and the step counter, and saves the walking session details.
     */
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the countdown timer
        }

        isTimerRunning = false; // Set flag to indicate the timer has stopped
        startButton.setText("Start"); // Change button text back to "Start"
        stopStepCounting(); // Stop listening to step sensor

        timer = 0; // Reset timer

        // Calculate the duration of the run session
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // Calculate elapsed time in milliseconds
        long durationInSeconds = duration / 1000; // Convert duration to seconds

        // Save the walking session when the timer stops
        saveRunSession(durationInSeconds);
        resetTimer();
    }

    /**
     * Resets the timer to its initial value.
     */
    private void resetTimer() {
        timer = 0; // Reset timer
        totalSteps = 0; // Reset total steps
        previousSteps = 0; // Reset previous steps

        // Reset the UI elements
        durationTextView.setText("Duration: 00:00"); // Reset duration display
        distanceCountView.setText(String.format("%.2f meters", 0.0)); // Reset distance display
        caloriesCountView.setText(String.format("%.2f cals", 0.0)); // Reset calories display
        durationCountView.setText(String.format("%d secs", 0));

        // Reset progress bars
        distanceProgressBar.setProgress(0);
        caloriesProgressBar.setProgress(0);
        durationProgressBar.setProgress(0);

        // Reset goals to their initial values
        distanceGoal = 5000; // Default distance goal in meters
        caloriesGoal = 500; // Default calories goal in calories
        durationGoal = 1800; // Default duration goal in seconds

        // Reset goal TextViews
        distanceGoalTextView.setText("Distance Goal: " + distanceGoal + " meters");
        caloriesGoalTextView.setText("Calories Goal: " + caloriesGoal + " cals");
        durationGoalTextView.setText("Duration Goal: " + durationGoal + " seconds");

        // Update progress max values if necessary
        distanceProgressBar.setMax((int) distanceGoal);
        caloriesProgressBar.setMax((int) caloriesGoal);
        durationProgressBar.setMax(durationGoal);
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
        int minutes = (int) timer / 1000 / 60;
        int seconds = (int) (timer / 1000) % 60;
        String timeText = String.format("Duration: %02d:%02d ", minutes, seconds);
        durationTextView.setText(timeText);
    }

    /**
     * Updates the UI elements like steps count and progress bars.
     */
    private void updateUI() {

        double distance = calculateDistance(totalSteps);
        distanceCountView.setText(String.format("%.2f meters", distance));

        double caloriesBurned = calculateCalories(totalSteps);
        caloriesCountView.setText(String.format("%.2f cals", caloriesBurned));

        // Calculate duration in seconds
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // Duration in seconds
        String formattedTime = formatDuration(elapsedTime); // Format the time for display
        String formattedTimeInDurationCountView = formatDurationInDurationCountView(elapsedTime);
        durationCountView.setText(formattedTimeInDurationCountView);
        durationTextView.setText(formattedTime); // Update the duration display

        // Update progress bars
        distanceProgressBar.setProgress((int) Math.min(distance, distanceGoal));
        caloriesProgressBar.setProgress((int) Math.min(caloriesBurned, caloriesGoal));
        durationProgressBar.setProgress((int) Math.min(elapsedTime, durationGoal));

        // Check if goals are met
        if (distance >= distanceGoal || caloriesBurned >= caloriesGoal || elapsedTime >= durationGoal) {
            stopTimer(); // Stop the timer if any goal is met
            Toast.makeText(getActivity(), "Goals Met! Timer Stopped.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Formats the duration in seconds into a string of format mm:ss.
     *
     * @param seconds The total duration in seconds.
     * @return A formatted string representing the duration.
     */
    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("Duration: %02d:%02d", minutes, remainingSeconds);
    }

    private String formatDurationInDurationCountView(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
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
    private void showSetTargetDialogRun() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Run Target Goals");

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_target, null);
        EditText distanceInput = dialogView.findViewById(R.id.editTextDistance);
        EditText caloriesInput = dialogView.findViewById(R.id.editTextCalories);
        EditText durationInput = dialogView.findViewById(R.id.editTextDuration);

        builder.setView(dialogView);
        builder.setPositiveButton("Set", (dialog, which) -> {
            boolean isGoalSet = false; // Track if any goal is set

            // Get values from input fields
            try {
                if (!distanceInput.getText().toString().isEmpty()) {
                    distanceGoal = Double.parseDouble(distanceInput.getText().toString());
                    distanceProgressBar.setMax((int) distanceGoal); // Max distance in meters
                    distanceGoalTextView.setText("Distance Goal: " + distanceGoal + " meters");
                    Toast.makeText(getActivity(), "Distance goal set to " + distanceGoal + " meters", Toast.LENGTH_SHORT).show();
                    Log.d("RunFragment", "Distance goal set to " + distanceGoal);
                    isGoalSet = true; // Mark that at least one goal is set
                }

                if (!caloriesInput.getText().toString().isEmpty()) {
                    caloriesGoal = Double.parseDouble(caloriesInput.getText().toString());
                    caloriesProgressBar.setMax((int) caloriesGoal); // Max calorie goal
                    caloriesGoalTextView.setText("Calories Goal: " + caloriesGoal + " cals");
                    Toast.makeText(getActivity(), "Calories goal set to " + caloriesGoal + " cals", Toast.LENGTH_SHORT).show();
                    Log.d("RunFragment", "Calories goal set to " + caloriesGoal);
                    isGoalSet = true; // Mark that at least one goal is set
                }

                if (!durationInput.getText().toString().isEmpty()) {
                    durationGoal = Integer.parseInt(durationInput.getText().toString());
                    durationProgressBar.setMax(durationGoal); // Max duration goal
                    durationGoalTextView.setText("Duration Goal: " + durationGoal);
                    Toast.makeText(getActivity(), "Duration goal set to " + durationGoal, Toast.LENGTH_SHORT).show();
                    Log.d("RunFragment", "Duration goal set to " + durationGoal);
                    isGoalSet = true; // Mark that at least one goal is set
                }


                // If no goals were set, show a message
                if (!isGoalSet) {
                    Toast.makeText(getActivity(), "No goals were set!", Toast.LENGTH_SHORT).show();
                    Log.d("RunFragment", "No goals were set.");
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                Log.e("RunFragment", "Error parsing input: " + e.getMessage());
            }
        });

        builder.setNegativeButton("Cancel", null); // Handle cancel button
        builder.show(); // Show the dialog
    }



    /**
     * Saves the walk session details to the list and database.
     */
    private void saveRunSession(long duration) {
        // Retrieve email from SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", null);

        if (email == null || email.isEmpty()) {
            Toast.makeText(getActivity(), "User email not found. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }

        double distance = calculateDistance(totalSteps);
        double caloriesBurned = calculateCalories(totalSteps);
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // Duration in seconds
        RunSession runSession = new RunSession(email, caloriesBurned, distance, duration, distanceGoal);

        // Save to the in-memory list
        runSessionLogs.add(runSession);
        Toast.makeText(getActivity(), "Run Session logged successfully", Toast.LENGTH_SHORT).show();

        // Save to the database
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        dbHelper.insertRunSession(runSession);
    }

}