package com.example.myapplication.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.Calendar;

public class ModifyActivity extends AppCompatActivity {
    private ImageButton btn_back, gender_choose, birthday_choose, height_choose, weight_choose;
    private RelativeLayout change_profile;
    private TextView change_username;
    private TextView gender_text;

    private String[] gender_arr = new String[]{"男","女","保密"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        init();
    }

    private void init(){
        btn_back = findViewById(R.id.modify_back);
        change_profile = findViewById(R.id.change_profile);
        gender_choose = findViewById(R.id.gender_choose);
        gender_text = findViewById(R.id.gender_text);
        change_username = findViewById(R.id.change_username);
        birthday_choose = findViewById(R.id.birthday_choose);
        height_choose = findViewById(R.id.height_choose);
        weight_choose = findViewById(R.id.weight_choose);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModifyActivity.this.finish();
            }
        });
        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectImgs();
            }
        });
        gender_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowGenderChooseDialog();
            }
        });
        change_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNameDialog();
            }
        });
        birthday_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar nowdate = Calendar.getInstance();
                final int mYear = nowdate.get(Calendar.YEAR);
                final int mMonth = nowdate.get(Calendar.MONTH);
                final int mDay = nowdate.get(Calendar.DAY_OF_MONTH);
                //调用DatePickerDialog
                new DatePickerDialog(ModifyActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        height_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateHeightDialog();
            }
        });
        weight_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateWeightDialog();
            }
        });
    }

    private void ShowGenderChooseDialog(){
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框
        builder3.setSingleChoiceItems(gender_arr, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                gender_text.setText(gender_arr[which]);
                dialog.dismiss();// 点击一个item消失对话框，不用点击确认取消
            }
        });
        builder3.show();// 让弹出框显示
    }

    private void onCreateNameDialog() {
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View nameView = layoutInflater.inflate(R.layout.dialog_setname, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(nameView);

        final EditText userInput = (EditText) nameView.findViewById(R.id.changename_edit);
        final TextView name = (TextView) findViewById(R.id.change_username);

        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 获取edittext的内容,显示到textview
                                name.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * 日期选择器对话框监听
     */
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            TextView date_textview = (TextView) findViewById(R.id.birthday_text);
            String days;
            days = new StringBuffer().append(mYear).append("-").append(mMonth).append("-").append(mDay).toString();
            date_textview.setText(days);
        }
    };

    private void onCreateHeightDialog(){
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View heightView = layoutInflater.inflate(R.layout.dialog_setheight, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(heightView);

        final EditText userInput = (EditText) heightView.findViewById(R.id.changeheight_edit);
        final TextView height = (TextView) findViewById(R.id.change_userheight);

        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 获取edittext的内容,显示到textview
                                height.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void onCreateWeightDialog(){
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View weightView = layoutInflater.inflate(R.layout.dialog_setweight, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(weightView);

        final EditText userInput = (EditText) weightView.findViewById(R.id.changeweight_edit);
        final TextView weight = (TextView) findViewById(R.id.change_userweight);

        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 获取edittext的内容,显示到textview
                                weight.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
