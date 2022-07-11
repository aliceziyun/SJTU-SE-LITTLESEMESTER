package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Controller.CourseController;
import com.example.myapplication.R;
import com.example.myapplication.adapter.SportFragmentAdapter;
import com.example.myapplication.entity.Course;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Exercise extends Fragment {
    private CourseController courseController;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //标题数组
    private String[] titles = {"运动","跑步"};

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_exercise,
                container, false);

        courseController = new CourseController(getContext());
        Course course = new Course();
        ArrayList<Course> res = courseController.getCourseByType("yoga");
        if (res == null){
            course = courseController.packCourse("瑜伽1", "这是瑜伽1", 20, "yoga",
                    1200, "111", "222");
            courseController.addCourse(course);
            course = courseController.packCourse("瑜伽2", "这是瑜伽2", 20, "yoga",
                    1200, "222", "222");
            courseController.addCourse(course);
            course = courseController.packCourse("瑜伽3", "这是瑜伽3", 20, "yoga",
                    1200, "333", "333");
            courseController.addCourse(course);
            course = courseController.packCourse("hiit1", "这是hiit1", 20, "hiit",
                    2000, "111", "111");
            courseController.addCourse(course);
            course = courseController.packCourse("hiit2", "这是hiit2", 20, "hiit",
                    1900, "222", "222");
            courseController.addCourse(course);
            course = courseController.packCourse("hiit3", "这是hiit3", 20, "hiit",
                    1800, "333", "333");
            courseController.addCourse(course);

            course = courseController.packCourse("hiit4", "这是hiit4", 20, "hiit",
                    1700, "333", "333");
            courseController.addCourse(course);

            course = courseController.packCourse("hiit5", "这是hiit5", 20, "hiit",
                    1500, "333", "333");
            courseController.addCourse(course);
            course = courseController.packCourse("hiit6", "这是hiit6", 20, "hiit",
                    1200, "333", "333");
            courseController.addCourse(course);
            Toast.makeText(getContext(), "add success", Toast.LENGTH_SHORT).show();
        }

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SportFragment());
        fragmentList.add(new RunFragment());

        SportFragmentAdapter adapter = new SportFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }
}
