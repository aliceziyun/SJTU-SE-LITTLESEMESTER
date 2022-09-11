package com.example.myapplication.db.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.myapplication.db.MyDatabaseHelper;
import com.example.myapplication.db.entity.PathRecord;
import com.example.myapplication.util.DateHelper;
import com.example.myapplication.util.PathUtil;

import java.util.ArrayList;
import java.util.List;

public class PathRecordDao {
    private Context context;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private DateHelper dateHelper;

    public PathRecordDao(Context my_context){
        context = my_context;
    }

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

    public long addRecord(String userId,String pathLine, String startPoint,
                             String endPoint, String date) {
        ContentValues args = new ContentValues();
        args.put("user_id",userId);
        args.put("path_line", pathLine);
        args.put("start_point", startPoint);
        args.put("end_point", endPoint);
        args.put("date", date);
        return db.insert("record", null, args);
    }

    @SuppressLint("Range")
    public ArrayList<PathRecord> getRecordByUserId(String userId){
        ArrayList<PathRecord> pathRecords = null;
        Cursor cursor = db.query("record", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToNext()) {
            PathRecord record = new PathRecord();
            record.setId(cursor.getInt(cursor.getColumnIndex("id")));
            record.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
            record.setDate(cursor.getString(cursor.getColumnIndex("date")));
            String lines = cursor.getString(cursor.getColumnIndex("path_line"));
            record.setPathline(PathUtil.parseLocations(lines));
            record.setStartpoint(PathUtil.parseLocation(cursor.getString(cursor.getColumnIndex("start_point"))));
            record.setEndpoint(PathUtil.parseLocation(cursor.getString(cursor.getColumnIndex("end_point"))));
            pathRecords.add(record);
        }
        return pathRecords;
    }

    @SuppressLint("Range")
    public ArrayList<PathRecord> getRecordByDate(String userId,String date){
        Cursor cursor = db.query("record", null, "user_id=? and date=?",
                new String[]{String.valueOf(userId),date}, null, null, null);
        ArrayList<PathRecord> pathRecords = new ArrayList<PathRecord>();
        while(cursor.moveToNext()) {
            PathRecord record = new PathRecord();
            record.setId(cursor.getInt(cursor.getColumnIndex("id")));
            record.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
            record.setDate(cursor.getString(cursor.getColumnIndex("date")));
            String lines = cursor.getString(cursor.getColumnIndex("path_line"));
            record.setPathline(PathUtil.parseLocations(lines));
            record.setStartpoint(PathUtil.parseLocation(cursor.getString(cursor.getColumnIndex("start_point"))));
            record.setEndpoint(PathUtil.parseLocation(cursor.getString(cursor.getColumnIndex("end_point"))));

            pathRecords.add(record);
        }
        return pathRecords;
    }
}
