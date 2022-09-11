package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;
import static android.view.View.INVISIBLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.db.Controller.SportsPlanController;
import com.example.myapplication.db.entity.SportsPlan;
import com.example.myapplication.notice.AlarmReceiver;

import java.util.Calendar;
import java.util.TimeZone;

public class SportPlanActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private ImageButton btn_modify_sport_plan_time;
    private ImageButton btn_modify_sport_week_plan;
    private Button startSportPlan;
    private RelativeLayout layoutTime;
    private LinearLayout layout_sport_plan;
    private TextView message;
    private String remindTime;
    private String userId;
    private SportsPlanController sportsPlanController;

    private TextView tv_kcal_Mon, tv_kcal_Tue, tv_kcal_Wed, tv_kcal_Thur, tv_kcal_Fri, tv_kcal_Sat, tv_kcal_Sun;
    private int kcalMon = 0, kcalTue= 0, kcalWed = 0, kcalThur = 0, kcalFri = 0, kcalSat = 0, kcalSun = 0;
    private int kcalEveryday = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_plan);
        init();
    }

    private void init(){
        btn_back = findViewById(R.id.spplan_back);

        btn_modify_sport_plan_time = (ImageButton) findViewById(R.id.modify_sport_plan_time);
        btn_modify_sport_week_plan = (ImageButton) findViewById(R.id.modify_sport_week_plan);
        sportsPlanController = new SportsPlanController(this);
        //制定计划按钮
        startSportPlan = (Button) findViewById(R.id.start_sport_plan);
        //两个显示的布局
        layout_sport_plan = (LinearLayout) findViewById(R.id.layout_sport_plan);
        layoutTime = (RelativeLayout) findViewById(R.id.layout_sport_plan_time);
        //提示信息
        message = (TextView) findViewById(R.id.set_plan_message);

        tv_kcal_Mon = (TextView) findViewById(R.id.tv_kcal_Mon);
        tv_kcal_Tue = (TextView) findViewById(R.id.tv_kcal_Tue);
        tv_kcal_Wed = (TextView) findViewById(R.id.tv_kcal_Wed);
        tv_kcal_Thur = (TextView) findViewById(R.id.tv_kcal_Thur);
        tv_kcal_Fri = (TextView) findViewById(R.id.tv_kcal_Fri);
        tv_kcal_Sat =(TextView) findViewById(R.id.tv_kcal_Sat);
        tv_kcal_Sun = (TextView) findViewById(R.id.tv_kcal_Sun);



        //获取用户id
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        userId = pref.getString("user_id", "");

        sportsPlanController = new SportsPlanController(this);
        SportsPlan plan = sportsPlanController.getSportsPlan(userId);
        if (plan == null){
            layout_sport_plan.setVisibility(INVISIBLE);
            message.setVisibility(TextView.VISIBLE);
            startSportPlan.setVisibility(Button.VISIBLE);
        }
        else{

//            TextView kcal = (TextView) findViewById(R.id.sport_plan_kcal);
//            kcal.setText("目标消耗: " + plan.getPlanKcal() + "kcal");
            tv_kcal_Mon.setText(String.valueOf(plan.getKcalMon()) + "kcal");
            tv_kcal_Tue.setText(String.valueOf(plan.getKcalTue()) + "kcal");
            tv_kcal_Wed.setText(String.valueOf(plan.getKcalWed()) + "kcal");
            tv_kcal_Thur.setText(String.valueOf(plan.getKcalThur()) + "kcal");
            tv_kcal_Fri.setText(String.valueOf(plan.getKcalFri()) + "kcal");
            tv_kcal_Sat.setText(String.valueOf(plan.getKcalSat()) + "kcal");
            tv_kcal_Sun.setText(String.valueOf(plan.getKcalSun()) + "kcal");
            TextView time = (TextView) findViewById(R.id.sport_plan_remind_time);
            time.setText("提醒时间: " + plan.getRemindTime());
        }

        // 返回键
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SportPlanActivity.this.finish();
            }
        });

