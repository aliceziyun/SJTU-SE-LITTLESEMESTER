package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.Controller.CourseController;
import com.example.myapplication.db.entity.Course;

import java.util.ArrayList;
import java.util.HashMap;

public class CourseListActivity extends AppCompatActivity {
    private ImageButton arrow_back, btn_search;
    private CourseController courseController;
    private ListView listView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        init();
    }

    private void init(){
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        userId = pref.getString("user_id", "");

        arrow_back = findViewById(R.id.course_back);
        btn_search = findViewById(R.id.course_search);
        listView = findViewById(R.id.list_view_my_course);

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourseListActivity.this.finish();
            }
        });

        courseController = new CourseController(CourseListActivity.this);
        ArrayList<Course> res = courseController.getFaovrCourse(userId);

        if (res != null){
            ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
            Log.d(TAG, "init: " + res.size());
            for (int i = 0; i < res.size(); i++){
                Course course = res.get(i);
                HashMap<String, Object> map = new HashMap<>();
                map.put("courseId", course.getId());
                if(course.getType().equals("yoga"))
                    map.put("videoImage", R.drawable.yoga);
                else if(course.getType().equals("hiit"))
                    map.put("videoImage", R.drawable.hilt);
                map.put("videoTitle", course.getCourseName());
                map.put("videoDuration", course.getDuration());
                map.put("videoDescription", course.getDescription());
                listItem.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(this,
                    //绑定的数据
                    listItem,
                    //每一行的布局
                    R.layout.video_display_item,
                    //动态数组中的数据源的键映射到布局文件对应的控件中
                    new String[]{"videoImage", "videoTitle", "videoDuration", "videoDescription"},
                    new int[]{R.id.video_image, R.id.video_title, R.id.video_duration, R.id.video_description});
            listView.setAdapter(adapter);

            // 为ListView添加点击事件
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(CourseListActivity.this, VideoActivity.class);
                    intent.putExtra("courseId", listItem.get(i).get("courseId").toString());
                    startActivity(intent);
//                    Toast.makeText(getContext(), "你点击了第" + i + "行", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
