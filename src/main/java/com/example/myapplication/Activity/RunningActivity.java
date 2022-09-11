package com.example.myapplication.Activity;

//还剩：
//3.没有轨迹回放
//5.没有计算卡路里

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.myapplication.R;
import com.example.myapplication.db.Controller.PathRecordController;
import com.example.myapplication.db.Controller.SportsDataController;
import com.example.myapplication.db.Controller.SportsPlanController;
import com.example.myapplication.db.entity.PathRecord;
import com.example.myapplication.db.entity.SportsData;
import com.example.myapplication.db.entity.SportsType;
import com.example.myapplication.util.DateHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RunningActivity extends AppCompatActivity {
    private static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public AMap aMap;
    private MapView mapView;
    private DateHelper dateHelper = new DateHelper();
    private Timer timer;
    private String userId;
    private String date;
    private PathRecord record;
    private final List<LatLng> latLngs = new ArrayList<>(); //用于暂时存储一段轨迹
    private SportsPlanController sportsPlanController;
    private PathRecordController pathRecordController;
    public boolean startDrawLine = false;
    public Button startRunningBtn;
    public ImageButton endRunningBtn, continueRunningBtn, pauseRunningBtn;
    public Button finishRunningBtn2;
    public TextView kmText, speedText, timeText;
    public LinearLayout runningBanner;
    public float distance = 0, speed = 0, nowKm = 0,averageSpeed = 0;
    public int time = 0, interval = 2000, count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);

        pathRecordController = new PathRecordController(getApplicationContext());
        sportsPlanController  = new SportsPlanController(getApplicationContext());

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        startRunningBtn = (Button) findViewById(R.id.start_running_btn);
        startRunningBtn.setOnClickListener(this::onClick);
        pauseRunningBtn = (ImageButton) findViewById(R.id.pause_running_btn);
        pauseRunningBtn.setOnClickListener(this::onClick);
        endRunningBtn = (ImageButton) findViewById(R.id.end_running_btn);
        endRunningBtn.setOnClickListener(this::onClick);
        continueRunningBtn = (ImageButton) findViewById(R.id.continue_running_btn);
        continueRunningBtn.setOnClickListener(this::onClick);

        kmText = (TextView) findViewById(R.id.running_km);
        kmText.setText(String.valueOf(distance));
        speedText = (TextView) findViewById(R.id.running_speed);
        speedText.setText(String.valueOf(speed));
        timeText = (TextView) findViewById(R.id.running_time);
        timeText.setText(String.valueOf(time)); //之后用dateHelper转换

        runningBanner = (LinearLayout) findViewById(R.id.running_data_banner);

        init();
        getUserIdAndDate();
        record.setDate(date);
        record.setUserId(userId);
        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //按钮点击函数
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_running_btn:{
                startDrawLine = true;
                startRunningBtn.setVisibility(View.INVISIBLE);
                pauseRunningBtn.setVisibility(View.VISIBLE);
                endRunningBtn.setVisibility(View.VISIBLE);
                runningBanner.setVisibility(View.VISIBLE);
                startTimer();
                break;
            }
            case R.id.pause_running_btn:{
                startDrawLine = false;
                pauseRunningBtn.setVisibility(View.INVISIBLE);
                continueRunningBtn.setVisibility(View.VISIBLE);
                endTimer();
                Log.i("PathRecord","pause"+record.getDate());
                savePathRecord(record.getPathline(),record.getDate(),userId);
                break;
            }
            case R.id.continue_running_btn:{
                startDrawLine = true;
                pauseRunningBtn.setVisibility(View.VISIBLE);
                continueRunningBtn.setVisibility(View.INVISIBLE);
                startTimer();
                record = new PathRecord();
                record.setDate(date);
                record.setUserId(userId);
                break;
            }
            case R.id.end_running_btn: {
                startDrawLine = false;
                endTimer();
                Log.i("PathRecord","end"+record.getDate());
                savePathRecord(record.getPathline(),record.getDate(),userId);
                initEndScene();
                break;
            }
            default:
        }
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    if (startDrawLine) {
                        LatLng newPoint = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                        latLngs.add(newPoint);
                        if (latLngs.size() > 1) {     //计算跑步公里数
                            LatLng oldPoint = latLngs.get(latLngs.size() - 2);
                            distance += AMapUtils.calculateLineDistance(newPoint, oldPoint);
                            DecimalFormat df = new DecimalFormat("0.00");
                            kmText.setText(df.format(distance / 1000) + "km");
                            if (count == 5) {    //每隔10s计算一次配速
                                nowKm += AMapUtils.calculateLineDistance(newPoint, oldPoint);
                                double nowSpeed = (1.0 / 6) / (nowKm / 1000);
                                if (0 <= nowSpeed && nowSpeed <= 20)
                                    speedText.setText(df.format(nowSpeed));
                                count = 1;
                                nowKm = 0;
                            } else {
                                nowKm += AMapUtils.calculateLineDistance(newPoint, oldPoint);
                            }
                            count++;
                        }
                        aMap.addPolyline(new PolylineOptions().addAll(latLngs).width(20).color(Color.argb(235, 1, 180, 247)));  //绘制
                        record.addpoint(amapLocation);
                    } else
                        latLngs.clear();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
            aMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
    };

    //从pref中获取用户Id
    private void getUserIdAndDate(){
        SharedPreferences pref = getSharedPreferences("LogInfo",MODE_PRIVATE);     //获取当前用户id
        userId = pref.getString("user_id","");
        date = dateHelper.getNowTime();
    }

    //初始化地图
    public void init() {
        aMap = mapView.getMap();
        aMap.getUiSettings().setAllGesturesEnabled(false);//禁止地图可拖动

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(interval); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        record = new PathRecord();
    }

    //开启定位
    public void startLocation() {
        //需要选择ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }

        //可在此继续其他操作。
        try {
            mLocationClient = new AMapLocationClient(this);
        } catch (Exception e) {
            Log.e("AmapError", "error");
            e.printStackTrace();
        }

        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //启动定位
        mLocationClient.startLocation();
    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathLine = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathLine.append(locString).append(";");
        }
        String pathLineString = pathLine.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            timeText.setText(dateHelper.secToTime(time));
        }
    };

    //计时器开始
    public void startTimer(){
        timer = new Timer("Timer");
        timer.schedule(new TimerTask() {
            public void run() {
                time++;
                Message message= new Message();
                message.arg1 = time;
                handler.sendMessage(message);
            }
        }, 0,1000);
    }

    //计时器停止
    public void endTimer(){
        timer.cancel();
        timer = null;
    }

    //加载结束画面
    public void initEndScene(){
        //运动距离达标的情况
        if(distance <= 200){    //debug
            //直接加载一个新活动
            generateRecord();
            Intent intent=new Intent( RunningActivity.this,RecordActivity.class);
            intent.putExtra("distance",distance);
            intent.putExtra("time",time);
            intent.putExtra("date",date);
            intent.putExtra("user_id",userId);
            startActivity(intent);
            RunningActivity.this.finish();

        } else{
            setContentView(R.layout.activity_running2);
            finishRunningBtn2 = findViewById(R.id.running_finish_btn2);
            finishRunningBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RunningActivity.this.finish();
                    generateRecord();   //debug用
                }
            });
        }
    }

    //将一段轨迹存入数据库
    public void savePathRecord(List<AMapLocation> list,String date,String userId){
        if (list != null && list.size() > 0) {
            String pathLineString = getPathLineString(list);
            AMapLocation firstLocation = list.get(0);
            AMapLocation lastLocation = list.get(list.size() - 1);
            String startPoint = amapLocationToString(firstLocation);
            String endPoint = amapLocationToString(lastLocation
            );
            Log.i("posData",userId);
            pathRecordController.addRecord(userId,pathLineString,startPoint,endPoint,date);
            record = null;    //清空record
        }
    }

    //生成运动记录
    public long generateRecord(){
        //获取用户id
        String courseName = "running";
        int kcal = calculateKcal();
        Log.i("posData","running"+userId);
        SportsDataController sportsDataController = new SportsDataController(getApplicationContext());
        SportsData sportsData = sportsDataController.packData(userId,courseName,kcal,date,time,SportsType.RUNNING,averageSpeed);

        // 更新周达成天数
        Date today = new Date();
        String weekDay = dateHelper.getWeekOfDate(today);
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
        return sportsDataController.addNewRecord(sportsData);
    }


    //计算卡路里（未实现）
    public int calculateKcal(){ //还没写
        return 100;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}