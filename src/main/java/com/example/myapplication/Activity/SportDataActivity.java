package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.Controller.SportsDataController;
import com.example.myapplication.db.Controller.SportsPlanController;
import com.example.myapplication.db.entity.SportsData;
import com.example.myapplication.db.entity.SportsType;
import com.example.myapplication.util.DateHelper;
import com.google.android.gms.common.internal.StringResourceValueReader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SportDataActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private ImageButton daliy_detail, weekly_detail;
    private TextView dayConsumeText;
    private TextView dayTotalTimeText;
    private TextView distanceToGoalText;
    private TextView weekConsumeText;
    private TextView weekTotalTimeText, history_title_text;
    private TextView goalReachDayText;
    private ArrayList<SportsData> dayList;
    private ArrayList<SportsData> weekList;
    private ArrayList<SportsData> runningList;
    private ArrayList<SportsData> fitnessList;
    private ArrayList<SportsData> yogaList;
    private String userId;
    private TextView sports_data_total, running_history_btn_hour, running_history_btn_min, running_history_btn_second;
    private TextView fitness_data_total, fitness_history_btn_hour, fitness_history_btn_min, fitness_history_btn_second;
    private TextView yoga_data_total, yoga_history_btn_hour, yoga_history_btn_min, yoga_history_btn_second;

    private DateHelper dh;
    private SportsPlanController sportsPlanController;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_data);
        init();
    }

    private void init() {
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);     //获取当前用户id
        userId = pref.getString("user_id","");

        //初始化
        SportsDataController sportsDataController = new SportsDataController(getApplicationContext());
        sportsPlanController = new SportsPlanController(this);
        dayList = sportsDataController.getRecordByKind(SportsType.DAY, userId);
        weekList = sportsDataController.getRecordByKind(SportsType.WEEK, userId);
        runningList = sportsDataController.getRecordByKind(SportsType.RUNNING, userId);
        fitnessList = sportsDataController.getRecordByKind(SportsType.FITNESS, userId);
        yogaList = sportsDataController.getRecordByKind(SportsType.YOGA, userId);
        dh = new DateHelper();

        //初始化文本
        dayConsumeText = (TextView) findViewById(R.id.total_consume_text);
        dayTotalTimeText = (TextView) findViewById(R.id.day_total_time_text);
        distanceToGoalText = (TextView) findViewById(R.id.goal_distance_text);
        weekConsumeText = (TextView) findViewById(R.id.week_total_consume_text);
        weekTotalTimeText = (TextView) findViewById(R.id.week_total_time_text);
        goalReachDayText = (TextView) findViewById(R.id.goal_reach_day_text);
        history_title_text = (TextView)findViewById(R.id.history_title_text) ;
        sports_data_total = (TextView)findViewById(R.id.sports_data_total);
        running_history_btn_hour = (TextView)findViewById(R.id.running_history_btn_hour);
        running_history_btn_min = (TextView)findViewById(R.id.running_history_btn_min);
        running_history_btn_second = (TextView)findViewById(R.id.running_history_btn_second);
        fitness_data_total = (TextView)findViewById(R.id.fitness_data_total);
        fitness_history_btn_hour = (TextView)findViewById(R.id.fitness_history_btn_hour);
        fitness_history_btn_min = (TextView)findViewById(R.id.fitness_history_btn_min);
        fitness_history_btn_second = (TextView)findViewById(R.id.fitness_history_btn_second);
        yoga_data_total = (TextView)findViewById(R.id.yoga_data_total);
        yoga_history_btn_hour = (TextView)findViewById(R.id.yoga_history_btn_hour);
        yoga_history_btn_min = (TextView)findViewById(R.id.yoga_history_btn_min);
        yoga_history_btn_second = (TextView)findViewById(R.id.yoga_history_btn_second);

        //设置字体
        Typeface history_title_text_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        history_title_text.setTypeface(history_title_text_typeface);
        Typeface sports_data_total_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        sports_data_total.setTypeface(sports_data_total_typeface);
        Typeface running_history_btn_hour_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        running_history_btn_hour.setTypeface(running_history_btn_hour_typeface);
        Typeface running_history_btn_min_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        running_history_btn_min.setTypeface(running_history_btn_min_typeface);
        Typeface running_history_btn_second_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        running_history_btn_second.setTypeface(running_history_btn_second_typeface);
        Typeface fitness_data_total_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        fitness_data_total.setTypeface(fitness_data_total_typeface);
        Typeface fitness_history_btn_hour_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        fitness_history_btn_hour.setTypeface(fitness_history_btn_hour_typeface);
        Typeface fitness_history_btn_min_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        fitness_history_btn_min.setTypeface(fitness_history_btn_min_typeface);
        Typeface fitness_history_btn_second_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        fitness_history_btn_second.setTypeface(fitness_history_btn_second_typeface);
        Typeface yoga_data_total_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        yoga_data_total.setTypeface(yoga_data_total_typeface);
        Typeface yoga_history_btn_hour_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        yoga_history_btn_hour.setTypeface(yoga_history_btn_hour_typeface);
        Typeface yoga_history_btn_min_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        yoga_history_btn_min.setTypeface(yoga_history_btn_min_typeface);
        Typeface yoga_history_btn_second_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
        yoga_history_btn_second.setTypeface(yoga_history_btn_second_typeface);

        //初始化按钮
        LinearLayout layout_running_record = (LinearLayout) findViewById(R.id.layout_running_history);
        String[] runningStore = sportsDataController.calculateTotal(runningList)[1].split(":");
        running_history_btn_hour.setText(runningStore[0]);
        running_history_btn_min.setText(runningStore[1]);
        running_history_btn_second.setText(runningStore[2]);
        layout_running_record.setOnClickListener(this::onClick);


        LinearLayout layout_fitness_record = (LinearLayout) findViewById(R.id.layout_fitness_history);
        String fitnessStore[] = sportsDataController.calculateTotal(fitnessList)[1].split(":");
        fitness_history_btn_hour.setText(fitnessStore[0]);
        fitness_history_btn_min.setText(fitnessStore[1]);
        fitness_history_btn_second.setText(fitnessStore[2]);
        layout_fitness_record.setOnClickListener(this::onClick);

        LinearLayout layout_yoga_record = (LinearLayout) findViewById(R.id.layout_yoga_history);
        String[] yogaStore = sportsDataController.calculateTotal(yogaList)[1].split(":");
        yoga_history_btn_hour.setText(yogaStore[0]);
        yoga_history_btn_min.setText(yogaStore[1]);
        yoga_history_btn_second.setText(yogaStore[2]);
        layout_yoga_record.setOnClickListener(this::onClick);

        LinearLayout layout_day_record = (LinearLayout) findViewById(R.id.layout_day_history);
        layout_day_record.setOnClickListener(this::onClick);

        LinearLayout layout_week_record = (LinearLayout) findViewById(R.id.layout_week_history);
        layout_week_record.setOnClickListener(this::onClick);

        //设置日文本
        String[] dayTotal = sportsDataController.calculateTotal(dayList);
        String dayConsume = dayTotal[0] + " " + "kcal";
        Log.i("HistoryActivity", dayConsume);
        dayConsumeText.setText(dayConsume);
        String dayTotalTime = dayTotal[1];
        dayTotalTimeText.setText(dayTotalTime);


        // 设置日距离目标
        // 获取今天星期几
        Date today = new Date();
        String weekDay = dh.getWeekOfDate(today);
