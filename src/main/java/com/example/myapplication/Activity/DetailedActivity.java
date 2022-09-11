package com.example.myapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.entity.SportsData;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {
    private ImageButton arrow_back;
    private ListView sportsDataListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Intent intent = getIntent();
        ArrayList<SportsData> sportsDataList = intent.getParcelableArrayListExtra("sports_list");
        if(sportsDataList != null){
            Log.i("posData", String.valueOf(sportsDataList.get(0).kcal));
            sportsDataListView = findViewById(R.id.list_view);
            ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
            for(int i = 0; i < sportsDataList.size();i++){
                HashMap<String, String> map = new HashMap<>();
                SportsData tmp = sportsDataList.get(i);
                map.put("courseNameText",tmp.courseName);
                map.put("countText",tmp.startTime);
                map.put("timeText","运动时长：" + String.valueOf(tmp.duration)+" 秒");
                map.put("kcalText","消耗卡路里数：" + String.valueOf(tmp.kcal)+ " kcal");
                listItem.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(this,
                    //绑定的数据
                    listItem,
                    //每一行的布局
                    R.layout.sports_data_item,
                    //动态数组中的数据源的键映射到布局文件对应的控件中
                    new String[] {"courseNameText", "countText","timeText","kcalText"},
                    new int[] {R.id.course_name_text, R.id.count_text,R.id.time_text,R.id.kcal_text});
            sportsDataListView.setAdapter(adapter);
        }

        init();
    }

    private void init(){
        arrow_back = findViewById(R.id.detail_back);
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailedActivity.this.finish();
            }
        });
    }
}
