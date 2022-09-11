package com.example.myapplication.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 开启进度弹窗
     * @param content
     */
    public void showProgressDialog(Context c,String content){
        if (progressDialog==null) {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage(content);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度弹窗
     */
    public void closeProgressDialog() {
        if (progressDialog!=null) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }
}
