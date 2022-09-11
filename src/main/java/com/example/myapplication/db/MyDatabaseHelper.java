package com.example.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //存在常量里面，方便之后维护
    public static final String name = "db_metaphor.db";    //数据库名
    public static final int DB_VERSION = 24;     //现版本

    //使用字符串执行sql操作
    public static final String CREATE_USERDATA = "create table if not exists user ("
            + "id text,"
            + "sexual integer,"
            + "username text unique not null,"
            + "password text not null,"
            + "description text,"
            + "img text,"
            + "birthday text,"
            + "tel text,"
            + "model_id text)";

    public static final String CREATE_SPORTSDATA = "create table if not exists sports_data ("
            + "data_id integer primary key autoincrement,"
            + "user_id text not null,"
            + "kcal integer not null,"
            + "speed real,"
            + "course_name text not null,"
            + "start_time text not null,"
            + "duration integer not null,"
            + "sports_type integer not null)";

    public static final String CREATE_COURSE = "create table if not exists Course ("
            + "id integer primary key autoincrement, "
            + "course_name text not null, "
            + "description text not null, "
            + "kcal integer, "
            + "type text, "
            + "duration integer not null, "
            + "img_url text, "
            + "src_url text not null, "
            + "is_favored integer not null)";

    public static final String CREATE_RECORD = "create table if not exists record ("
            + "id integer primary key autoincrement,"
            + "user_id text,"
            + "start_point text,"
            + "end_point text,"
            + "path_line text,"
            + "date text)";

    public static final String CREATE_FAVOR = "create table if not exists Favor ("
            + "id integer primary key autoincrement, "
            + "user_id text not null, "
            + "course_id integer not null, "
            + "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE, "
            + "FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE)";



    public static final String CREATE_SPORTSPLAN = "create table if not exists sports_plan ("
            + "id integer primary key autoincrement, "
            + "user_id text not null, "
            + "remind_time text not null, "
            + "kcal_mon integer not null, "
            + "kcal_tue integer not null, "
            + "kcal_wed integer not null, "
            + "kcal_thur integer not null, "
            + "kcal_fri integer not null, "
            + "kcal_sat integer not null, "
            + "kcal_sun integer not null, "
            + "goal_reach_day integer not null, "
            + "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE)";


    //构造函数
    public MyDatabaseHelper(Context context) {
        super(context, name, null, DB_VERSION);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("myDatabaseHelper", "createDataBase");
        db.execSQL(CREATE_USERDATA);   //执行刚刚的sql语句
        db.execSQL(CREATE_SPORTSDATA);
        db.execSQL(CREATE_COURSE);
        db.execSQL(CREATE_RECORD);
        db.execSQL(CREATE_FAVOR);
        db.execSQL(CREATE_SPORTSPLAN);
    }

    //数据库更新维护
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("myDatabaseHelper", String.valueOf(oldVersion));
        switch (oldVersion) {
            case 20:
                db.execSQL("drop table if exists sports_plan");
                db.execSQL(CREATE_SPORTSPLAN);
            case 21:
            case 22:
            case 23:
                db.execSQL("drop table if exists Course");
                db.execSQL(CREATE_COURSE);
            default:

        }
//        db.execSQL("drop table if exists user");
//        db.execSQL("drop table if exists sports_data");
//        db.execSQL("drop table if exists Course");
//        db.execSQL("drop table if exists record");
//        db.execSQL("drop table if exists Favor");
//        db.execSQL("drop table if exists sports_plan");
//        onCreate(db);
    }
}
