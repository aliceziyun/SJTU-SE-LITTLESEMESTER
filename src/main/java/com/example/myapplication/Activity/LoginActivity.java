package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.util.InitDatabase;
import com.example.myapplication.util.MD5Utils;
import com.example.myapplication.view.PrivacyDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.PasswordAuthentication;
import java.util.Iterator;

public class LoginActivity extends BaseActivity{
    private static final int LOGIN_SUCCESS = 0X01;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor = null;

    private Button login;
    private TextView tv_register,tv_privacy;
    private EditText et_username,et_pwd;
    private CheckBox save_pwd,save_privacy;
    private String userName,passWord,spPsw;

    private ValueEventListener valueEventListener;
    private DatabaseReference mDatabase;

    private PrivacyDialog privacyDialog;
    private boolean isPwdEdit;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    closeProgressDialog();
                    mDatabase.child("user").removeEventListener(valueEventListener);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitDatabase initDatabase = new InitDatabase();
        mDatabase = initDatabase.getDatabase().getReference();

        init();
    }

    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        login = (Button) findViewById(R.id.loginBtn);
        tv_register = (TextView) findViewById(R.id.register_acc);
        tv_privacy = (TextView) findViewById(R.id.privacy_agreement_text);
        et_username = (EditText) findViewById(R.id.username);
        et_pwd = (EditText) findViewById(R.id.pwd);
        save_pwd = (CheckBox) findViewById(R.id.save_pwd);
        save_privacy = (CheckBox) findViewById(R.id.privacy_agreement_checkbox);
        save_privacy.setChecked(true);
        isPwdEdit = false;

        boolean isRemember = pref.getBoolean("save_pwd",false);
        if(isRemember){
            //将账号和密码都设置到文本框中
            String account = pref.getString("user_name","");
            String password = pref.getString("password","");
            Log.i(TAG, "init: savedpass " + password);
            et_username.setText(account);
            et_pwd.setText(password);
            save_pwd.setChecked(true);

        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(save_privacy.isChecked()){
                    getEditString();
                    if (TextUtils.isEmpty(userName)) {
                        Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(passWord)) {
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    } else {
                        showProgressDialog(LoginActivity.this, "验证用户中");
                        String md5Psw;
                        if (isRemember) {
                            if (checkIsSame()) {
                                if (isPwdEdit) {
                                    md5Psw = MD5Utils.md5(passWord);
                                } else {
                                    md5Psw = pref.getString("password", "");
                                }
                            } else {
                                md5Psw = MD5Utils.md5(passWord);
                            }
                        } else {
                            if (checkIsSame()) {
                                md5Psw = passWord;
                            } else {
                                md5Psw = MD5Utils.md5(passWord);
                            }
                        }

//                    String md5Psw= isRemember? passWord : MD5Utils.md5(passWord);

                        Log.i(TAG, "onClick: password " + md5Psw);
                        valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    Toast.makeText(LoginActivity.this, "不存在该用户", Toast.LENGTH_SHORT).show();
                                    closeProgressDialog();
                                } else {
                                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                    Iterator it = children.iterator();
                                    DataSnapshot userData = (DataSnapshot) it.next();
                                    String userId = userData.getKey();
                                    User user = dataSnapshot.child(userId).getValue(User.class);
                                    Log.i(TAG, "onDataChange: userPassword " + user.getPassword());
                                    if (md5Psw.equals(user.getPassword())) {
                                        handler.sendEmptyMessage(LOGIN_SUCCESS);
                                        Log.i(TAG, "onDataChange: password" + md5Psw);
                                        if (save_pwd.isChecked()) {   //复选框被选中
                                            //将用户名和密码存入SharedPreferences中
                                            saveLoginStatus(userId, userName, md5Psw);
                                        } else {
                                            editor = pref.edit();
                                            editor.clear();
                                            editor.commit();
                                        }
                                        Toast.makeText(LoginActivity.this, "welcome！" + userName, Toast.LENGTH_SHORT).show();
                                        //销毁登录界面
                                        LoginActivity.this.finish();
                                        //跳转到主界面，登录成功的状态传递到 MainActivity 中
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(LoginActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                                        closeProgressDialog();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginActivity.this, "网络故障，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        };

                        mDatabase.child("user").orderByChild("userName").equalTo(userName).addValueEventListener(valueEventListener);
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "请先同意隐私协议！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为了跳转到注册界面，并实现注册功能
                Intent intent=new Intent( LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacyDialog = new PrivacyDialog(LoginActivity.this);
                privacyDialog.show();
            }
        });

        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isPwdEdit = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void getEditString(){
        userName = et_username.getText().toString().trim();
        passWord = et_pwd.getText().toString().trim();
    }

    private void saveLoginStatus(String userId,String userName,String passWord){
        editor = pref.edit();
        //存入boolean类型的记住用户
        editor.putBoolean("save_pwd",true);
        //存入登录状态时的用户Id
        editor.putString("user_id", userId);
        //存入用户名
        editor.putString("user_name",userName);
        //存入用户密码
        editor.putString("password",passWord);
        //提交修改
        editor.commit();
    }

    private boolean checkIsSame(){
        getEditString();
        if (userName.equals(pref.getString("user_name",""))){
            return true;
        }
        return false;
    }





}
