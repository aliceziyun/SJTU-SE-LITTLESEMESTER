package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.SportDataActivity;
import com.example.myapplication.Activity.SportPlanActivity;
import com.example.myapplication.R;

public class HomePage extends Fragment {
    private ImageButton spData, spPlan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_home_page,null);

        init(view);

        return view;
    }

    private void init(View view){
        spData = view.findViewById(R.id.sport_data);
        spPlan = view.findViewById(R.id.sport_plan);
        spData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SportDataActivity.class);
                startActivity(intent);
            }
        });
        spPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SportPlanActivity.class);
                startActivity(intent);
            }
        });
    }
}
