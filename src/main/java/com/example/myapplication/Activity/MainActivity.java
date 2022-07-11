package com.example.myapplication.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Fragment.Exercise;
import com.example.myapplication.Fragment.HomePage;
import com.example.myapplication.Fragment.Social;
import com.example.myapplication.Fragment.UserPersonal;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment personal_fragment;
    private Fragment social_fragment;
    private Fragment home_fragment;
    private Fragment exercise_fragment;
    private Fragment[] fragments;
    private int lastfragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personal_fragment = new UserPersonal();
        exercise_fragment = new Exercise();
        social_fragment = new Social();
        home_fragment = new HomePage();
        fragments = new Fragment[]{home_fragment,exercise_fragment,social_fragment,personal_fragment};
        initFragment();
    }

    @Override
    protected void onStart(){
        super.onStart();
        reloadFragment(lastfragment);
        Log.d("TAG","here");
    }

    private void initFragment() {
        Log.d("INIT",String.valueOf(lastfragment));
        lastfragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, home_fragment).show(home_fragment).commit();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(changFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener changFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Log.d("SWITCH",String.valueOf(lastfragment));
            switch (menuItem.getItemId()) {
                case R.id.tohome: {
                    if (lastfragment != 0) {
                        switchFragment(lastfragment, 0,"0");
                        lastfragment = 0;
                    }
                    return true;
                }
                case R.id.exercise:{
                    if(lastfragment != 1){
                        switchFragment(lastfragment,1, "1");
                        lastfragment = 1;
                    }
                    return true;
                }
                case R.id.social: {
                    if (lastfragment != 2) {
                        switchFragment(lastfragment, 2, "2");
                        lastfragment = 2;
                    }
                    return true;
                }
                case R.id.personal: {
                    if (lastfragment != 3) {
                        switchFragment(lastfragment, 3, "3");
                        lastfragment = 3;
                    }
                    return true;
                }
            }
            return false;
        }
    };

    private void switchFragment(int lastfragment, int index, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);
        if(fragments[index].isAdded()==false){
            transaction.add(R.id.frame_fragment,fragments[index]);
            transaction.addToBackStack(tag);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

    private void reloadFragment(int lastfragment){
        Log.d("RELOAD",String.valueOf(lastfragment));
        switch(lastfragment){
            case 0:{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, home_fragment).show(home_fragment).commit();
                break;
            }
            case 1:{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, exercise_fragment).show(exercise_fragment).commit();
                break;
            }
            case 2:{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, social_fragment).show(social_fragment).commit();
                break;
            }
            case 3:{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, personal_fragment).show(personal_fragment).commit();
            }
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(changFragment);
    }
}
