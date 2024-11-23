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

public class RunLogsFragment extends Fragment {

    private ListView listViewRun;
    private List<String> runSessionLogs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run_logs, container, false);

        listViewRun = view.findViewById(R.id.listViewLogsRun);
        runSessionLogs = new ArrayList<>();

        // Retrieve the logged-in user's email
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        // Retrieve run session logs from database
        DatabaseHelper dbHelperRun = new DatabaseHelper(getActivity());
        List<RunSession> sessionsRun = dbHelperRun.getAllRunSessions(userEmail);

        // Populate logs with run session details
        for (RunSession sessionRun : sessionsRun) {
            runSessionLogs.add("Distance Goal: " + sessionRun.getDistanceGoal() +
                    ", Distance: " + sessionRun.getDistance() + " km" +
                    ", Calories: " + sessionRun.getCaloriesBurned() + " kcal" +
                    ", Duration: " + sessionRun.getDuration() + " sec");
        }

        // Create ArrayAdapter and set it to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, runSessionLogs);
        listViewRun.setAdapter(adapter);

        return view;
    }
}
