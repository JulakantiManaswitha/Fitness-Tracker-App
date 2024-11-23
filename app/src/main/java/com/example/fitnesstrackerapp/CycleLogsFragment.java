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

public class CycleLogsFragment extends Fragment {

    private ListView listViewCycle;
    private List<String> cycleSessionLogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cycle_logs, container, false);

        listViewCycle = view.findViewById(R.id.listViewLogsCycle);
        cycleSessionLogs = new ArrayList<>();

        // Retrieve the logged-in user's email
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        // Retrieve cycle session logs from database
        DatabaseHelper dbHelperRun = new DatabaseHelper(getActivity());
        List<CycleSession> sessionsCycle = dbHelperRun.getAllCycleSessions(userEmail);

        // Populate logs with run session details
        for (CycleSession sessionCycle : sessionsCycle) {
            cycleSessionLogs.add("Calories Goal: " + sessionCycle.getCaloriesGoal() +
                    ", Distance: " + sessionCycle.getDistance() + " km" +
                    ", Calories: " + sessionCycle.getCaloriesBurned() + " kcal" +
                    ", Duration: " + sessionCycle.getDuration() + " sec");
        }

        // Create ArrayAdapter and set it to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cycleSessionLogs);
        listViewCycle.setAdapter(adapter);

        return view;
    }
}
