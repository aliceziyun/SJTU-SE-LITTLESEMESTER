package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.db.Controller.CourseController;
import com.example.myapplication.db.entity.Course;
import com.example.myapplication.view.SeekRangeBar;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoSearchActivity extends Activity {
    private CourseController courseController;

    private ImageButton btn_start_search;
    private ImageButton btn_back;
    private TextView mTvMinWithoutRule;
    private TextView mTvMaxWithoutRule;
    private EditText et_search_input;
    private ListView lv_video_search;
    private int min = 0, max = 120;
    private CheckBox check_yoga, check_hiit, check_stretch, check_pamela;

    private SeekRangeBar doubleSeekbar;//双向进度条

    private ArrayList<Course> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_search);

        init();
    }
    private void init(){
        courseController = new CourseController(this);
        data = new ArrayList<>();
        btn_start_search = (ImageButton) findViewById(R.id.btn_start_search);
        btn_back = (ImageButton) findViewById(R.id.video_search_back);
        mTvMinWithoutRule = (TextView) findViewById(R.id.tv_min_without_rule);
        mTvMaxWithoutRule = (TextView) findViewById(R.id.tv_max_without_rule);
        check_yoga = (CheckBox) findViewById(R.id.check_yoga);
        check_hiit = (CheckBox) findViewById(R.id.check_hiit);
        check_stretch = (CheckBox) findViewById(R.id.check_stretch);
        check_pamela = (CheckBox) findViewById(R.id.check_pamela);
        et_search_input = (EditText) findViewById(R.id.et_search_input);
        lv_video_search = (ListView) findViewById(R.id.video_search_listview);
        doubleSeekbar = (SeekRangeBar) findViewById(R.id.seek_range_bar);

        btn_start_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.clear();
                String input = et_search_input.getText().toString();
                Log.i(TAG, "onClick: input "+ input);
                Log.i(TAG, "onClick: min " + String.valueOf(min));
                Log.i(TAG, "onClick: max " + String.valueOf(max));
                if (check_yoga.isChecked()){
                    ArrayList<Course> tmp = courseController.getCourseByCondition("yoga", min, max, input);
                    if (tmp != null){
                        data.addAll(tmp);
                    }

                }
                if (check_hiit.isChecked()){
                    ArrayList<Course> tmp = courseController.getCourseByCondition("hiit", min, max, input);
                    if (tmp != null){
                        data.addAll(tmp);
                    }
                }
                if (check_stretch.isChecked()){
                    ArrayList<Course> tmp = courseController.getCourseByCondition("stretch", min, max, input);
                    if (tmp != null){
                        data.addAll(tmp);
                    }
                }
                if (check_pamela.isChecked()){
                    ArrayList<Course> tmp = courseController.getCourseByCondition("pamela", min, max, input);
                    if (tmp != null){
                        data.addAll(tmp);
                    }
                }

                if (data != null){
                    ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++){
                        Course course = data.get(i);
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
                    SimpleAdapter adapter = new SimpleAdapter(VideoSearchActivity.this,
                            //绑定的数据
                            listItem,
                            //每一行的布局
                            R.layout.video_display_item,
                            //动态数组中的数据源的键映射到布局文件对应的控件中
                            new String[]{"videoImage", "videoTitle", "videoDuration", "videoDescription"},
                            new int[]{R.id.video_image, R.id.video_title, R.id.video_duration, R.id.video_description});
                    lv_video_search.setAdapter(adapter);

                    // 为ListView添加点击事件
                    lv_video_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(VideoSearchActivity.this, VideoActivity.class);
                            intent.putExtra("courseId", listItem.get(i).get("courseId").toString());
                            startActivity(intent);
                        }
                    });
                }


            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        doubleSeekbar.setOnSeekBarChangeListener(new SeekRangeBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekRangeBar seekBar, double progressLow, double progressHigh) {
                Log.d("LOGCAT","低：" + progressLow + "高：" + progressHigh);
                mTvMinWithoutRule.setText(String.valueOf((int)progressLow)+"min");
                mTvMaxWithoutRule.setText(String.valueOf((int)progressHigh)+"min");
                min = (int)progressLow;
                max = (int)progressHigh;
            }
        });

    }
}