//        //设置目标卡路里
//        modifySportPlanKcal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onCreateModifyKcalDialog();
//            }
//        });

        btn_modify_sport_week_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateModifySportPlanDialog();
            }
        });

        //设置计划时间
        btn_modify_sport_plan_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateModifyTimeDialog();
            }
        });

        startSportPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_sport_plan.setVisibility(LinearLayout.VISIBLE);
                layoutTime.setVisibility(LinearLayout.VISIBLE);
                message.setVisibility(INVISIBLE);
                startSportPlan.setVisibility(INVISIBLE);
                sportsPlanController.setSportsPlan(userId, "17:00", 50, 50, 50, 50, 50, 50, 50);
            }
        });
    }

//    private void onCreateModifyKcalDialog(){
//        // 使用LayoutInflater来加载dialog_modify_sport_plan_kcal.xml布局
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View modifyPlanView = layoutInflater.inflate(R.layout.dialog_modify_sport_plan_kcal, null);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        // 使用setView()方法将布局显示到dialog
//        alertDialogBuilder.setView(modifyPlanView);
//
//        //EditText为编辑的文本框
//        //TextView为显示的文本框
//        final EditText editView = (EditText) modifyPlanView.findViewById(R.id.edit_plan_kcal);
//        final TextView showView = (TextView) findViewById(R.id.sport_plan_kcal);
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                // 获取edittext的内容,显示到textview
//                                planKcal = Integer.valueOf(editView.getText().toString());
////                                System.out.println(editView.getText());
//                                sportsPlanController.setPlanKcal(userId, planKcal);
//                                showView.setText("目标消耗: " + planKcal + "kcal");
//                            }
//                        })
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }


    private void onCreateModifyTimeDialog(){
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(calendar.HOUR_OF_DAY);
        int minute = calendar.get(calendar.MINUTE);

        final TextView showView = (TextView) findViewById(R.id.sport_plan_remind_time);

        TimePickerDialog timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                remindTime = hourOfDay + ":" + minute;
                // 处理分钟为0时出现诸如15:0的情况
                if (minute == 0){
                    remindTime = remindTime + "0";
                }
                sportsPlanController.setRemindTime(userId, remindTime);
                showView.setText("提醒时间: " + remindTime);
                startRemind(hourOfDay, minute);
            }
        }, hourOfDay, minute, true); // 最后一个参数设置是否为24小时制
        timeDialog.show();
    }

    private void onCreateModifySportPlanDialog(){
        // 使用LayoutInflater来加载dialog_modify_sport_plan_kcal.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View modifyPlanView = layoutInflater.inflate(R.layout.dialog_modify_week_plan, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 使用setView()方法将布局显示到dialog


        alertDialogBuilder.setView(modifyPlanView);
        Log.i(TAG, "onCreateModifySportPlanDialog: heiheiheiheiehi");
        EditText et_everyday_kcal = (EditText) modifyPlanView.findViewById(R.id.et_everyday_kcal);
        EditText et_kcal_Mon = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Mon);
        EditText et_kcal_Tue = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Tue);
        EditText et_kcal_Wed = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Wed);
        EditText et_kcal_Thur = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Thur);
        EditText et_kcal_Fri = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Fri);
        EditText et_kcal_Sat = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Sat);
        EditText et_kcal_Sun = (EditText) modifyPlanView.findViewById(R.id.et_kcal_Sun);

        et_everyday_kcal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                kcalEveryday = 0;
                String input = et_everyday_kcal.getText().toString();
                if (!input.equals("")){
                    Log.i(TAG, "onTextChanged: " + input);
                    if (input == ""){
                        Log.i(TAG, "onTextChanged: hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                    }
                    kcalEveryday = Integer.valueOf(input);
                }
                if (kcalEveryday != 0){
                    kcalMon = kcalTue = kcalWed = kcalThur = kcalFri = kcalSat = kcalSun = kcalEveryday;
                    tv_kcal_Mon.setText(String.valueOf(kcalMon) + "kcal");
                    tv_kcal_Tue.setText(String.valueOf(kcalTue) + "kcal");
                    tv_kcal_Wed.setText(String.valueOf(kcalWed) + "kcal");
                    tv_kcal_Thur.setText(String.valueOf(kcalThur) + "kcal");
                    tv_kcal_Fri.setText(String.valueOf(kcalFri) + "kcal");
                    tv_kcal_Sat.setText(String.valueOf(kcalSat) + "kcal");
                    tv_kcal_Sun.setText(String.valueOf(kcalSun) + "kcal");

                    et_kcal_Mon.setText(String.valueOf(kcalMon));
                    et_kcal_Tue.setText(String.valueOf(kcalTue));
                    et_kcal_Wed.setText(String.valueOf(kcalWed));
                    et_kcal_Thur.setText(String.valueOf(kcalThur));
                    et_kcal_Fri.setText(String.valueOf(kcalFri));
                    et_kcal_Sat.setText(String.valueOf(kcalSat));
                    et_kcal_Sun.setText(String.valueOf(kcalSun));
                }
                else{
                    et_kcal_Mon.setText("");
                    et_kcal_Tue.setText("");
                    et_kcal_Wed.setText("");
                    et_kcal_Thur.setText("");
                    et_kcal_Fri.setText("");
                    et_kcal_Sat.setText("");
                    et_kcal_Sun.setText("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!et_kcal_Mon.getText().toString().equals(""))
                                    kcalMon = Integer.valueOf(et_kcal_Mon.getText().toString());
                                else
                                    kcalMon = 0;
                                if (!et_kcal_Tue.getText().toString().equals(""))
                                    kcalTue = Integer.valueOf(et_kcal_Tue.getText().toString());
                                else
                                    kcalTue = 0;
                                if (!et_kcal_Wed.getText().toString().equals(""))
                                    kcalWed = Integer.valueOf(et_kcal_Wed.getText().toString());
                                else kcalWed = 0;
                                if (!et_kcal_Thur.getText().toString().equals(""))
                                    kcalThur = Integer.valueOf(et_kcal_Thur.getText().toString());
                                else
                                    kcalThur = 0;
                                if (!et_kcal_Fri.getText().toString().equals(""))
                                    kcalFri = Integer.valueOf(et_kcal_Fri.getText().toString());
                                else
                                    kcalFri = 0;
                                if (!et_kcal_Sat.getText().toString().equals(""))
                                    kcalSat = Integer.valueOf(et_kcal_Sat.getText().toString());
                                else
                                    kcalSat = 0;
                                if (!et_kcal_Sun.getText().toString().equals(""))
                                    kcalSun = Integer.valueOf(et_kcal_Sun.getText().toString());
                                else
                                    kcalSun = 0;

                                tv_kcal_Mon.setText(String.valueOf(kcalMon) + "kcal");
                                tv_kcal_Tue.setText(String.valueOf(kcalTue) + "kcal");
                                tv_kcal_Wed.setText(String.valueOf(kcalWed) + "kcal");
                                tv_kcal_Thur.setText(String.valueOf(kcalThur) + "kcal");
                                tv_kcal_Fri.setText(String.valueOf(kcalFri) + "kcal");
                                tv_kcal_Sat.setText(String.valueOf(kcalSat) + "kcal");
                                tv_kcal_Sun.setText(String.valueOf(kcalSun) + "kcal");

                                sportsPlanController.setKcal(userId, kcalMon, kcalTue, kcalWed, kcalThur, kcalFri, kcalSat, kcalSun);

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void startRemind(int hour, int min){
        //得到日历实例，主要是为了下面的获取时间
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        //设置在几点提醒
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        //设置在几分提醒
        mCalendar.set(Calendar.MINUTE, min);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        long selectTime = mCalendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        System.out.println(selectTime - systemTime);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), (1000 * 60 * 60 * 24), pi);
        Toast.makeText(this, "打开了提醒", Toast.LENGTH_SHORT).show();
    }

    private void stopRemind(){
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        manager.cancel(pi);
        Toast.makeText(this, "关闭了提醒", Toast.LENGTH_SHORT).show();
    }
}
