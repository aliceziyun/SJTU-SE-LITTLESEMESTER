package com.example.myapplication.db.Dao;

import static com.autonavi.aps.amapapi.restruct.e.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.autonavi.aps.amapapi.restruct.e;
import com.example.myapplication.db.MyDatabaseHelper;
import com.example.myapplication.db.entity.Course;

import java.util.ArrayList;
import java.util.List;

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
        values.put("is_favored", record.isFavored());
        return db.insert("Course", null, values);
    }

    public Course getCourseById(int courseId){
        Cursor cursor = db.query("Course", null, "id = ?", new String[]{String.valueOf(courseId)}, null, null, null);
        int resultCounts = cursor.getCount();
//        Log.d("count", "getCourseById: here " + resultCounts);
        if (resultCounts == 0){
            Log.i("getCourseByType", "resultCount = 0");
            return null;
        }
        Course course = new Course();
        cursor.moveToFirst();
        course.setId(cursor.getInt(cursor.getColumnIndex("id")));
        course.setCourseName(cursor.getString(cursor.getColumnIndex("course_name")));
        course.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        course.setType(cursor.getString(cursor.getColumnIndex("type")));
        course.setImgUrl(cursor.getString(cursor.getColumnIndex("img_url")));
        course.setSrcUrl(cursor.getString(cursor.getColumnIndex("src_url")));
        course.setKcal(cursor.getInt(cursor.getColumnIndex("kcal")));
        course.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
        course.setFavored(cursor.getInt(cursor.getColumnIndex("is_favored")));
        return course;
    }

    //按种类查询课程并按时间从短到长排列
    public ArrayList<Course> getCourseByType(String type){
        ArrayList<Course> dataList = new ArrayList<Course>();
        Cursor cursor = db.query("Course", null, "type = ?", new String[]{type}, null, null,"duration");
//        System.out.println(cursor);
        int resultCounts = cursor.getCount();
        if (resultCounts == 0){
            Log.i("getCourseByType", "resultCount = 0");
            return null;
        }
        while(cursor.moveToNext()){
            Course course = new Course();
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("id"));
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("course_name"));
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("description"));
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("img_url"));
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("src_url"));
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("kcal"));
            Log.d("cursor", "getCourseByType: "  + cursor.getColumnIndex("duration"));
            course.setId(cursor.getInt(cursor.getColumnIndex("id")));
            course.setCourseName(cursor.getString(cursor.getColumnIndex("course_name")));
            course.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            course.setType(type);
            course.setImgUrl(cursor.getString(cursor.getColumnIndex("img_url")));
            course.setSrcUrl(cursor.getString(cursor.getColumnIndex("src_url")));
            course.setKcal(cursor.getInt(cursor.getColumnIndex("kcal")));
            course.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
            course.setFavored(cursor.getInt(cursor.getColumnIndex("is_favored")));
            dataList.add(course);
        }
        closeDB();
        return dataList;
    }

    //清空课程表
    public void clearCourse(){
        db.delete("Course",null, null);
    }

    //收藏课程
//    public void favorCourse(int courseId){
//        ContentValues values = new ContentValues();
//        values.put("is_favored", 1);
//        db.update("Course", values, "id = ?", new String[]{ String.valueOf(courseId) });
//    }
//
//    //取消收藏
//    public void  disfavorCourse(int courseId){
//        ContentValues values = new ContentValues();
//        values.put("is_favored", 0);
//        db.update("Course", values, "id = ?", new String[]{ String.valueOf(courseId) });
//    }

//    public int checkFavor(int courseId){
//        Course course = getCourseById(courseId);
//        return course.isFavored();
//    }

    public String getSrcById(int courseId){
        Course course = getCourseById(courseId);
        return course.getSrcUrl();
    }

    public int checkFavor(String userId, int courseId){
        Cursor cursor = db.query("Favor", null, "user_id = ? and course_id = ?",
                new String[]{userId, String.valueOf(courseId)}, null, null, null);
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || resultCounts == 1){
            return resultCounts;
        }
        else{
            Log.e("table Favor", "Error data");
            return -1;
        }
    }

    public long favorCourse(String userId, int courseId){
        if (checkFavor(userId, courseId) == 1) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("course_id", courseId);
        return db.insert("Favor", null, values);
    }

    public void  disfavorCourse(String userId, int courseId){
        if (checkFavor(userId, courseId) == 0){
            return;
        }
        db.delete("Favor", "user_id = ? and course_id = ?",
                new String[] { userId, String.valueOf(courseId) });
        return;

    }

    public ArrayList<Course> getFavorCourse(String userId){
        Cursor cursor = db.query("Favor", null, "user_id = ?",
                new String[]{userId}, null, null, null);
        int resultCount = cursor.getCount();
        if (resultCount == 0){
            Log.i("getFavorCourse", "resultCount = 0");
            return null;
        }
        ArrayList<Course> result = new ArrayList<>();
        while(cursor.moveToNext()) {
            Course course = new Course();
            int courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
            course = getCourseById(courseId);
            result.add(course);
        }
        return result;
    }

    public ArrayList<Course> getCourseByCondition(String type, int min ,int max, String input){
        int minSec = min * 60;
        int maxSec = max * 60;
        Cursor cursor = db.query("Course", null,
                "type = ? and duration > ? and duration < ?",
                new String[]{type, String.valueOf(minSec), String.valueOf(maxSec)}, null, null, null);
        int resultCount = cursor.getCount();
        if (resultCount == 0){
            Log.i("getByCondition", "resultCount = 0");
            return null;
        }
        ArrayList<Course> result = new ArrayList<>();
        while(cursor.moveToNext()) {
            String courseName = cursor.getString(cursor.getColumnIndex("course_name"));
            if (courseName.indexOf(input) != -1){
                Course course = new Course();
                course.setId(cursor.getInt(cursor.getColumnIndex("id")));
                course.setCourseName(courseName);
                course.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                course.setType(type);
                course.setImgUrl(cursor.getString(cursor.getColumnIndex("img_url")));
                course.setSrcUrl(cursor.getString(cursor.getColumnIndex("src_url")));
                course.setKcal(cursor.getInt(cursor.getColumnIndex("kcal")));
                course.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                course.setFavored(cursor.getInt(cursor.getColumnIndex("is_favored")));
                result.add(course);
            }
        }
        return result;
    }
}




