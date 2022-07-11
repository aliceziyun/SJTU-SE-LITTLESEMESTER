package com.example.myapplication.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.MyDatabaseHelper;
import com.example.myapplication.entity.Course;

import java.util.ArrayList;

public class CourseDao {
    private Context context;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public CourseDao(Context context){ this.context = context; }

    //打开数据库
    public void openDB(){
        dbHelper = new MyDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //关闭数据库
    public void closeDB(){
        if (db != null){
            db.close();;
            db = null;
        }
    }

    //插入新课程
    public long addCourse(Course record){
        ContentValues values = new ContentValues();
        values.put("course_name", record.getCourseName());
        values.put("description", record.getDescription());
        values.put("kcal", record.getKcal());
        values.put("duration", record.getDuration());
        values.put("type", record.getType());
        values.put("img_url",record.getImgUrl());
        values.put("src_url", record.getSrcUrl());
        return db.insert("Course", null, values);
    }

    //按种类查询课程并按时间从短到长排列
    public ArrayList<Course> getCourseByType(String type){
        ArrayList<Course> dataList = new ArrayList<Course>();
        Cursor cursor = db.query("Course", null, "type = ?", new String[]{type}, null, null,"duration");
        int resultCounts = cursor.getCount();
        if (resultCounts == 0){
            Log.i("getCourseByType", "resultCount = 0");
            return null;
        }
        while(cursor.moveToNext()){
            Course course = new Course();
            course.setCourseName(cursor.getString(cursor.getColumnIndex("course_name")));
            course.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            course.setType(type);
            course.setImgUrl(cursor.getString(cursor.getColumnIndex("img_url")));
            course.setSrcUrl(cursor.getString(cursor.getColumnIndex("src_url")));
            course.setKcal(cursor.getInt(cursor.getColumnIndex("kcal")));
            course.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
            dataList.add(course);
        }
        closeDB();
        return dataList;
    }

    //清空课程表
    public void clearCourse(){
        db.delete("Course",null, null);
    }
}
