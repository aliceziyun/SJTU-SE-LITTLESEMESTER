package com.example.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.Activity.SportDataActivity;
import com.example.myapplication.Activity.SportPlanActivity;
import com.example.myapplication.R;
import com.example.myapplication.db.Controller.SportsDataController;
import com.example.myapplication.db.entity.SportsData;
import com.example.myapplication.db.entity.SportsType;
import com.example.myapplication.options.style.EchartsColor;
import com.example.myapplication.options.data.Data;
import com.example.myapplication.options.Legend;
import com.example.myapplication.options.Theme;
import com.example.myapplication.options.Tooltip;
import com.example.myapplication.options.code.Align;
import com.example.myapplication.options.code.EmphasisFocus;
import com.example.myapplication.options.code.RoseType;
import com.example.myapplication.options.code.Trigger;
import com.example.myapplication.options.json.GsonOption;
import com.example.myapplication.options.series.Pie;
import com.example.myapplication.options.style.ItemStyle;
import com.example.myapplication.options.style.itemstyle.Emphasis;
import com.example.myapplication.util.DateHelper;
import com.example.myapplication.view.ECharts;
import com.example.myapplication.view.ItemClick;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class HomePage extends Fragment implements ItemClick {
    private ImageButton spData, spPlan;
    private ViewPager viewpager;
    private PagerAdapter viewPagerAdapter;
    /*父容器，内部为动态添加的示意点*/
    private LinearLayout lyDot ;
    /*示意点不是的颜色值*/
    private String unSelectColor = "#476990";
    /*示意点的颜色值*/
    private String SelectColor = "#1d2939" ;

    /*轮播图图片资源*/
    private ArrayList<View> viewPagerData;

    /*Viewpager当前位置*/
    private int currentPosition = 0;

    ImageView imageView;
    /*延迟发送实现轮播*/
    private Handler handler;

    /* 当前activity是否存活，当为false的时候结束viewpager轮播线程*/
    private boolean actIsAlive = true;

    private Data yogaData,hiitData,runningData,stretchData,pamelaData;
    private int YogaTime,HiitTime,RunningTime,StretchTime,PamelaTime;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_home_page,null);

        init(view);
        initData();
        initDots();;
        initViewpager();;
        initHandler();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        actIsAlive = true; // 标记 Activity存活状态，子线程运行
        autoViewPager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        actIsAlive = false; // 标记 Activity销毁，子线程结束
    }

    /**
     * viewpager自动播放
     */
    private void autoViewPager() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (actIsAlive) {
                    try {
                        sleep(6000);
                        handler.sendEmptyMessage(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void init(View view){
        SharedPreferences pref = getActivity().getSharedPreferences("LogInfo", Context.MODE_PRIVATE);
        userId = pref.getString("user_id","");

        Pie pie = new Pie("1"); //统计图
        pie.name("Data 1")
                .selectMode(false)
                .label(new ItemStyle().emphasis(new Emphasis().focus(EmphasisFocus.self)))
                .radius("5%", "50%")
                .center("50%", "45%")
                .roseType(RoseType.area)
                .itemStyle(new ItemStyle().borderRadius(8));


        //set data value and name
        ArrayList<EchartsColor.Item> colorItem = new ArrayList<>();
        colorItem.add(new EchartsColor.Item().offset(0).color("#000"));
        colorItem.add(new EchartsColor.Item().offset(1).color("#fae"));
        hiitData =  new Data().value(0).name("Hiit");
        stretchData = new Data().value(5).name("Stretch");
        yogaData = new Data().value(5).name("Yoga");
        pamelaData =  new Data().value(5).name("Pamela");
        runningData = new Data().value(0).name("Running");

        upDateEchartsData();

        pie.data(hiitData);
        pie.data(stretchData);
        pie.data(yogaData);
        pie.data(pamelaData);
        pie.data(runningData);

        GsonOption option = new GsonOption();
        option.title("运动情况分析")
                .backgroundColor("#ffffff")
                .legend(new Legend().top(Align.bottom))
                .series(pie)
                .tooltip(new Tooltip().trigger(Trigger.item).formatter("<b>{b}</b> <br/>{c} (<i>{d}%</i>)"));


        ECharts eCharts = view.findViewById(R.id.chart);
        eCharts.setListener(this);
        eCharts.setTheme(Theme.LIGHT);
        eCharts.setOptions(option);
        eCharts.build();

        spData = view.findViewById(R.id.sport_data);
        spPlan = view.findViewById(R.id.sport_plan);
        lyDot = (LinearLayout) view.findViewById(R.id.lyDot);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        spData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SportDataActivity.class);
                startActivity(intent);
            }
        });
        spPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SportPlanActivity.class);
                startActivity(intent);
            }
        });


        SportsDataController sportsDataController = new SportsDataController(getContext());
        ArrayList<SportsData> dayData = sportsDataController.getRecordByKind(SportsType.DAY,userId);
        String[] result = sportsDataController.calculateTotal(dayData);
        TextView homeTime = view.findViewById(R.id.home_reach_time);
        TextView homeKcal = view.findViewById(R.id.home_reach_kcal);
        DateHelper dateHelper = new DateHelper();
        homeTime.setText(String.valueOf(dateHelper.timeToSec(result[1])));
        homeKcal.setText(result[0]);
    }

    private void upDateEchartsData(){
        SportsDataController sportsDataController = new SportsDataController(getContext());

        ArrayList<SportsData> fitnessArray = sportsDataController.getRecordByKind(SportsType.FITNESS,userId);
        HiitTime = 0;
        if(fitnessArray != null){
            for(SportsData each: fitnessArray){
                HiitTime += each.duration;
            }
        }
        hiitData.value(HiitTime);

        ArrayList<SportsData> yogaArray = sportsDataController.getRecordByKind(SportsType.YOGA,userId);
        YogaTime = 5;
        if(yogaArray != null){
            for(SportsData each: yogaArray){
                YogaTime += each.duration;
            }
        }
        yogaData.value(YogaTime);

        ArrayList<SportsData> runningArray = sportsDataController.getRecordByKind(SportsType.RUNNING,userId);
        RunningTime = 0;
        if(runningArray != null){
            for(SportsData each: runningArray){
                RunningTime += each.duration;
            }
        }
        runningData.value(RunningTime);
    }

    /**
     * 动态创建轮播图位置点显示
     */
    private void initDots() {
        // 动态添加轮播图位置点 , 默认第0个位置 为当前轮播图的颜色
        for (int i = 0; i < viewPagerData.size(); i++) {
            imageView = new ImageView(this.getActivity());
            if (i==0) {
                imageView.setBackgroundColor(Color.parseColor(SelectColor));
            }else{
                imageView.setBackgroundColor(Color.parseColor(unSelectColor));
            }
            imageView.setLayoutParams(new LinearLayout.LayoutParams(dip2px(8), dip2px(8)));
            setMargins(imageView,dip2px(2),0,dip2px(2),0);
            lyDot.addView(imageView);
        }
    }

    private void initData() {
        viewPagerData = new ArrayList<>();
        ImageView imageView = new ImageView(this.getActivity());
        /*添加图片资源，实际开发中为for循环即可 ，这里demo麻烦了*/
        // 第一张图片
        imageView.setBackgroundResource(R.drawable.view1);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        viewPagerData.add(imageView);

        // 第二张图片
        ImageView imageView2 = new ImageView(this.getActivity());
        imageView2.setBackgroundResource(R.drawable.view2);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        viewPagerData.add(imageView2);

        // 第三张图片
        ImageView imageView3 = new ImageView(this.getActivity());
        imageView3.setBackgroundResource(R.drawable.view3);
        imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
        viewPagerData.add(imageView3);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置view的margin
     * @param v           view
     * @param l           left
     * @param t           top
     * @param r           right
     * @param b           bottom
     */
    public void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void initViewpager() {
        //数据适配器
        viewPagerAdapter = new PagerAdapter() {
            private int mChildCount = 0;

            @Override
            public void notifyDataSetChanged() {
                mChildCount = getCount();
                super.notifyDataSetChanged();
            }

            @Override
            public int getItemPosition(Object object) {
                if (mChildCount > 0) {
                    mChildCount--;
                    return POSITION_NONE;
                }
                return super.getItemPosition(object);
            }

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return viewPagerData.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            //是从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(viewPagerData.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(viewPagerData.get(arg1));
                return viewPagerData.get(arg1);
            }
        };

        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setCurrentItem(0);
        viewpager.setOffscreenPageLimit(7);
//        viewpager.setPageTransformer(true, new ScaleInTransformer());
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                for (int i = 0; i < lyDot.getChildCount(); i++) {
                    if (i == currentPosition) {
                        lyDot.getChildAt(i).setBackgroundColor(Color.parseColor("#1d2939"));
                    } else {
                        lyDot.getChildAt(i).setBackgroundColor(Color.parseColor("#476990"));
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                // 没有滑动的时候 切换页面
            }
        });
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (currentPosition==viewPagerData.size()-1){
                        currentPosition = 0 ;
                        viewpager.setCurrentItem(0,false);
                    }else{
                        currentPosition ++;
                        viewpager.setCurrentItem(currentPosition,true);
                    }
                }
            }
        };
    }

    @Override
    public void index(int index) {
//        Toast.makeText(getBaseContext(), String.valueOf(index), Toast.LENGTH_SHORT).show();
    }
}
