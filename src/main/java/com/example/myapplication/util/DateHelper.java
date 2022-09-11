package com.example.myapplication.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    //格式：2022-7-22 2022-12-03

    /*功能:获取系统当前时间
     *返回值:字符串 格式为年-月-日 时:分:秒
     */
    public String getNowTime(){
        Date date = new Date();
        String str = date.toString();
        Log.i("DateHelper",str);
        String[] arr = str.split(" ");
        String year = arr[5];
        String day = arr[2];
        String month = convertMonth(arr[1]);
        String time = arr[3];
        String result = year+"-"+month+"-"+day+" "+time;
        return result;
    }

    /*功能:获取当前日所在的一周
     *返回值:字符串数组，周一~周日的日期 格式 年-月-日
     */
    public String[] getWeek(){
        String[] week = new String[7];
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取本周的周一日期
        Date date = getThisWeekMonDay();
        cal.setTime(date);
        int day = 0;
        for (int i = 0; i <7; i++) {
            day = cal.get(Calendar.DATE);
            if(i==0){
                cal.set(Calendar.DATE, day + i);
            }else{
                cal.set(Calendar.DATE, day + 1);
            }
            String dayAfter = dateFormat.format(cal.getTime());
            String[] arr = dayAfter.split(" ");
            String[] tmp = arr[0].split("-");
            if(tmp[1]!="10"||tmp[1]!="11"||tmp[1]!="12")
                tmp[1] = tmp[1].substring(1,2);
            String store = tmp[0]+"-"+tmp[1]+"-"+tmp[2];
            week[i] = store;
        }

        return week;
    }

    /*功能:获取当前日期
     *返回值:字符串数组 格式 年-月-日
     */
    public String getDay(){
        Date date = new Date();
        String str = date.toString();
        String[] arr = str.split(" ");
        String year = arr[5];
        String day = arr[2];
        String month = convertMonth(arr[1]);
        String result = year+"-"+month+"-"+day;
        Log.i("getDate",result);
        return result;
    }

    /*功能:将时间字符串转换为秒
     *返回值:一共的秒数
     */
    public int timeToSec(String time){
        Log.i("DateHelper",time);
        String arr[] = time.split(":");
//        if(arr[0].charAt(0) == '0'){
//            arr[0] = arr[0].substring(1,1);
//        }
        String hour = arr[0];

//        if(arr[1].charAt(0) == '0'){
//            arr[1] = arr[1].substring(1,1);
//        }
        String minute = arr[1];

//        if(arr[2].charAt(0) == '0'){
//            arr[2] = arr[2].substring(1,1);
//        }
        String sec = arr[2];
        return Integer.parseInt(hour) * 3600 + Integer.parseInt(minute) * 60 + Integer.parseInt(sec);
    }

    /*功能:将秒数转换为时间
     *返回值:时间字符串
     */
    public String secToTime(int sec){
        int int_hour = sec / 3600;
        int int_minute = sec % 3600 / 60;
        int int_second = sec % 3600 % 60;
        String hour = String.valueOf(int_hour);
        String minute = String.valueOf(int_minute);
        String second = String.valueOf(int_second);
        if (0 <= int_hour && int_hour <= 9){
            hour = "0" + hour;
        }
        if (0 <= int_minute && int_minute <= 9){
            minute = "0" + minute;
        }
        if (0 <= int_second && int_second <= 9){
            second = "0" + second;
        }

        return hour + ":" + minute + ":" + second;
    }

    private Date getThisWeekMonDay(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    private String convertMonth(String month){
        switch (month){
            case "Jan":
                return "1";
            case "Feb":
                return "2";
            case "Mar":
                return "3";
            case "Apr":
                return "4";
            case "May":
                return "5";
            case "Jun":
                return "6";
            case "Jul":
                return "7";
            case "Aug":
                return "8";
            case "Sep":
                return "9";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
            default:
        }
        return "";
    }

    /**
     * 获取当前日期是星期几
     * @param date
     * @return
     */
    public String getWeekOfDate(Date date) {
        String[] weekDays = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }
}
