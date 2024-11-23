package com.example.fitnesstrackerapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class HomeFragment extends Fragment {

    private Button btnWalk, btnRun, btnCycle, btnEx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnWalk = view.findViewById(R.id.btnWalk);
        btnRun = view.findViewById(R.id.btnRun);
        btnCycle = view.findViewById(R.id.btnCycle);
        btnEx = view.findViewById(R.id.btnEx);

        btnWalk.setOnClickListener(v -> {
            loadFragment(new WalkFragment());
            setSelectedButton(btnWalk);
        });

        btnRun.setOnClickListener(v -> {
            loadFragment(new RunFragment());
            setSelectedButton(btnRun);
        });

        btnCycle.setOnClickListener(v -> {
            loadFragment(new CycleFragment());
            setSelectedButton(btnCycle);
        });

        btnEx.setOnClickListener(v -> {
            loadFragment(new ExercisesFragment());
            setSelectedButton(btnEx);
        });


        // Load WalkFragment as the default fragment
        loadFragment(new WalkFragment());
        setSelectedButton(btnWalk);

        return view;
    }

    private void loadFragment(Fragment fragment) {
        // This method will replace the fragment in the fragmentContainer
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment) // replace with the new fragment
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
        btnWalk.setBackgroundResource(R.drawable.card_border); // Change to your default background drawable
        btnRun.setBackgroundResource(R.drawable.card_border);
        btnCycle.setBackgroundResource(R.drawable.card_border);
        btnEx.setBackgroundResource(R.drawable.card_border);
    }
}