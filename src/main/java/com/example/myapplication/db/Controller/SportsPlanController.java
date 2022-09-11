package com.example.myapplication.db.Controller;

import android.content.Context;

import com.example.myapplication.db.Dao.SportsPlanDao;
import com.example.myapplication.db.entity.SportsPlan;

public class SportsPlanController {
    private Context context;
    private SportsPlanDao sportsPlanDao;

    public SportsPlanController(Context context){
        this.context = context;
        sportsPlanDao = new SportsPlanDao(context);
    }

    /* 功能:根据userId获取用户运动计划
     * 参数:userId
     * 返回值:运动计划
     */
    public SportsPlan getSportsPlan(String userId){
        sportsPlanDao.openDB();
        SportsPlan plan = sportsPlanDao.getSportsPlan(userId);
        sportsPlanDao.closeDB();
        return plan;
    }

    /* 功能:设置用户运动计划
     * 参数:userId，提醒时间，计划消耗的千卡数
     */
    public void setSportsPlan(String userId, String remindTime, int kcalMon, int kcalTue, int kcalWed,
                              int kcalThur, int kcalFri, int kcalSat, int kcalSun){
        sportsPlanDao.openDB();
        sportsPlanDao.setSportsPlan(userId, remindTime,kcalMon, kcalTue, kcalWed, kcalThur, kcalFri, kcalSat, kcalSun);
        sportsPlanDao.closeDB();
    }

//    /* 功能:设置用户计划消耗千卡数
//     * 参数:userId，计划消耗的千卡数
//     */
//    public void setPlanKcal(String userId, int planKcal){
//        sportsPlanDao.openDB();
//        sportsPlanDao.setPlanKcal(userId, planKcal);
//        sportsPlanDao.closeDB();
//    }

    /* 功能:设置用户计划提醒时间
     * 参数:userId，计划提醒时间
     */
    public void setRemindTime(String userId, String RemindTime){
        sportsPlanDao.openDB();
        sportsPlanDao.setRemindTime(userId, RemindTime);
        sportsPlanDao.closeDB();
    }

    public void setKcalMon(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalMon(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public void setKcalTue(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalTue(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public void setKcalWed(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalWed(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public void setKcalThur(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalThur(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public void setKcalFri(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalFri(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public void setKcalSat(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalSat(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public void setKcalSun(String userId, int kcal){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcalSun(userId, kcal);
        sportsPlanDao.closeDB();
    }

    public int getKcalMon(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalMon(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public int getKcalTue(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalTue(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public int getKcalWed(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalWed(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public int getKcalThur(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalThur(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public int getKcalFri(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalFri(userId);
        sportsPlanDao.closeDB();
        return result;
    }
    public int getKcalSat(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalSat(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public int getKcalSun(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getKcalSun(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public void setKcal(String userId, int kcalMon, int kcalTue, int kcalWed,
                        int kcalThur, int kcalFri, int kcalSat, int kcalSun){
        sportsPlanDao.openDB();
        sportsPlanDao.setKcal(userId, kcalMon, kcalTue, kcalWed, kcalThur, kcalFri, kcalSat, kcalSun);
        sportsPlanDao.closeDB();
    }

    public int getKcalByDay(String weekDay, String userId){
        int result = 0;
        switch (weekDay){
            case "Mon":
                result = getKcalMon(userId);
                break;
            case "Tue":
                result = getKcalTue(userId);
                break;
            case "Wed":
                result = getKcalWed(userId);
                break;
            case "Thur":
                result = getKcalThur(userId);
                break;
            case "Fri":
                result = getKcalFri(userId);
                break;
            case "Sat":
                result = getKcalSat(userId);
                break;
            case "Sun":
                result = getKcalSun(userId);
                break;
        }
        return result;
    }

    public int getGoalReachDay(String userId){
        sportsPlanDao.openDB();
        int result = sportsPlanDao.getGoalReachDay(userId);
        sportsPlanDao.closeDB();
        return result;
    }

    public void setGoalReachDay(int day, String userId){
        sportsPlanDao.openDB();
        sportsPlanDao.setGoalReachDay(day, userId);
        sportsPlanDao.closeDB();
    }

    public void addGoalReachDay(String userId){
        sportsPlanDao.openDB();
        sportsPlanDao.addGoalReachDay(userId);
        sportsPlanDao.closeDB();
    }

}
