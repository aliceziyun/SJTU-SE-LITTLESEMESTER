package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.adapter.SportFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.R;

public class SportFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    //标题数组
    private String[] titles = {"yoga","hiit","pamela","stretch"};

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sport,
                container, false);
        tabLayout = view.findViewById(R.id.sport_tab_layout);
        viewPager = view.findViewById(R.id.sport_view_pager);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SportYogaFragment());
        fragmentList.add(new SportHiitFragment());
        fragmentList.add(new SportPamelaFragment());
        fragmentList.add(new SportStretchFragment());

        SportFragmentAdapter adapter = new SportFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
