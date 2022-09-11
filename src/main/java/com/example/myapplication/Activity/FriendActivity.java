package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AddFriendRecyclerAdapter;
import com.example.myapplication.db.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FriendActivity extends BaseActivity {

    // 信号常量
    private static final int Get_Fans_Success = 0x11;
    private static final int Get_Query_Follow_Success = 0x12;
    private static final int Get_Current_Follow_Success = 0x15;

    private ImageView btn_back;
    private TextView titleText;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;

    private int sign;//1为粉丝，0为关注，2为排名
    private String userId;
    private String username = null;

    private AddFriendRecyclerAdapter adapter;
    private List<User> data = new ArrayList<>(); // 传给适配器的数据
    private List<User> queryFollowData = new ArrayList<>(); // 查询用户关注列表
    private List<User> fanData = new ArrayList<>(); // 当前用户粉丝列表
    private List<User> followData = new ArrayList<>(); // 当前用户关注列表
    private User queryUser; // 要查询关注/粉丝的用户
    private User user; // 当前用户

    public static FriendActivity FriendActivity_instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
//        ActivityManager.getInstance().pushOneActivity(this);

        FriendActivity_instance = this;

        // 要查询的用户id和用户名
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userId = bundle.getString("userId");
        username = bundle.getString("userName");
        sign = bundle.getInt("sign", 0);
        queryUser = new User();
        queryUser.setId(userId);
        queryUser.setUserName(username);

        // 当前用户
        user = new User();
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        String userId = pref.getString("user_id", "");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(FriendActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                else {
                    user = task.getResult().getValue(User.class);
                    initComponent();
                    getCurrentUserFollow();
                }
            }
        });
    }


    private void initComponent(){
        btn_back = (ImageView) findViewById(R.id.friend_back_button);
        titleText = (TextView) findViewById(R.id.friend_title_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_friend);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AddFriendRecyclerAdapter(user, this, 0);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Get_Current_Follow_Success:
                    if (sign == 1) { //粉丝
                        titleText.setText(username+"的粉丝");
                        getFans();
                    } else if(sign == 0) {//关注
                        titleText.setText(username+"的关注");
                        if (user.equals(queryUser)){
                            Collections.sort(followData,new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) {
                                    return o2.getTotalTime() - o1.getTotalTime();
                                }
                            });
                            adapter.setData(followData);
                            adapter.setFollowData(followData);
                            adapter.notifyDataSetChanged();
                            Log.i(TAG, "handleMessage: " + String.valueOf(followData.size()));
                            closeProgressDialog();
                        }
                        else{
                            // 应该不用写
                        }

                    }
                    break;
//                case Get_Query_Follow_Success:
//                    data = queryFollowData;
//                    adapter.setData(queryFollowData);
//                    adapter.setFollowData(followData);
//                    adapter.notifyDataSetChanged();
//                    break;

                case Get_Fans_Success:
                    Log.i("TAG", "粉丝");
                    data = fanData;
                    adapter.setData(data);
                    adapter.setFollowData(followData);
                    adapter.notifyDataSetChanged();
                    closeProgressDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void getQueryUserFollow(){
        Query followQuery = mDatabase.child("friend").child(String.valueOf(queryUser.getId())).child("follow");
        followQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    for (DataSnapshot item: task.getResult().getChildren()) {
                        // TODO: handle the post
                        String value = String.valueOf(item.getValue());
                        String key = String.valueOf(item.getKey());
                        if (value == "true"){
                            mDatabase.child("user").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(FriendActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        queryFollowData.add(task.getResult().getValue(User.class));
                                    }
                                }
                            });
                        }
                    }
                    handler.sendEmptyMessage(Get_Query_Follow_Success);
                }
            }
        });
    }

//
    private void getCurrentUserFollow(){
        showProgressDialog(this, "加载中");
        Query currentFollowQuery = mDatabase.child("friend").child(String.valueOf(user.getId())).child("follow");
        currentFollowQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
//                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    followData.clear();
                    int count = (int)task.getResult().getChildrenCount();
                    for (DataSnapshot item: task.getResult().getChildren()) {
                        // TODO: handle the post
                        String value = String.valueOf(item.getValue());
                        String key = String.valueOf(item.getKey());
                        if (value == "true"){
                            Log.i(TAG, "onComplete: hhhh");
                            mDatabase.child("user").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        followData.add(task.getResult().getValue(User.class));
                                        sendMessage(count, followData.size(), Get_Current_Follow_Success);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void getFans(){
        Query fanQuery = mDatabase.child("friend").child(String.valueOf(queryUser.getId())).child("fans");
        fanQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fanData.clear();
                int count = (int)snapshot.getChildrenCount();
                for (DataSnapshot item: snapshot.getChildren()){
                    String value = String.valueOf(item.getValue());
                    String key = String.valueOf(item.getKey());

                    if (value == "true"){
                        mDatabase.child("user").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(FriendActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    fanData.add(task.getResult().getValue(User.class));
                                    sendMessage(count, fanData.size(), Get_Fans_Success);
                                }
                            }
                        });
                    }
                }
//                handler.sendEmptyMessage(Get_Fans_Success);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public AddFriendRecyclerAdapter getAdapter(){
        return this.adapter;
    }

    private void sendMessage(int goal, int current, int msg){
        if (goal == current){
            handler.sendEmptyMessage(msg);
        }
    }
}
