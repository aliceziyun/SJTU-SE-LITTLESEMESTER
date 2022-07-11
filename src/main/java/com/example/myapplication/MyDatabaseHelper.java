package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //存在常量里面，方便之后维护
    public static final String name = "db_metaphor.db";    //数据库名
    public static final int DB_VERSION = 7;     //现版本

    //使用字符串执行sql操作
    public static final String CREATE_USERDATA = "create table user ("
            + "id integer primary key autoincrement,"
            + "sexual integer,"
            + "username text unique not null,"
            + "password text not null,"
            + "description text,"
            + "img text,"
            + "birthday text,"
            + "tel text,"
            + "model_id text)";

    public static final String CREATE_SPORTSDATA = "create table sports_data ("
            + "data_id integer primary key autoincrement,"
            + "user_id integer not null,"
            + "kcal integer not null,"
            + "speed real,"
            + "course_name text not null,"
            + "start_time text not null,"
            + "end_time text not null,"
            + "sports_type integer not null)";

    public static final String CREATE_COURSE = "create table Course ("
            + "id integer primary key autoincrement, "
            + "course_name text, "
            + "description text, "
            + "kcal integer, "
            + "type text, "
            + "duration integer, "
            + "img_url text, "
            + "src_url text)";

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
    }

    //数据库更新维护
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("myDatabaseHelper", String.valueOf(oldVersion));
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists sports_data");
        db.execSQL("drop table if exists Course");
        onCreate(db);
    }
}
