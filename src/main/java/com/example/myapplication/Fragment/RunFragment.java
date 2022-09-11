package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.RunningActivity;
import com.example.myapplication.Activity.SportPlanActivity;
import com.example.myapplication.R;

public class RunFragment extends Fragment {
    Button runningBtn;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_run,
                container, false);
        runningBtn = (Button) view.findViewById(R.id.running_btn);
        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RunningActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
