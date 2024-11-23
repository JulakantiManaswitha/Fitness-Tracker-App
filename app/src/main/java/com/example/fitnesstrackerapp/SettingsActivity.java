package com.example.fitnesstrackerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private EditText reminderEditText;
    private ImageView backArrow;
    private Calendar calendar; // To store selected time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        reminderEditText = findViewById(R.id.reminder_edit_text);
        Button setReminderButton = findViewById(R.id.set_reminder_button);
        backArrow = findViewById(R.id.backArrow);
        calendar = Calendar.getInstance(); // Initialize the calendar

        backArrow.setOnClickListener(v -> finish());
        setReminderButton.setOnClickListener(v -> showTimePicker()); // Show time picker

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Check and request permission for scheduling exact alarms (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager.class).canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent); // This will open the system settings to grant permission
            }
        }

    }

    // Show a TimePickerDialog to let the user choose the reminder time
    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute1);
            calendar.set(Calendar.SECOND, 0); // Set seconds to 0 for accuracy
            setReminder(); // After time is selected, set the reminder
        }, hour, minute, true);

        timePickerDialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setReminder() {
        String reminderText = reminderEditText.getText().toString().trim();
        if (reminderText.isEmpty()) {
            Toast.makeText(this, "Please enter a reminder text", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.putExtra("reminder_text", reminderText);

        // Use FLAG_IMMUTABLE for security purposes
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {

            // Set the alarm for the chosen time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Reminder set for: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
        }
    }

}
