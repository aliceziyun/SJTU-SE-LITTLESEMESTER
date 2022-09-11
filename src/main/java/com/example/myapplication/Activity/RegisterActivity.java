package com.example.myapplication.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.util.MD5Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private static final int REGISTER_SUCCESS = 0X02;
    private DatabaseReference mDatabase;

    private EditText et_username,et_pwd,et_pwd_sure;
    private Button register;
    private String userName,passWord,passWord_sure;
    private ImageView btn_back;

    private ValueEventListener valueEventListener;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_SUCCESS:
                    mDatabase.child("user").removeEventListener(valueEventListener);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        et_username = (EditText)findViewById(R.id.username);
        et_pwd = (EditText)findViewById(R.id.pwd);
        et_pwd_sure = (EditText)findViewById(R.id.pwd_confirm);
        register = (Button)findViewById(R.id.registerBtn);
        btn_back = (ImageView) findViewById(R.id.btn_register_back);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(passWord)) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(passWord_sure)) {
                    Toast.makeText(RegisterActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!passWord.equals(passWord_sure)) {
                    Toast.makeText(RegisterActivity.this, "两次输入密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                    /**
                     *保证用户名唯一，查看是否有此用户名
                     */
                } else {
                    mDatabase.child("username-to-id").child(userName).get() .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "网络故障！", Toast.LENGTH_SHORT).show();
                            }else {
                                Log.i("Register",String.valueOf(task.getResult().getValue()));
                                if(task.getResult().getValue() != null){
                                    Toast.makeText(RegisterActivity.this, "此账户名已经存在", Toast.LENGTH_SHORT).show();
                                }else{
                                    String key = mDatabase.child("user").push().getKey();
                                    User newUser = new User();
                                    newUser.setId(key);
                                    newUser.setUserName(userName);
                                    newUser.setPassword(MD5Utils.md5(passWord));
                                    mDatabase.child("user").child(key).setValue(newUser);
                                    mDatabase.child("username-to-id").child(userName).setValue(key);
                                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.clear();
                                    editor.putBoolean("save_pwd",true);
                                    editor.putString("user_name",userName);
                                    editor.putString("password",MD5Utils.md5(passWord));  //暂时不做加密
                                    editor.commit();
                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    RegisterActivity.this.finish();
                                }
                            }
                        }
                    });
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getEditString(){
        userName = et_username.getText().toString().trim();
        passWord = et_pwd.getText().toString().trim();
        passWord_sure = et_pwd_sure.getText().toString().trim();
    }
}
