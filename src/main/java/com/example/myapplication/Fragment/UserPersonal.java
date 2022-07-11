package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.CourseListActivity;
import com.example.myapplication.Activity.MessageActivity;
import com.example.myapplication.Activity.ModifyActivity;
import com.example.myapplication.Activity.MyPostActivity;
import com.example.myapplication.Activity.SportDataActivity;
import com.example.myapplication.Activity.SportPlanActivity;
import com.example.myapplication.Activity.StarPostActivity;
import com.example.myapplication.Controller.UserController;
import com.example.myapplication.R;
import com.example.myapplication.entity.User;

public class UserPersonal extends Fragment {
    private Button logout;
    private RelativeLayout btn_course, btn_spdata, btn_spplan, btn_mystar, btn_mypost,userinfo;
    private ImageView btn_message;
    private TextView user_name, user_info;
    private ImageView profile;

    private UserController userController;
    private int userId;
    public User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_user_personal,container,false);

        init(view);
        return view;
    }

    private void init(View view){
        btn_course = view.findViewById(R.id.per_my_course);
        btn_spdata = view.findViewById(R.id.per_sport_data);
        btn_spplan = view.findViewById(R.id.per_sport_plan);
        btn_mystar = view.findViewById(R.id.per_star_post);
        btn_mypost = view.findViewById(R.id.per_my_post);
        user_name = view.findViewById(R.id.username);
        user_info = view.findViewById(R.id.information);
        profile = view.findViewById(R.id.profile);
        btn_message = view.findViewById(R.id.personal_message);
        userinfo = view.findViewById(R.id.userinfo);

        btn_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                startActivity(intent);
            }
        });
        btn_spdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SportDataActivity.class);
                startActivity(intent);
            }
        });
        btn_spplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SportPlanActivity.class);
                startActivity(intent);
            }
        });
        btn_mystar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StarPostActivity.class);
                startActivity(intent);
            }
        });
        btn_mypost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyPostActivity.class);
                startActivity(intent);
            }
        });
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        userinfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ModifyActivity.class);
                startActivity(intent);
            }
        });
    }
}
