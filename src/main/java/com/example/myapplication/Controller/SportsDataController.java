package com.example.myapplication.Controller;

import android.content.Context;

import com.example.myapplication.Dao.SportsDataDao;
import com.example.myapplication.entity.SportsData;
import com.example.myapplication.entity.SportsType;

import java.util.ArrayList;

public class SportsDataController {
    private SportsDataDao sportsDataDao;
    private Context my_context;

    public SportsDataController(Context context){
        my_context = context;
        sportsDataDao = new SportsDataDao(my_context);
    }

    /*功能:打包运动记录
     *参数:用户id,课程名称,消耗kcal,开始时间,结束时间,运动类型,配速(非跑步则为-1)
     *返回值:运动记录实体
     */
    public SportsData packData(int userId, String courseName, int kcal, String startTime,
                               int duration, SportsType sportsType, float speed){
        SportsData sportsData = new SportsData();
        sportsData.userId = userId;
        sportsData.courseName = courseName;
        sportsData.kcal = kcal;
        sportsData.startTime = startTime;
        sportsData.duration = duration;
        sportsData.sportsType = sportsType;
        sportsData.speed = speed;

        return sportsData;
    }

    /*功能:添加新的运动记录
     *参数:运动记录
     *返回值:成功返回非负值,失败返回-1
     */
    public long addNewRecord(SportsData record){
        sportsDataDao.openDB();
        long tmp = sportsDataDao.addSportsData(record);
        sportsDataDao.closeDB();
        return tmp;
    }

    /*功能:根据type返回运动记录表
     *参数:运动type,用户id
     *返回值:运动记录列表
     */
    public ArrayList<SportsData> getRecordByKind(SportsType type, int userId){
        sportsDataDao.openDB();
        return sportsDataDao.getFitnessRecord(type,userId);
    }
}
