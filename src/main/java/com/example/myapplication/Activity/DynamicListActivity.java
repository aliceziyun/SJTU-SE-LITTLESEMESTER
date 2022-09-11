package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AddFriendRecyclerAdapter;
import com.example.myapplication.adapter.DynamicRecyclerAdapter;
import com.example.myapplication.db.entity.Dynamic;
import com.example.myapplication.db.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class DynamicListActivity extends BaseActivity {
    private static final int FIRST_LOAD_DYNAMIC = 0X1;
    private static final int DYNAMIC_SIZE = 5;

    private User user;
    private String userId;
    private DatabaseReference mDatabase;

    private DynamicRecyclerAdapter adapter;
    private ArrayList<Dynamic> data = new ArrayList<Dynamic>(); //动态数据
    private ArrayList<Dynamic> likesData = new ArrayList<Dynamic>(); //动态数据
    private ArrayList<String> likes = new ArrayList<String>();
    private ArrayList<String> myDynamic = new ArrayList<String>();
    public static DynamicListActivity DynamicListActivity_instance;
    private RecyclerView recyclerView;
    private ImageView iv_back;
    private int lastVisibleItemPosition = 0;
    private int position = 0;
    private boolean firstIn = true;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case FIRST_LOAD_DYNAMIC:
                    adapter.setData(data);
                    adapter.setLikes(likes);
                    adapter.notifyDataSetChanged();
                    firstIn = false;
                    closeProgressDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_list);

        data = new ArrayList<>();
        user = new User();
        SharedPreferences pref = getSharedPreferences("LogInfo", MODE_PRIVATE);
        userId = pref.getString("user_id", "");
        Log.i(TAG, "onCreate: " + userId);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).child("dynamic_published").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(DynamicListActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot item: task.getResult().getChildren()) {
                        String value = String.valueOf(item.getValue());
                        String key = String.valueOf(item.getKey());
                        Log.i("DynamicList",key+value);
                        myDynamic.add(key);
                    }
                    getLike();
                }
            }
        });
    }

    private void getLike(){
        mDatabase.child("user").child(userId).child("likeDynamic").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(DynamicListActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                } else {
                    for (DataSnapshot item: task.getResult().getChildren()) {
                        String value = String.valueOf(item.getValue());
                        String key = String.valueOf(item.getKey());
                        likes.add(key);
                    }
                    initComponent();
                }
            }
        });
    }

    private void initComponent() {
        DynamicListActivity_instance = this;
        adapter = new DynamicRecyclerAdapter(getApplicationContext(),DynamicListActivity.this);

        recyclerView = findViewById(R.id.recycler_dynamic_list);
        iv_back = findViewById(R.id.dynamic_list_back_btn);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        adapter = new DynamicRecyclerAdapter(getApplicationContext(),DynamicListActivity.this);
        recyclerView.setAdapter(adapter);

        initLoadDynamic();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.i("SocialFragment", String.valueOf(adapter.getItemCount()));
                super.onScrollStateChanged(recyclerView, newState);

                if(lastVisibleItemPosition == 0 && adapter.getItemCount() == 0){
                    adapter.setIsLoadMore(true);
                    loadDynamic();
                }else if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    //显示加载更多布局
                    adapter.setIsLoadMore(true);
                    loadDynamic();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                Log.i("SocialFragment", String.valueOf(lastVisibleItemPosition));
            }
        });
    }

    private void initLoadDynamic(){
        showProgressDialog(DynamicListActivity.this,"加载动态中……");
        loadDynamic();
    }

    private void loadDynamic() {
        Query dynamicQuery =  mDatabase.child("dynamic");
        dynamicQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task){
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    Log.i("SocialFragment", "dynamic"+String.valueOf(myDynamic.size()));
                    if(position == myDynamic.size()){
                        Toast.makeText(DynamicListActivity.this,"已到最底部",Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i = 0;i < DYNAMIC_SIZE;i++){
                            if(position < myDynamic.size()){
                                String timeline = myDynamic.get(position);
                                data.add(dataSnapshot.child(timeline).getValue(Dynamic.class));
                                Log.i("SocialFragment",position +String.valueOf(dataSnapshot.child(timeline).getValue(Dynamic.class)));
                                position++;
                            }
                        }
                    }
                    Log.i("ListTest", "data"+String.valueOf(likes.size()));


                    if(firstIn){
                        handler.sendEmptyMessage(FIRST_LOAD_DYNAMIC);
                    }else{
                        adapter.setIsLoadMore(false);
                        adapter.setData(data);
                        adapter.setLikes(likes);
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(DynamicListActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