//        Log.i(TAG, "init: weekDay " + weekDay);
        int dayGoal = sportsPlanController.getKcalByDay(weekDay, userId);
        int consume = Integer.valueOf(dayTotal[0]);
//        Log.i(TAG, "init: "+ String.valueOf(consume));
        int distance = consume < dayGoal? dayGoal - consume : 0;
        distanceToGoalText.setText(String.valueOf(distance));
        

        //设置周文本
        String[] weekTotal = sportsDataController.calculateTotal(weekList);
        String weekConsume = weekTotal[0] + " " + "kcal";
        weekConsumeText.setText(weekConsume);
        String weekTotalTime = weekTotal[1];
        weekTotalTimeText.setText(weekTotalTime);

        //设置周达标天数
        int goalReachDay = sportsPlanController.getGoalReachDay(userId);
        goalReachDayText.setText(String.valueOf(goalReachDay));

        //设置按钮
        btn_back = findViewById(R.id.history_title_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SportDataActivity.this.finish();
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.layout_running_history: {
                Log.i("HistoryActivity", "running");
                Intent intent = new Intent(this,DetailedActivity.class);
                intent.putExtra("sports_list", runningList);
                startActivity(intent);
                break;
            }
            case R.id.layout_fitness_history: {
                Log.i("HistoryActivity", "fitness");
                Intent intent = new Intent(this,DetailedActivity.class);
                intent.putExtra("sports_list", fitnessList);
                startActivity(intent);
                break;
            }
            case R.id.layout_yoga_history:{
                Log.i("HistoryActivity", "yoga");
                Intent intent = new Intent(this,DetailedActivity.class);
                intent.putExtra("sports_list", yogaList);
                startActivity(intent);
                break;
            }
            case R.id.layout_day_history:{
                Log.i("HistoryActivity", "day");
                Intent intent = new Intent(this,DetailedActivity.class);
                intent.putExtra("sports_list", dayList);
                startActivity(intent);
                break;
            }
            case R.id.layout_week_history:{
                Log.i("HistoryActivity", "week");
                Intent intent = new Intent(this,DetailedActivity.class);
                intent.putExtra("sports_list", weekList);
                startActivity(intent);
                break;
            }
            default:
        }
    }
}
