package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SportDataActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private Button daliy_detail, weekly_detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_data);
        init();
    }

    private void init(){
        btn_back = findViewById(R.id.history_title_back);
        daliy_detail = findViewById(R.id.daliy_detail);
        weekly_detail = findViewById(R.id.weekly_detail);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SportDataActivity.this.finish();
            }
        });
        daliy_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SportDataActivity.this,DetailedActivity.class);
                startActivity(intent);
            }
        });
        weekly_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SportDataActivity.this,DetailedActivity.class);
                startActivity(intent);
            }
        });
    }
}
