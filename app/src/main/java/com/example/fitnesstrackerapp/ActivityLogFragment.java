package com.example.fitnesstrackerapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ActivityLogFragment extends Fragment {

    private Button btnWalkLog, btnRunLog, btnCycleLog;
    private ImageView backArrow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity_log, container, false);

        btnWalkLog = view.findViewById(R.id.btnWalkLog);
        btnRunLog = view.findViewById(R.id.btnRunLog);
        btnCycleLog = view.findViewById(R.id.btnCycleLog);
        backArrow = view.findViewById(R.id.backArrow);

        // Handle back arrow click
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear stack
            startActivity(intent);
            requireActivity().finish(); // Finish current activity if needed
        });

        btnWalkLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the fragmentContainer with WalkFragment
                loadFragment(new WalkLogsFragment());
                setSelectedButton(btnWalkLog);

            }
        });

        btnRunLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new RunLogsFragment());
                setSelectedButton(btnRunLog);
            }
        });

        btnCycleLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new CycleLogsFragment());
                setSelectedButton(btnCycleLog);
            }
        });

        // Load WalkLogsFragment as the default fragment
        loadFragment(new WalkLogsFragment());
        setSelectedButton(btnWalkLog);

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // This method will replace the fragment in the fragmentContainer
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerLog, fragment) // replace with the new fragment
                    .addToBackStack(null) // add to back stack to allow users to navigate back
                    .commit();
        }
    }

    private void setSelectedButton(Button selectedButton) {
        // Reset all buttons to normal background
        resetButtonBackgrounds();

        // Change background for the selected button
        selectedButton.setBackgroundResource(R.drawable.selected_border); // Change to your selected background drawable
    }

    private void resetButtonBackgrounds() {
        // Reset all buttons to their default background
        btnWalkLog.setBackgroundResource(R.drawable.card_border); // Change to your default background drawable
        btnRunLog.setBackgroundResource(R.drawable.card_border);
        btnCycleLog.setBackgroundResource(R.drawable.card_border);
    }
}