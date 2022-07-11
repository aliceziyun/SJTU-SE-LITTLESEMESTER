package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Fragment.UserPersonal;
import com.example.myapplication.R;

public class CourseListActivity extends AppCompatActivity {
    private ImageButton arrow_back, btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        init();
    }

    private void init(){
        arrow_back = findViewById(R.id.course_back);
        btn_search = findViewById(R.id.course_search);
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseListActivity.this.finish();
            }
        });
    }
}
