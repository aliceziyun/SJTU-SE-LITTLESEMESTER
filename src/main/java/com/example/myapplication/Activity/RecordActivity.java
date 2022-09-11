package com.example.myapplication.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.example.myapplication.R;
import com.example.myapplication.db.Controller.PathRecordController;
import com.example.myapplication.db.entity.PathRecord;
import com.example.myapplication.db.entity.User;
import com.example.myapplication.util.DateHelper;
import com.example.myapplication.util.PathUtil;
import com.example.myapplication.util.TraceRePlay;
import com.example.myapplication.view.NineGridLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordActivity extends AppCompatActivity implements
        AMap.OnMapLoadedListener{
    private final static int AMAP_LOADED = 2;

    private Button finishRunningBtn;
    private ImageLoader imageLoader = ImageLoader.getInstance();;
    private TextView userNameText;
    private DateHelper dateHelper = new DateHelper();
    private List<PathRecord> pathRecords;
    private PathRecordController pathRecordController;
    private MapView mMapView;
    private AMap mAMap;
    private List<LatLng> mOriginLatLngList;
    private Marker mOriginStartMarker, mOriginEndMarker, mOriginRoleMarker;
    private Polyline mOriginPolyline;
    private ExecutorService mThreadPool;

    private DisplayImageOptions circleOptions;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        float distance = intent.getFloatExtra("distance",0);
        int time = intent.getIntExtra("time",0);
        String date = intent.getStringExtra("date");
        userId = intent.getStringExtra("user_id");

        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_head)
                .showImageOnFail(R.drawable.default_head)
                .showImageForEmptyUri(R.drawable.default_head)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    User runningUser = task.getResult().getValue(User.class);
                    ImageView runningUserAvator = (ImageView) findViewById(R.id.running_end_avator);
                    userNameText = findViewById(R.id.running_user_name);
                    userNameText.setText(runningUser.getUserName());
                    imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
                    imageLoader.displayImage(runningUser.getImg(), runningUserAvator, circleOptions);

                    TextView kmTotalText = findViewById(R.id.running_total_km);
                    Typeface kmTotalText_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Bold.ttf");
                    kmTotalText.setTypeface(kmTotalText_typeface);
                    DecimalFormat df = new DecimalFormat("0.00");
                    kmTotalText.setText(df.format(distance / 1000) + "km"); //设置总公里文本

                    TextView speedAverageText = findViewById(R.id.running_average_speed);
                    Typeface speedAverageText_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
                    speedAverageText.setTypeface(speedAverageText_typeface);
                    float averageSpeed = (float) ((time/60.0)/(distance/1000));
                    speedAverageText.setText(df.format(averageSpeed));  //设置平均配速

                    TextView timeTotalText = findViewById(R.id.running_total_time);
                    Typeface timeTotalText_typeface = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
                    timeTotalText.setTypeface(timeTotalText_typeface);
                    timeTotalText.setText(dateHelper.secToTime(time));  //设置时间文本

                    finishRunningBtn = findViewById(R.id.running_finish_btn);
                    finishRunningBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RecordActivity.this.finish();
                        }
                    });

                    pathRecordController = new PathRecordController(getApplicationContext());
                    pathRecords = pathRecordController.getRecordByDate(userId,date);

                    mThreadPool = Executors.newFixedThreadPool(2);

                    initMap(savedInstanceState);

                }
            }
        });
    }

    private void initMap(Bundle savedInstanceState){
        mMapView = (MapView) findViewById(R.id.record_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.setOnMapLoadedListener(this);
        }
    }

    @Override
    public void onMapLoaded() {
        Message msg = handler.obtainMessage();
        msg.what = AMAP_LOADED;
        handler.sendMessage(msg);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AMAP_LOADED:
                    try {
                        for(int i = 0;i < pathRecords.size();i++){
                            Log.i("PathRecord", String.valueOf(i));
                            setupRecord(i);
                            showOriginalRecord(mOriginLatLngList, mOriginRoleMarker);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            return b.build();
        }
        for (int i = 0; i < mOriginLatLngList.size(); i++) {
            b.include(mOriginLatLngList.get(i));
        }
        return b.build();
    }

    /**
     * 显示原始轨迹
     */
    private void showOriginalRecord(List<LatLng> list, final Marker updateMarker){
        TraceRePlay replay = new TraceRePlay(list, 100,
                new TraceRePlay.TraceRePlayListener() {

                    @Override
                    public void onTraceUpdating(LatLng latLng) {
                        if (updateMarker != null) {
                            updateMarker.setPosition(latLng); // 更新小人实现轨迹回放
                        }
                    }

                    @Override
                    public void onTraceUpdateFinish() {
                    }
                });
        mThreadPool.execute(replay);
        return;
    }

    /**
     * 轨迹数据初始化
     *
     */
    private void setupRecord(int i) throws Exception {
        if (pathRecords != null) {
            PathRecord record = pathRecords.get(i);
            List<AMapLocation> recordList = record.getPathline();
            AMapLocation startLoc = record.getStartpoint();
            AMapLocation endLoc = record.getEndpoint();
            if (recordList == null || startLoc == null || endLoc == null) {
                return;
            }
            LatLng startLatLng = new LatLng(startLoc.getLatitude(), startLoc.getLongitude());
            LatLng endLatLng = new LatLng(endLoc.getLatitude(), endLoc.getLongitude());
            mOriginLatLngList = PathUtil.parseLatLngList(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);
        }
    }


    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint,
                                List<LatLng> originList) {
        mOriginPolyline = mAMap.addPolyline(new PolylineOptions().color(Color.BLUE).addAll(originList));
        mOriginStartMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_on_24_start)));
        mOriginEndMarker = mAMap.addMarker(new MarkerOptions().position(
                endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_on_24_end)));

        try {
            mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(),
                    50));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mOriginRoleMarker = mAMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.ic_baseline_directions_run_24_org))));
    }
}
