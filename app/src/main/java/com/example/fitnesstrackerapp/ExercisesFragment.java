package com.example.fitnesstrackerapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ExercisesFragment extends Fragment implements ExerciseRVAdapter.ExerciseClickInterface {

    private RecyclerView exerciseRV;
    private ArrayList<ExerciseRVModal> exerciseRVModalArrayList;
    private ExerciseRVAdapter exerciseRVAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);

        exerciseRV = view.findViewById(R.id.idRVExercise);
        exerciseRVModalArrayList = new ArrayList<>();
        exerciseRVAdapter = new ExerciseRVAdapter(exerciseRVModalArrayList, requireContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        exerciseRV.setLayoutManager(manager);
        exerciseRV.setAdapter(exerciseRVAdapter);
        addData();

        return view;
    }

    private void addData() {
        exerciseRVModalArrayList.add(new ExerciseRVModal("Side Plank", getResources().getString(R.string.side_plank), "https://lottie.host/30496deb-e991-4486-8270-61db4e7f4da1/DvfzCtp3zJ.json", 20, 10));
        exerciseRVModalArrayList.add(new ExerciseRVModal("Lunges", getResources().getString(R.string.lungs), "https://lottie.host/44b45ccd-3696-42b6-abb3-ad21656c858d/TZ6Kswb5so.json", 30, 10));
        exerciseRVModalArrayList.add(new ExerciseRVModal("High Stepping", getResources().getString(R.string.stepping), "https://lottie.host/e8d327e8-a21f-4dc6-bc5e-19a02c7d16a6/s706vOmBJw.json", 40, 10));
        exerciseRVModalArrayList.add(new ExerciseRVModal("Abs Crunches", getResources().getString(R.string.abs_crunches), "https://lottie.host/34f0fbf4-f34c-4f51-922a-7b3657769883/by9PCQFUeo.json", 30, 20));
        exerciseRVModalArrayList.add(new ExerciseRVModal("Push Ups", getResources().getString(R.string.push_ups), "https://lottie.host/61d24475-3d98-4211-89a8-53bd010c72f1/0QKLpipVWq.json", 10, 5));
        exerciseRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExerciseClick(int position) {
        // Handle the click event here
        // You can start the ExerciseDetailFragment instead of a new Activity
        ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(
                exerciseRVModalArrayList.get(position).getExerciseName(),
                exerciseRVModalArrayList.get(position).getImgUrl(),
                exerciseRVModalArrayList.get(position).getTime(),
                exerciseRVModalArrayList.get(position).getCalories(),
                exerciseRVModalArrayList.get(position).getExerciseDescription()
        );

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
