package com.example.myapplication.notice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

import java.util.Calendar;
import java.util.TimeZone;

/**
 *
 * @ClassName: AlarmReceiver
 * @Description: 闹铃时间到了会进入这个广播，这个时候可以做一些该做的业务。
 * @author HuHood
 * @date 2013-11-25 下午4:44:30
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();

        //通知渠道
        String channelId = "计划通知渠道";
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("是时候运动啦")
                .setContentText("执行运动计划，过健康生活")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background))   //设置大图标
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(channelId, "测试渠道名称", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        //计算通知id，保证不重复
        Calendar calendar = Calendar.getInstance();
        int id = calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);

        notificationManager.notify(id, notification);

    }
}
