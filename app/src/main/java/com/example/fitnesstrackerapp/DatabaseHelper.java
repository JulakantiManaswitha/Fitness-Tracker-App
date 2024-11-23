package com.example.fitnesstrackerapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitnessTrackingData.db";
    private static final String TABLE_NAME = "user_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "EMAIL";
    private static final String COL_3 = "PASSWORD";

    // Profile details table
    private static final String TABLE_PROFILE = "profile_details";
    private static final String COL_PROFILE_ID = "ID";
    private static final String COL_NAME = "NAME";
    private static final String COL_DOB = "DOB";
    private static final String COL_AGE = "AGE";
    private static final String COL_WEIGHT = "WEIGHT";
    private static final String COL_HEIGHT = "HEIGHT";
    private static final String COL_GENDER = "GENDER";

    // Add tables for tracking walk, run, and cycle sessions
    private static final String TABLE_WALK_SESSIONS = "walk_sessions";
    private static final String TABLE_RUN_SESSIONS = "run_sessions";
    private static final String TABLE_CYCLE_SESSIONS = "cycle_sessions";

    private static final String COL_SESSION_ID = "SESSION_ID";
    private static final String COL_EMAIL = "EMAIL"; // Track by user
    private static final String COL_DISTANCE = "DISTANCE"; // For distance covered (in meters or kilometers)
    private static final String COL_DURATION = "DURATION"; // For session duration (in seconds)
    private static final String COL_TIMESTAMP = "TIMESTAMP"; // For date and time of the session
    private static final String COL_TOTAL_STEPS = "totalSteps";
    private static final String COL_CALORIES_BURNED = "caloriesBurned";
    private static final String COL_STEP_GOAL = "stepGoal";
    private static final String COL_DISTANCE_GOAL = "distanceGoal";
    private static final String COL_CALORIES_GOAL = "caloriesGoal";
    private static final String COL_DURATION_GOAL = "durationGoal";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create user table
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT)");

        // Create profile details table with EMAIL column
        db.execSQL("CREATE TABLE " + TABLE_PROFILE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, NAME TEXT, DOB TEXT, AGE INTEGER, WEIGHT REAL, HEIGHT REAL, GENDER TEXT)");


        // Create walk sessions table
        db.execSQL("CREATE TABLE " + TABLE_WALK_SESSIONS + " (" +
                COL_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_DISTANCE + " REAL, " +
                COL_DURATION + " INTEGER, " +
                COL_TOTAL_STEPS +" INTEGER, " +
                COL_CALORIES_BURNED + " INTEGER, " +
                COL_STEP_GOAL + " INTEGER, " +
                COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // Create run sessions table
        db.execSQL("CREATE TABLE " + TABLE_RUN_SESSIONS + " (" +
                COL_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_DISTANCE + " REAL, " +
                COL_DURATION + " INTEGER, " +
                COL_CALORIES_BURNED + " INTEGER, " +
                COL_DISTANCE_GOAL + " REAL, " +
                COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // Create cycle sessions table
        db.execSQL("CREATE TABLE " + TABLE_CYCLE_SESSIONS + " (" +
                COL_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT, " +
                COL_DISTANCE + " REAL, " +
                COL_DURATION + " INTEGER, " +
                COL_CALORIES_BURNED + " INTEGER, " +
                COL_CALORIES_GOAL + " INTEGER, " +
                COL_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALK_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUN_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CYCLE_SESSIONS);

        onCreate(db);
    }

    // Method to add user to the database
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, user.getEmail());
        contentValues.put(COL_3, user.getPassword());
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Returns true if user is added successfully
    }

    // Method to check user login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Method to check if a profile exists for the user
    public boolean isProfileCreated(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE EMAIL=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Method to insert profile details into the database
    public void insertProfile(Profile profile, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, profile.getName());
        contentValues.put(COL_DOB, profile.getDob());
        contentValues.put(COL_AGE, profile.getAge());
        contentValues.put(COL_WEIGHT, profile.getWeight());
        contentValues.put(COL_HEIGHT, profile.getHeight());
        contentValues.put(COL_GENDER, profile.getGender());
        contentValues.put("EMAIL", email); // Associate profile with email
        db.insert(TABLE_PROFILE, null, contentValues);
        db.close();
    }

    // Method to get profile details by email
    public Profile getProfile(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE EMAIL=?", new String[]{email});
        Profile profile = null;

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
            @SuppressLint("Range") String dob = cursor.getString(cursor.getColumnIndex(COL_DOB));
            @SuppressLint("Range") int age = cursor.getInt(cursor.getColumnIndex(COL_AGE));
            @SuppressLint("Range") double weight = cursor.getDouble(cursor.getColumnIndex(COL_WEIGHT));
            @SuppressLint("Range") double height = cursor.getDouble(cursor.getColumnIndex(COL_HEIGHT));
            @SuppressLint("Range") String gender = cursor.getString(cursor.getColumnIndex(COL_GENDER));

            profile = new Profile(name, dob, age, weight, height, gender);
        }

        if (cursor != null) {
            cursor.close();
        }

        return profile;
    }

    // Method to update profile details in the database
    public void updateProfile(Profile profile, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, profile.getName());
        contentValues.put(COL_DOB, profile.getDob());
        contentValues.put(COL_AGE, profile.getAge());
        contentValues.put(COL_WEIGHT, profile.getWeight());
        contentValues.put(COL_HEIGHT, profile.getHeight());
        contentValues.put(COL_GENDER, profile.getGender());

        db.update(TABLE_PROFILE, contentValues, "EMAIL=?", new String[]{email});
        db.close();
    }

    // Method to delete a profile from the database
    public void deleteProfile(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, "EMAIL = ?", new String[]{email}); // Delete where email matches
        db.close();
    }

    // Method to add walk session
    public void insertWalkSession(WalkSession walkSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_EMAIL, walkSession.getEmail());
        values.put(COL_TOTAL_STEPS, walkSession.getTotalSteps());
        values.put(COL_CALORIES_BURNED, walkSession.getCaloriesBurned());
        values.put(COL_DISTANCE, walkSession.getDistance());
        values.put(COL_STEP_GOAL, walkSession.getStepGoal());
        values.put(COL_DURATION, walkSession.getDuration());
        values.put(COL_TIMESTAMP, System.currentTimeMillis()); // Store current time as date

        db.insert(TABLE_WALK_SESSIONS, null, values);
        Log.d("InsertWalkSession", "Inserted Steps: " + walkSession.getTotalSteps());
        db.close();
    }


    // Retrieve walk session details by session ID
    public List<WalkSession> getAllWalkSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<WalkSession> walkSessions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WALK_SESSIONS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String EMAIL = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int totalSteps = cursor.getInt(cursor.getColumnIndex(COL_TOTAL_STEPS));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int stepGoal = cursor.getInt(cursor.getColumnIndex(COL_STEP_GOAL));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));

                // Create a new WalkSession object and add to the list
                WalkSession walkSession = new WalkSession(email, totalSteps, caloriesBurned, distance, stepGoal, duration);
                walkSessions.add(walkSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return walkSessions;
    }

    // Method to add RUN session
    public void insertRunSession(RunSession runSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesRun = new ContentValues();

        valuesRun.put(COL_EMAIL, runSession.getEmail());
        valuesRun.put(COL_CALORIES_BURNED, runSession.getCaloriesBurned());
        valuesRun.put(COL_DISTANCE, runSession.getDistance());
        valuesRun.put(COL_DURATION, runSession.getDuration());
        valuesRun.put(COL_DISTANCE_GOAL, runSession.getDistanceGoal());
        valuesRun.put(COL_TIMESTAMP, System.currentTimeMillis()); // Store current time as date

        db.insert(TABLE_RUN_SESSIONS, null, valuesRun);
        db.close();
    }

    // Retrieve run session details by session ID
    public List<RunSession> getAllRunSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RunSession> runSessions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RUN_SESSIONS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String EMAIL = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") double distanceGoal = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE_GOAL));

                // Create a new WalkSession object and add to the list
                RunSession runSession = new RunSession(email, caloriesBurned, distance, duration, distanceGoal);
                runSessions.add(runSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return runSessions;
    }

    // Method to add Cycle session
    public void insertCycleSession(CycleSession cycleSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valuesCycle = new ContentValues();

        valuesCycle.put(COL_EMAIL, cycleSession.getEmail());
        valuesCycle.put(COL_DISTANCE, cycleSession.getDistance());
        valuesCycle.put(COL_DURATION, cycleSession.getDuration());
        valuesCycle.put(COL_CALORIES_BURNED, cycleSession.getCaloriesBurned());
        valuesCycle.put(COL_CALORIES_GOAL, cycleSession.getCaloriesGoal());
        valuesCycle.put(COL_TIMESTAMP, System.currentTimeMillis()); // Store current time as date

        db.insert(TABLE_CYCLE_SESSIONS, null, valuesCycle);
        db.close();
    }

    // Retrieve cycle session details by session ID
    public List<CycleSession> getAllCycleSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CycleSession> cycleSessions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CYCLE_SESSIONS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String EMAIL = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") int caloriesGoal = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_GOAL));

                // Create a new WalkSession object and add to the list
                CycleSession cycleSession = new CycleSession(email, distance, duration, caloriesBurned, caloriesGoal);
                cycleSessions.add(cycleSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cycleSessions;
    }


    //WALK
    public List<WalkSession> getTodayWalkSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<WalkSession> walkSessions = new ArrayList<>();

        // Get today's date in a format suitable for your timestamp comparison
        long startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000)); // Start of today
        long endOfDay = startOfDay + (24 * 60 * 60 * 1000); // End of today

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WALK_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ? AND " + COL_EMAIL + " = ?",
                new String[]{String.valueOf(startOfDay), String.valueOf(endOfDay), email});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String EMAIL = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int totalSteps = cursor.getInt(cursor.getColumnIndex(COL_TOTAL_STEPS));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int stepGoal = cursor.getInt(cursor.getColumnIndex(COL_STEP_GOAL));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));

                WalkSession walkSession = new WalkSession(email, totalSteps, caloriesBurned, distance, stepGoal, duration);
                walkSessions.add(walkSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return walkSessions;
    }


    // Weekly Steps for Walking
    public List<WalkSession> getThisWeekWalkSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<WalkSession> walkSessions = new ArrayList<>();

        // Calculate the start of the week (Monday) and the end of the week (next Monday)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfWeek = calendar.getTimeInMillis(); // Start of this week
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Move to the next week
        long endOfWeek = calendar.getTimeInMillis(); // End of this week

        // Log the calculated start and end of the week
        Log.d("WalkSessions", "Start of week: " + startOfWeek);
        Log.d("WalkSessions", "End of week: " + endOfWeek);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WALK_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ? AND " + COL_EMAIL + " = ?",
                new String[]{String.valueOf(startOfWeek), String.valueOf(endOfWeek), email});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String EMAIL = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int totalSteps = cursor.getInt(cursor.getColumnIndex(COL_TOTAL_STEPS));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int stepGoal = cursor.getInt(cursor.getColumnIndex(COL_STEP_GOAL));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));

                WalkSession walkSession = new WalkSession(email, totalSteps, caloriesBurned, distance, stepGoal, duration);
                walkSessions.add(walkSession);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Log the number of sessions found
        Log.d("WalkSessions", "Weekly sessions found: " + walkSessions.size());

        return walkSessions;
    }


    // Monthly Steps for Walking
    public List<WalkSession> getThisMonthWalkSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<WalkSession> walkSessions = new ArrayList<>();

        // Calculate the start of the month and the end of the month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfMonth = calendar.getTimeInMillis(); // Start of this month
        calendar.add(Calendar.MONTH, 1); // Move to the next month
        long endOfMonth = calendar.getTimeInMillis(); // End of this month

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WALK_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ? AND " + COL_EMAIL + " = ?",
                new String[]{String.valueOf(startOfMonth), String.valueOf(endOfMonth), email});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String EMAIL = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int totalSteps = cursor.getInt(cursor.getColumnIndex(COL_TOTAL_STEPS));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int stepGoal = cursor.getInt(cursor.getColumnIndex(COL_STEP_GOAL));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));

                WalkSession walkSession = new WalkSession(email, totalSteps, caloriesBurned, distance, stepGoal, duration);
                walkSessions.add(walkSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return walkSessions;
    }


    // Example of a debug method to check today's records
    public void printTodayWalkSessions(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_WALK_SESSIONS + " WHERE EMAIL = ? AND DATE(TIMESTAMP) = DATE('now')";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int totalSteps = cursor.getInt(cursor.getColumnIndex(COL_TOTAL_STEPS));
                Log.d("TodayWalk", "Steps: " + totalSteps);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }


    //run
    public List<RunSession> getTodayRunSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RunSession> runSessions = new ArrayList<>();

        // Get today's date in a format suitable for your timestamp comparison
        long startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000)); // Start of today
        long endOfDay = startOfDay + (24 * 60 * 60 * 1000); // End of today

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RUN_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ?",
                new String[]{String.valueOf(startOfDay), String.valueOf(endOfDay)});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") double distanceGoal = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE_GOAL));

                RunSession runSession = new RunSession(email, caloriesBurned, distance, duration, distanceGoal);
                runSessions.add(runSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return runSessions;
    }

    public List<RunSession> getWeeklyRunSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RunSession> runSessions = new ArrayList<>();

        // Get the start of the current week (assuming week starts on Sunday)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        long startOfWeek = calendar.getTimeInMillis();
        long endOfWeek = startOfWeek + (7 * 24 * 60 * 60 * 1000); // End of the week

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RUN_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ?",
                new String[]{String.valueOf(startOfWeek), String.valueOf(endOfWeek)});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") double distanceGoal = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE_GOAL));

                RunSession runSession = new RunSession(email, caloriesBurned, distance, duration, distanceGoal);
                runSessions.add(runSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return runSessions;
    }

    public List<RunSession> getMonthlyRunSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<RunSession> runSessions = new ArrayList<>();

        // Calculate the start and end of the month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfMonth = calendar.getTimeInMillis(); // Start of this month
        calendar.add(Calendar.MONTH, 1); // Move to the first day of the next month
        long endOfMonth = calendar.getTimeInMillis(); // End of this month

        Log.d("RunSession", "Start of Month: " + startOfMonth + ", End of Month: " + endOfMonth); // Log timestamps

        // Query the database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RUN_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ?",
                new String[]{String.valueOf(startOfMonth), String.valueOf(endOfMonth)});

        if (cursor.moveToFirst()) {
            do {
                // Retrieve columns using correct indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") double distanceGoal = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE_GOAL));

                RunSession runSession = new RunSession(email, caloriesBurned, distance, duration, distanceGoal);
                runSessions.add(runSession);

                Log.d("RunSessionData", "Session ID: " + sessionId + ", Email: " + email +
                        ", Calories: " + caloriesBurned + ", Distance: " + distance +
                        ", Duration: " + duration + ", Goal: " + distanceGoal);
            } while (cursor.moveToNext());
        } else {
            Log.d("RunSession", "No sessions found for the specified month range.");
        }
        cursor.close();
        return runSessions;
    }

    //cycle
    public List<CycleSession> getTodayCycleSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CycleSession> cycleSessions = new ArrayList<>();

        // Get today's date in a format suitable for your timestamp comparison
        long startOfDay = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000)); // Start of today
        long endOfDay = startOfDay + (24 * 60 * 60 * 1000); // End of today

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CYCLE_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ?",
                new String[]{String.valueOf(startOfDay), String.valueOf(endOfDay)});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") int caloriesGoal = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_GOAL));

                // Create a new WalkSession object and add to the list
                CycleSession cycleSession = new CycleSession(email, distance, duration, caloriesBurned, caloriesGoal);
                cycleSessions.add(cycleSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cycleSessions;
    }

    public List<CycleSession> getWeeklyCycleSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CycleSession> cycleSessions = new ArrayList<>();

        // Get the start of the current week (assuming week starts on Sunday)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        long startOfWeek = calendar.getTimeInMillis();
        long endOfWeek = startOfWeek + (7 * 24 * 60 * 60 * 1000); // End of the week

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CYCLE_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ?",
                new String[]{String.valueOf(startOfWeek), String.valueOf(endOfWeek)});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") int caloriesGoal = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_GOAL));

                // Create a new WalkSession object and add to the list
                CycleSession cycleSession = new CycleSession(email, distance, duration, caloriesBurned, caloriesGoal);
                cycleSessions.add(cycleSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cycleSessions;
    }

    public List<CycleSession> getMonthlyCycleSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CycleSession> cycleSessions = new ArrayList<>();

        // Calculate the start and end of the month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfMonth = calendar.getTimeInMillis(); // Start of this month
        calendar.add(Calendar.MONTH, 1); // Move to the first day of the next month
        long endOfMonth = calendar.getTimeInMillis(); // End of this month

        Log.d("CycleSession", "Start of Month: " + startOfMonth + ", End of Month: " + endOfMonth); // Log timestamps

        // Query the database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CYCLE_SESSIONS + " WHERE TIMESTAMP >= ? AND TIMESTAMP < ?",
                new String[]{String.valueOf(startOfMonth), String.valueOf(endOfMonth)});

        if (cursor.moveToFirst()) {
            do {
                // Use correct column indexes or names
                @SuppressLint("Range") int sessionId = cursor.getInt(cursor.getColumnIndex(COL_SESSION_ID));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COL_EMAIL));
                @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex(COL_DISTANCE));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COL_DURATION));
                @SuppressLint("Range") int caloriesBurned = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_BURNED));
                @SuppressLint("Range") int caloriesGoal = cursor.getInt(cursor.getColumnIndex(COL_CALORIES_GOAL));

                // Create a new WalkSession object and add to the list
                CycleSession cycleSession = new CycleSession(email, distance, duration, caloriesBurned, caloriesGoal);
                cycleSessions.add(cycleSession);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cycleSessions;
    }

}