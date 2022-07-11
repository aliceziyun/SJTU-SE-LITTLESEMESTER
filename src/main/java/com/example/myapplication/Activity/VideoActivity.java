package com.example.myapplication.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;

import com.example.myapplication.Controller.SportsDataController;
import com.example.myapplication.R;
import com.example.myapplication.entity.SportsData;
import com.example.myapplication.entity.SportsType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends Activity implements View.OnClickListener{
    private VideoView videoView;
    //private TextView start_time,es_time,result;
    private Button play;
    private Button pause;
    private Button replay;
    private Button back;
    MediaController mMediaController;
    int clickstart=0;
    String uri;
    //获得信息 需要传入前三个
    int userId=1;
    String courseName="Video";
    int kcal=100;

    String startTime;
    long esTime;//计时器开始时间
    int duration= 0;
    int sumduration;
    float speed=-1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        replay = (Button) findViewById(R.id.replay);
        back = (Button) findViewById(R.id.backbutton);
        videoView = new VideoView(this);
        videoView = (VideoView) findViewById(R.id.video_view);
        mMediaController = new MediaController(this);
        play.setOnClickListener(this::onClick);
        pause.setOnClickListener(this::onClick);
        replay.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);
        initVideoPath(courseName);
        //start_time = findViewById(R.id.start_time);
        //es_time = findViewById(R.id.es_time);
        //result = findViewById(R.id.result);
    }
    public void initVideoPath(String courseName) {
        //本地视频路径
        uri = "android.resource://" + getPackageName() + "/" + R.raw.video;  //本地
        //String uri2 = "https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";  //网络
        videoView.setVideoURI(Uri.parse(uri));  //本地
        //mVideoView.setVideoURI(Uri.parse(uri2));  //网络
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
                    videoView.start(); // 开始播放
                    if(clickstart==0){
                        //第一次点击开始记录开始时间starttime
                        Date startcurDate = new Date(System.currentTimeMillis());
                        startTime = formatter.format(startcurDate);
                        long totalMillTimes=System.currentTimeMillis();
                        esTime=totalMillTimes/1000;
                        clickstart=1;
                    }else{
                        long totalMillTimes=System.currentTimeMillis();
                        esTime=totalMillTimes/1000;
                    }
                }
                break;
            case R.id.pause:
                if (videoView.isPlaying()) {
                    videoView.pause(); // 暂时播放
                    //结束计时器 duration+
                    long totalMillTimes=System.currentTimeMillis();
                    long tmptime=totalMillTimes/1000;
                    duration += (int)(tmptime - esTime);
                }
                break;
            case R.id.replay:
                if (videoView.isPlaying()) {
                    videoView.resume(); // 重新播放
                    //结束计时器 duration++ 开始计时器
                    long totalMillTimes=System.currentTimeMillis();
                    long tmptime=totalMillTimes/1000;
                    duration += (int)(tmptime - esTime);
                    esTime=totalMillTimes/1000;
                }
                break;
            case R.id.backbutton:
                if (videoView.isPlaying()) {
                    videoView.pause();
                    //结束计时器 duration++
                    long totalMillTimes=System.currentTimeMillis();
                    long tmptime=totalMillTimes/1000;
                    duration += (int)(tmptime - esTime);
                    sumduration=duration;
                }else{
                    sumduration=duration;//直接用当前duration
                }
                //System.out.println(sumduration);
                //添加运动记录
                SportsDataController controller=new SportsDataController(getApplicationContext());
                int sumkcal=duration*kcal/60;
                SportsData record= controller.packData(userId,courseName,sumkcal,startTime,sumduration, SportsType.FITNESS,speed);
                int test=(int)controller.addNewRecord(record);
                //System.out.println(test);
                finish();
                break;
        } }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.suspend();
        } }

    //封面
    public static Bitmap getLocalVideoBitmap(String localPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }
    //封面
    public static Bitmap getVideoThumb(String uri) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(uri);
        return media.getFrameAtTime();
    }
}
