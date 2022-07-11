package com.example.myapplication.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.myapplication.MyDatabaseHelper;
import com.example.myapplication.entity.SportsData;
import com.example.myapplication.entity.SportsType;
import com.example.myapplication.util.DateHelper;

import java.util.ArrayList;

public class SportsDataDao {
    private Context context;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private DateHelper dateHelper;

    public SportsDataDao(Context context){this.context = context;}

    public void openDB() throws SQLiteException {    //打开数据库
        dbHelper = new MyDatabaseHelper(context); //创建一个数据库对象
        try {
            db = dbHelper.getWritableDatabase();    //如果数据库存在则直接打开，否则则新建一个
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }

    //关闭数据库
    public void closeDB() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    //添加新纪录
    public long addSportsData(SportsData record){
        ContentValues values = new ContentValues();

        //插入
        values.put("user_id",record.userId);
        values.put("kcal",record.kcal);
        values.put("speed",record.speed);
        values.put("course_name",record.courseName);
        values.put("start_time",record.startTime);
        values.put("end_time",record.duration);
        switch (record.sportsType){
            case FITNESS:
                values.put("sports_type",0);
                break;
            case RUNNING:
                values.put("sports_type",1);
                break;
            case YOGA:
                values.put("sports_type",2);
                break;
        }
        return db.insert("sports_data", null, values);
    }

    //查询健身记录
    @SuppressLint("Range")
    public ArrayList<SportsData> getFitnessRecord(SportsType type, int userId){
        ArrayList<SportsData> dataList = new ArrayList<SportsData>();
        String queryString = new String();
        String[] arg = null;
        switch (type){
            case FITNESS:   //健身数据
                queryString = "select * from sports_data where user_id=? and sports_type=?";
                arg = new String[]{String.valueOf(userId),String.valueOf(0)};
                break;
            case RUNNING:  //跑步数据
                queryString = "select * from sports_data where user_id=? and sports_type=?";
                arg = new String[]{String.valueOf(userId),String.valueOf(1)};
                break;
            case YOGA:  //瑜伽数据
                queryString = "select * from sports_data where user_id=? and sports_type=?";
                arg = new String[]{String.valueOf(userId),String.valueOf(2)};
                break;
            case DAY:   //按日查询
                dateHelper = new DateHelper();
                String date = dateHelper.getDay();
                Log.i("posData",date);
                queryString = "select * from sports_data where user_id=? and start_time like ?";
                arg = new String[]{String.valueOf(userId),date};
                break;
            case WEEK:   //按日查询
                dateHelper = new DateHelper();
                String[] week = dateHelper.getWeek();
                dataList = getWeekRecord(week,userId);
                return dataList;
            default:
        }

        Cursor cursor = db.rawQuery(queryString,arg);
        int resultCounts = cursor.getCount();  //记录数
        if (resultCounts == 0 ) {
            Log.i("posData","null");
            return null;
        }
        else {
            while (cursor.moveToNext()) {
                //组装数据
                SportsData sportsData = new SportsData();
                sportsData.sportsType = SportsType.FITNESS;
                sportsData.userId = userId;
                sportsData.courseName = cursor.getString(cursor.getColumnIndex("course_name"));
                Log.i("posData",sportsData.courseName);
                sportsData.kcal = cursor.getInt(cursor.getColumnIndex("kcal"));
                sportsData.speed = cursor.getInt(cursor.getColumnIndex("speed"));
                sportsData.startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                sportsData.duration = cursor.getInt(cursor.getColumnIndex("duration"));
                dataList.add(sportsData);
            }
        }
        closeDB();
        return dataList;
    }

    @SuppressLint("Range")
    private ArrayList<SportsData> getWeekRecord(String[] week, int userId){
        ArrayList<SportsData> dataList = new ArrayList<SportsData>();
        for(int i = 0;i < 7;i++){
            String queryString = "select * from sports_data where user_id=? and start_time like ?";
            String[] arg = new String[]{String.valueOf(userId),week[i]};
            Cursor cursor = db.rawQuery(queryString,arg);
            int resultCounts = cursor.getCount();  //记录数
            if (resultCounts == 0 ) {
                Log.i("posData",week[i]);
            }
            else {
                while (cursor.moveToNext()) {
                    //组装数据
                    SportsData sportsData = new SportsData();
                    sportsData.sportsType = SportsType.FITNESS;
                    sportsData.userId = userId;
                    sportsData.courseName = cursor.getString(cursor.getColumnIndex("course_name"));
                    Log.i("posData",sportsData.courseName);
                    sportsData.kcal = cursor.getInt(cursor.getColumnIndex("kcal"));
                    sportsData.speed = cursor.getInt(cursor.getColumnIndex("speed"));
                    sportsData.startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                    sportsData.duration = cursor.getInt(cursor.getColumnIndex("duration"));
                    dataList.add(sportsData);
                }
            }
        }
        return dataList;
    }
}
