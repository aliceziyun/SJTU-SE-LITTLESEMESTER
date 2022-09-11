package com.example.myapplication.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Activity.VideoSearchActivity;
import com.example.myapplication.adapter.SportFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.R;

public class SportFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private ImageButton btn_search_course;

    private View searchView;
    private boolean isUp;

    //标题数组
    private String[] titles = {"yoga","hiit","pamela","stretch"};

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("sport", "onCreateView: create sportfrag");
        final View view = inflater.inflate(R.layout.fragment_sport,
                container, false);
        init(view);
//        setSlideListener();
        return view;
    }

    private void init(View view){
        tabLayout = view.findViewById(R.id.sport_tab_layout);
        viewPager = view.findViewById(R.id.sport_view_pager);
        btn_search_course = (ImageButton) view.findViewById(R.id.course_search);
        searchView = (View) view.findViewById(R.id.search_view);

        fragmentList = new ArrayList<>();
        fragmentList.add(new SportYogaFragment());
        fragmentList.add(new SportHiitFragment());
        fragmentList.add(new SportPamelaFragment());
        fragmentList.add(new SportStretchFragment());

        SportFragmentAdapter adapter = new SportFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

//        searchView.setVisibility(View.INVISIBLE);
//        isUp = false;
        btn_search_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: here");
                Intent intent = new Intent(getContext(),  VideoSearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentList){
            transaction.remove(fragment);
        }
        transaction.commitAllowingStateLoss();
        Log.d("tag", "onDestroyView: sport");
    }

    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


}
