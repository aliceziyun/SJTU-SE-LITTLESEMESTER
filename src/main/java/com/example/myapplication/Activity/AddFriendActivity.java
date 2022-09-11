package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AddFriendRecyclerAdapter;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.util.InitDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AddFriendActivity extends AppCompatActivity {

    private EditText edit_username;
    private ImageView imageView_search;
    private DatabaseReference mDatabase;
    private ImageView iv_back;
    private User user;

    private AddFriendRecyclerAdapter adapter;
    public static AddFriendActivity AddFriendActivity_instance;
    private RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        InitDatabase initDatabase = new InitDatabase();
        mDatabase = InitDatabase.getDatabase().getReference();

        user = new User();
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        String userId = pref.getString("user_id", "");
        Log.i(TAG, "onCreate: " + userId);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(AddFriendActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                else {
                    user = task.getResult().getValue(User.class);
                    Log.i(TAG, "onComplete: here");
                    initComponent();
                }
            }
        });
    }

    private void initComponent(){
        AddFriendActivity_instance = this;
        adapter = new AddFriendRecyclerAdapter(user, this, 1);

        edit_username = findViewById(R.id.edit_search_username);
        imageView_search = findViewById(R.id.iv_start_search);
        recyclerView = findViewById(R.id.recycler_add_friend);
        iv_back = findViewById(R.id.iv_add_friend_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.getCurrentFollowData(user);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // 修改回车键
        edit_username.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    Search();
                }
                return false;
            }
        });

        imageView_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void Search(){
        adapter.clearData();
        Log.i(TAG, "Search: " + edit_username.getText().toString());
        String inputUserName = edit_username.getText().toString();
        mDatabase.child("username-to-id").child(inputUserName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String userId = String.valueOf(task.getResult().getValue());
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Log.i(TAG, "onComplete: userid:" + userId);
                    if (userId == "null"){
                        // 显示搜索的用户不存在
                        Log.i(TAG, "onComplete: 用户不存在");
                    }
                    else{
//                        adapter.getCurrentFollowData(user);
                        adapter.getUserById(userId);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public AddFriendRecyclerAdapter getAdapter(){
        return this.adapter;
    }

}
