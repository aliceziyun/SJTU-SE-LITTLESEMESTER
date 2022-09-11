package com.example.myapplication.Activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.myapplication.db.Controller.CourseController;
import com.example.myapplication.R;
import com.example.myapplication.db.Controller.SportsPlanController;
import com.example.myapplication.db.entity.Course;
import com.example.myapplication.db.entity.SportsData;

import java.util.ArrayList;
import java.util.Date;

import com.example.myapplication.db.Controller.SportsDataController;
import com.example.myapplication.db.entity.SportsType;
import com.example.myapplication.util.DateHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class VideoActivity extends BaseActivity implements View.OnClickListener {
    private static final int GET_DATA_SUCCESS = 0X02;
    private VideoView videoView;
    //private TextView start_time,es_time,result;
    private ImageButton play;
    private ImageButton pause;
    private ImageButton replay;
    private ImageButton back;
    private ImageButton favor;
    private int courseId;
    private int isFavored;
    private int favorPicture = R.drawable.ic_baseline_star_border_24_org;
    private int disfavorPicture = R.drawable.ic_baseline_star_border_24;
    private TextView title;
    private TextView description;
    MediaController mMediaController;
    CourseController courseController;
    private SportsDataController sportsDataController;
    private SportsPlanController sportsPlanController;
    private DateHelper dh;
    private int clickstart = 0;
    private DatabaseReference mDatabase;
    private String uri;
    //获得信息 需要传入前三个
    private String userId = "";
    private String courseName = "Video2";
    private int kcal = 100;

    private String startTime;
    private long esTime;//计时器开始时间
    private int duration = 0;
    private int sumduration;
    private float speed=-1;

    private int favorNumber;
    private int playTimes;
    private ValueEventListener favorValueEventListener;
    private ValueEventListener playValueEventListener;
    private TextView tv_favor_number;
    private TextView tv_play_times;
    

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    closeProgressDialog();
            }
            super.handleMessage(msg);
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent = getIntent();
        courseId = Integer.parseInt(intent.getStringExtra("courseId"));
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);
        userId = pref.getString("user_id","");

        play = (ImageButton) findViewById(R.id.play);
        pause = (ImageButton) findViewById(R.id.pause);
        replay = (ImageButton) findViewById(R.id.replay);
        back = (ImageButton) findViewById(R.id.backbutton);
        favor = (ImageButton) findViewById(R.id.favor_button);
        videoView = new VideoView(this);
        videoView = (VideoView) findViewById(R.id.video_view);
        title = (TextView) findViewById(R.id.video_play_title);
        description = (TextView) findViewById(R.id.video_play_description);
        tv_favor_number = (TextView) findViewById(R.id.tv_video_favor_number);
        tv_play_times = (TextView) findViewById(R.id.tv_video_play_times);
        mMediaController = new MediaController(this);
        favor.setOnClickListener(this::onClick);
        play.setOnClickListener(this::onClick);
        pause.setOnClickListener(this::onClick);
        replay.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);

        courseController = new CourseController(this);
        isFavored = courseController.checkFavor(userId,courseId);
        sportsDataController = new SportsDataController(this);
        sportsPlanController = new SportsPlanController(this);
        dh = new DateHelper();
        Log.d("tag", "onCreate: isFavored: " + isFavored);
        if (isFavored == 1) {
            favor.setImageDrawable(getResources().getDrawable(favorPicture));
        }
        else{
            favor.setImageDrawable(getResources().getDrawable(disfavorPicture));
        }
        Course course = courseController.getCourseById(courseId);
        title.setText(course.getCourseName());
        description.setText(course.getDescription());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        showProgressDialog(VideoActivity.this, "加载中");
        favorValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    favorNumber = Integer.valueOf(snapshot.getValue().toString());
                    Log.i(TAG, "onDataChange: favorNumber is " + String.valueOf(favorNumber));
                }
                else{
                    favorNumber = 0;
                }
                tv_favor_number.setText(String.valueOf(favorNumber) + " 收藏");
                mDatabase.child("video-number").child(String.valueOf(courseId)).child("play").addValueEventListener(playValueEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        playValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    playTimes = Integer.valueOf(snapshot.getValue().toString());
                    Log.i(TAG, "onDataChange: playTimes is " + String.valueOf(favorNumber));
                }
                else{
                    playTimes = 0;
                }
                tv_play_times.setText(String.valueOf(playTimes) + " 播放");
                initVideoPath(courseName);
                handler.sendEmptyMessage(GET_DATA_SUCCESS);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.child("video-number").child(String.valueOf(courseId)).child("favor").addValueEventListener(favorValueEventListener);




    }
    public void initVideoPath(String courseName) {
        //本地视频路径
//        String uri = "android.resource://" + getPackageName() + "/" + R.raw.pamela_1;
//        uri = "android.resource://" + getPackageName() + "/" + R.raw.video;  //本地
//        String uri2 = "https://upos-sz-mirrorcos.bilivideo.com/upgcxcode/84/64/255196484/255196484-1-208.mp4?e=ig8euxZM2rNcNbhM7wdVhwdlhzKMhwdVhoNvNC8BqJIzNbfq9rVEuxTEnE8L5F6VnEsSTx0vkX8fqJeYTj_lta53NCM=&uipk=5&nbs=1&deadline=1657705045&gen=playurlv2&os=bcache&oi=1962914279&trid=00003e22af6caa2045cc914710b587262b26T&mid=0&platform=html5&upsig=29b71e3a60f3898d79fc849d373fc760&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&cdnid=1760&bvc=vod&nettype=0&bw=345019&orderid=0,1&logo=80000000#vp";  //网络
//        videoView.setVideoURI(Uri.parse(uri));  //本地
        courseController = new CourseController(this);
        String uri = courseController.getSrcById(courseId);
        Log.i("videoUrl",uri);
        videoView.setVideoURI(Uri.parse(uri));  //网络
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        mMediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mMediaController);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        switch (v.getId()) {
            case R.id.play:
                if (!videoView.isPlaying()) {
                    play.setVisibility(View.INVISIBLE);
                    pause.setVisibility(View.VISIBLE);
                    videoView.start(); // 开始播放
                    if(clickstart==0){
                        //第一次点击开始记录开始时间starttime
                        DateHelper dateHelper = new DateHelper();
                        startTime = dateHelper.getNowTime();
                        Log.i("posData",startTime);
                        long totalMillTimes=System.currentTimeMillis();
                        esTime=totalMillTimes / 1000;
                        clickstart=1;
                    }else{
                        long totalMillTimes=System.currentTimeMillis();
                        esTime=totalMillTimes / 1000;
                    }
                }
                break;
            case R.id.pause:
                if (videoView.isPlaying()) {
                    pause.setVisibility(View.INVISIBLE);
                    play.setVisibility(View.VISIBLE);
                    videoView.pause(); // 暂时播放
                    //结束计时器 duration+
                    long totalMillTimes=System.currentTimeMillis();
                    long tmptime=totalMillTimes / 1000;
                    duration += (int)(tmptime - esTime);
                }
                break;
            case R.id.replay:
                if (videoView.isPlaying()) {
                    videoView.resume(); // 重新播放
                    //结束计时器 duration++ 开始计时器
                    long totalMillTimes=System.currentTimeMillis();
                    long tmptime=totalMillTimes / 1000;
                    duration += (int)(tmptime - esTime);
                    esTime=totalMillTimes / 1000;
                }
                break;
            case R.id.backbutton:
                // 没有播放过视频,直接退出
                if (clickstart == 0) {
                    finish();
                    break;
                }
                if (videoView.isPlaying()) {
                    videoView.pause();
                    //结束计时器 duration++
                    long totalMillTimes=System.currentTimeMillis();
                    long tmptime=totalMillTimes / 1000;
                    duration += (int)(tmptime - esTime);
                    sumduration=duration;
                }else{
                    sumduration=duration;//直接用当前duration
                }
                //添加运动记录
                Log.i("posData","generateData");
                SportsDataController controller = new SportsDataController(getApplicationContext());
                int sumkcal = duration * kcal / 60;
                SportsData record= controller.packData(userId,courseName,sumkcal,startTime,sumduration, SportsType.FITNESS,speed);

                // 更新周达成天数
                Date today = new Date();
                String weekDay = dh.getWeekOfDate(today);
                if (weekDay == "Mon"){
                    sportsPlanController.setGoalReachDay(0, userId);
                }
                ArrayList<SportsData> dayList = sportsDataController.getRecordByKind(SportsType.DAY, userId);
                String[] dayTotal = sportsDataController.calculateTotal(dayList);
                int dayConsume = Integer.valueOf(dayTotal[0]);
                int dayGoal = sportsPlanController.getKcalByDay(weekDay, userId);
                if (dayConsume < dayGoal && dayConsume + kcal >= dayGoal) {
                    sportsPlanController.addGoalReachDay(userId);
                }

                int test=(int)controller.addNewRecord(record);
                Log.i("posData", String.valueOf(test));

                // 更新视频播放次数
                mDatabase = FirebaseDatabase.getInstance().getReference("/video-number/" + String.valueOf(courseId) + "/play");
                mDatabase.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Object obj = currentData.getValue();
                        int times = 0;
                        if (obj == null){
                            times = 0;
                        }
                        else if (!currentData.getValue().toString().equals("")){
                            times = Integer.valueOf(currentData.getValue().toString());
                        }
                        times++;
                        Log.i(TAG, "doTransaction: play_times " + String.valueOf(times));
                        currentData.setValue(times);
                        return Transaction.success(currentData);
                    }
                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                    }
                });



                finish();
                break;
            case R.id.favor_button:
                mDatabase = FirebaseDatabase.getInstance().getReference("/video-number/" + String.valueOf(courseId) + "/favor");
                if (isFavored == 0) {
                    courseController.favorCourse(userId,courseId);
                    favor.setImageDrawable(getResources().getDrawable(favorPicture));
                    isFavored = 1;

                    mDatabase.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Object obj = currentData.getValue();
                            int number = 0;
                            if (obj == null){
                                number = 0;
                            }
                            else if (!currentData.getValue().toString().equals("")){
                                number = Integer.valueOf(currentData.getValue().toString());
                            }
                            number++;
                            Log.i(TAG, "doTransaction: favor_number " + String.valueOf(number));
                            currentData.setValue(number);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        }
                    });

                }
                else {
                    courseController.disfavorCourse(userId,courseId);
                    favor.setImageDrawable(getResources().getDrawable(disfavorPicture));
                    isFavored = 0;

                    mDatabase.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Object obj = currentData.getValue();
                            int number = 0;
                            if (obj == null){
                                number = 0;
                            }
                            else if (!currentData.getValue().toString().equals("")){
                                number = Integer.valueOf(currentData.getValue().toString());
                            }
                            if (number == 0){
                                Log.i(TAG, "doTransaction: firebase data error!");
                            }
                            else {
                                number--;
                            }
                            Log.i(TAG, "doTransaction: favor_number: " + String.valueOf(number));
                            currentData.setValue(number);
                            return Transaction.success(currentData);
                        }
                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        }
                    });
                }
                break;
        } }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.suspend();
        }
        mDatabase.child("video-number").child(String.valueOf(courseId)).child("favor").removeEventListener(favorValueEventListener);
        mDatabase.child("video-number").child(String.valueOf(courseId)).child("play").removeEventListener(playValueEventListener);
    }

}