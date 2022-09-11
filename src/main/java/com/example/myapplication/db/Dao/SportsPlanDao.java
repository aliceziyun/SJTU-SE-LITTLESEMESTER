package com.example.myapplication.db.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.db.MyDatabaseHelper;
import com.example.myapplication.db.entity.SportsPlan;

public class SportsPlanDao {
    private Context context;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SportsPlanDao(Context context){ this.context = context; }

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

    //获取运动计划
    public SportsPlan getSportsPlan(String userId){
        Cursor cursor = db.query("sports_plan", null, "user_id = ?", new String[]{userId},
                null, null, null);
        if (cursor.getCount() == 0){
            return null;
        }
        SportsPlan plan = new SportsPlan();
        cursor.moveToNext();
        plan.setUser_id(cursor.getString(cursor.getColumnIndex("user_id")));
        plan.setKcalMon(cursor.getInt(cursor.getColumnIndex("kcal_mon")));
        plan.setKcalTue(cursor.getInt(cursor.getColumnIndex("kcal_tue")));
        plan.setKcalWed(cursor.getInt(cursor.getColumnIndex("kcal_wed")));
        plan.setKcalThur(cursor.getInt(cursor.getColumnIndex("kcal_thur")));
        plan.setKcalFri(cursor.getInt(cursor.getColumnIndex("kcal_fri")));
        plan.setKcalSat(cursor.getInt(cursor.getColumnIndex("kcal_sat")));
        plan.setKcalSun(cursor.getInt(cursor.getColumnIndex("kcal_sun")));
        plan.setRemindTime(cursor.getString(cursor.getColumnIndex("remind_time")));
        plan.setGoalReachDay(cursor.getInt(cursor.getColumnIndex("goal_reach_day")));
        return plan;
    }

    public long addSportsPlan(String userId, String time, int kcalMon, int kcalTue, int kcalWed,
                              int kcalThur, int kcalFri, int kcalSat, int kcalSun){
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("kcal_mon",kcalMon);
        values.put("kcal_tue",kcalTue);
        values.put("kcal_wed",kcalWed);
        values.put("kcal_thur",kcalThur);
        values.put("kcal_fri",kcalFri);
        values.put("kcal_sat",kcalSat);
        values.put("kcal_sun",kcalSun);
        values.put("remind_time", time);
        values.put("goal_reach_day", 0);
        return db.insert("sports_plan", null, values);
    }

    //设置运动计划
    public void setSportsPlan(String userId, String time, int kcalMon, int kcalTue, int kcalWed,
                              int kcalThur, int kcalFri, int kcalSat, int kcalSun){
        Cursor cursor = db.query("sports_plan", null, "user_id = ?", new String[]{userId},
                null, null, null);
        // 用户之前未设置过，新插入
        if (cursor.getCount() == 0){
            addSportsPlan(userId, time, kcalMon, kcalTue, kcalWed, kcalThur, kcalFri, kcalSat, kcalSun);
        }
        // 用户之前设置过，更新
        else{
            setKcal(userId, kcalMon, kcalTue, kcalWed, kcalThur, kcalFri, kcalSat, kcalSun);
            setRemindTime(userId, time);
        }
    }

//    public void setPlanKcal(String userId, int kcal){
//        ContentValues values = new ContentValues();
//        values.put("plan_kcal", kcal);
//        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
//    }

    public void setRemindTime(String userId, String time){
        ContentValues values = new ContentValues();
        values.put("remind_time", time);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }


    public void setKcal(String userId, int kcalMon, int kcalTue, int kcalWed,
                        int kcalThur, int kcalFri, int kcalSat, int kcalSun){
        setKcalMon(userId, kcalMon);
        setKcalTue(userId, kcalTue);
        setKcalWed(userId, kcalWed);
        setKcalThur(userId, kcalThur);
        setKcalFri(userId, kcalFri);
        setKcalSat(userId, kcalSat);
        setKcalSun(userId, kcalSun);
    }

    public void setKcalMon(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_mon", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setKcalTue(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_tue", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setKcalWed(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_wed", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setKcalThur(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_thur", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setKcalFri(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_fri", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setKcalSat(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_sat", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setKcalSun(String userId, int kcal){
        ContentValues values = new ContentValues();
        values.put("kcal_sun", kcal);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void setEverydayKcal(String userId, int kcal){
        setKcalMon(userId, kcal);
        setKcalTue(userId, kcal);
        setKcalWed(userId, kcal);
        setKcalThur(userId, kcal);
        setKcalFri(userId, kcal);
        setKcalSat(userId, kcal);
        setKcalSun(userId, kcal);
    }

    public int getKcalMon(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_mon"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_mon"));
        return kcal;
    }

    public int getKcalTue(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_tue"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_tue"));
        return kcal;
    }

    public int getKcalWed(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_wed"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_wed"));
        return kcal;
    }

    public int getKcalThur(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_thur"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_thur"));
        return kcal;
    }

    public int getKcalFri(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_fri"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_fri"));
        return kcal;
    }

    public int getKcalSat(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_sat"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_sat"));
        return kcal;
    }

    public int getKcalSun(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"kcal_sun"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int kcal;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        kcal = cursor.getInt(cursor.getColumnIndex("kcal_sun"));
        return kcal;
    }
    
    public int getGoalReachDay(String userId){
        Cursor cursor = db.query("sports_plan", new String[]{"goal_reach_day"}, "user_id = ?",
                new String[]{userId}, null, null, null);
        int result;
        if (cursor.getCount() == 0){
            return 0;
        }
        cursor.moveToNext();
        result = cursor.getInt(cursor.getColumnIndex("goal_reach_day"));
        return result;
    }

    public void setGoalReachDay(int day, String userId){
        ContentValues values = new ContentValues();
        values.put("goal_reach_day", day);
        db.update("sports_plan", values, "user_id = ?", new String[]{userId});
    }

    public void addGoalReachDay(String userId){
        int day = getGoalReachDay(userId);
        setGoalReachDay(day + 1, userId);
    }

}
