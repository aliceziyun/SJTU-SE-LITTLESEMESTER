package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Activity.VideoActivity;
import com.example.myapplication.db.Controller.CourseController;
import com.example.myapplication.db.entity.Course;
import com.example.myapplication.util.DateHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class SportPamelaFragment extends Fragment {
    private CourseController courseController;
    private ListView listView;
    private DateHelper dh;

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_sport_pamela,
                container, false);

        courseController = new CourseController(getContext());
        dh = new DateHelper();
        ArrayList<Course> res = courseController.getCourseByType("pamela");
        if (res != null){
            listView = view.findViewById(R.id.pamela_listview);
            ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
            for (int i = 0; i < res.size(); i++){
                Course course = res.get(i);
                HashMap<String, Object> map = new HashMap<>();
                map.put("courseId", course.getId());
                map.put("videoImage", R.drawable.pamela);
                map.put("videoTitle", course.getCourseName());
                map.put("videoDuration", "时长：" + dh.secToTime(course.getDuration()));
                map.put("videoDescription", course.getDescription());
                listItem.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(getContext(),
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
                    Intent intent = new Intent(getContext(), VideoActivity.class);
                    intent.putExtra("courseId", listItem.get(i).get("courseId").toString());
                    startActivity(intent);
//                    Toast.makeText(getContext(), "你点击了第" + i + "行", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}
