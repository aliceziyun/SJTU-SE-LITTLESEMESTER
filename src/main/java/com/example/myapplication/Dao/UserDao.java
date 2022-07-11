package com.example.myapplication.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.myapplication.MyDatabaseHelper;
import com.example.myapplication.entity.User;

public class UserDao {
    private Context context;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    //构造函数
    public UserDao(Context context) {
        this.context = context;
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

    //添加用户
    public long addUser(User user){
        //先判断是否存在
        if(isExistUser(user.username) == 0)
            return 0;
            //然后再插入
        else {
            Log.i("myDatabase", "success");
            ContentValues values = new ContentValues();
            //插入
            values.put("username", user.username);
            values.put("password", user.password);
            return db.insert("user", null, values);
        }
    }

    //删除用户
    public int deleteUser(Integer id){
        return db.delete("user","id=?",new String[]{String.valueOf(id)});
    }

    //修改用户数据
    public int modifyUser(User user){
        ContentValues values = new ContentValues();
        values.put("username",user.username);
        values.put("password",user.password);
        values.put("sexual",user.sexual);
        values.put("tel",user.tel);
        values.put("description",user.description);
        values.put("birthday",user.birthday);
        //img的修改方式和存储方式待定
        values.put("img",user.img);
        return db.update("user",values,"id=?",new String[]{String.valueOf(user.id)});
    }

    //验证用户
    public int authUser(String username,String password){
        Cursor cursor = db.query("user", null, "username=? and password=?",
                new String[]{username,password}, null, null, null);
        if(cursor.moveToFirst() == false){  //注意这里必须这样写，否则会出错
            cursor.close();
            return -1;
        }
        else{
            @SuppressLint("Range")
            int user_id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return user_id;
        }
    }

    //判断用户是否已存在
    public int isExistUser(String username){
        Cursor cursor = db.query("user", null, "username=?",
                new String[]{username}, null, null, null);
        if(cursor.moveToFirst() == false){  //注意这里必须这样写，否则会出错
            cursor.close();
            return 1;
        }
        else return 0;
    }

    //查找用户
    @SuppressLint("Range")
    public User findUser(Integer id) {
        Cursor cursor = db.query("user", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        User user = new User();
        while (cursor.moveToNext()) {
            user.id = cursor.getInt(cursor.getColumnIndex("id"));
            user.username = cursor.getString(cursor.getColumnIndex("username"));
            //关于password的保密问题
            user.password = cursor.getString(cursor.getColumnIndex("password"));
            user.sexual = cursor.getInt(cursor.getColumnIndex("sexual"));
            user.tel = cursor.getString(cursor.getColumnIndex("tel"));
            user.birthday = cursor.getString(cursor.getColumnIndex("birthday"));
            user.description = cursor.getString(cursor.getColumnIndex("description"));
            user.img = cursor.getString(cursor.getColumnIndex("img"));
        }
        cursor.close();
        return user;
    }
}
