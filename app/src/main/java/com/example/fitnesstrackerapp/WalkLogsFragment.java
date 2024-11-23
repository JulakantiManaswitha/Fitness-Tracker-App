package com.example.fitnesstrackerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class WalkLogsFragment extends Fragment {

    private ListView listView;
    private List<String> walkSessionLogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk_logs, container, false);

        listView = view.findViewById(R.id.listViewLogs);
        walkSessionLogs = new ArrayList<>();

        // Retrieve the logged-in user's email
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");


        // Retrieve walk session logs from database
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        List<WalkSession> sessions = dbHelper.getAllWalkSessions(userEmail);

        // Populate logs with session details
        for (WalkSession session : sessions) {
            walkSessionLogs.add("Step Goal: " + session.getStepGoal() +
                    ", Steps: " + session.getTotalSteps() +
                    ", Calories: " + session.getCaloriesBurned() + " kcal" +
                    ", Distance: " + session.getDistance() + " km" +
                    ", Duration: " + session.getDuration() + " sec");
        }

        // Create ArrayAdapter and set it to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, walkSessionLogs);
        listView.setAdapter(adapter);

        return view;
    }
}
