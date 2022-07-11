package com.example.myapplication.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Fragment.Comments;
import com.example.myapplication.Fragment.Likes;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MessageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment likes_fragment, comments_fragment;
    private Fragment[] fragments;
    private int lastfragment;
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btn_back = findViewById(R.id.message_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.this.finish();
            }
        });

        likes_fragment = new Likes();
        comments_fragment = new Comments();
        fragments = new Fragment[]{likes_fragment,comments_fragment};
        initFragment();
    }

    private void initFragment(){
        lastfragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.message_fragment, likes_fragment).show(likes_fragment).commit();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.message_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(changFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener changFragment = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.likes:{
                    if (lastfragment != 0) {
                        switchFragment(lastfragment, 0,"0");
                        lastfragment = 0;
                    }
                    return true;
                }
                case R.id.comments:{
                    if(lastfragment != 1){
                        switchFragment(lastfragment,1,"1");
                        lastfragment = 1;
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
            transaction.add(R.id.message_fragment,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }
}
