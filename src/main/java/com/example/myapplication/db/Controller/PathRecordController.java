package com.example.myapplication.db.Controller;

import android.content.Context;

import com.example.myapplication.db.Dao.PathRecordDao;
import com.example.myapplication.db.entity.PathRecord;

import java.util.ArrayList;
import java.util.List;

public class PathRecordController {
    private PathRecordDao pathRecordDao;
    private Context context;

    public PathRecordController(Context my_context){
        context = my_context;
        pathRecordDao = new PathRecordDao(my_context);
    }


    /*功能:向数据库中存储记录
     *参数:mId,userId，距离，持续时长，平均配速，轨迹途经点，轨迹开始点，轨迹终点，日期
     *返回值:成功返回行数，不成功返回-1
     */
    public long addRecord(String userId, String pathLine, String startPoint,
                             String endPoint, String date) {
        pathRecordDao.openDB();
        long tmp = pathRecordDao.addRecord(userId,pathLine,startPoint,endPoint,date);
        pathRecordDao.closeDB();
        return tmp;
    }

    /*功能:按照userId查询记录
     *参数:userId
     *返回值:轨迹实体的list
     */
    public List<PathRecord> getRecordByUserId(String userId){
        pathRecordDao.openDB();
        List<PathRecord> pathRecords = pathRecordDao.getRecordByUserId(userId);
        pathRecordDao.closeDB();
        return pathRecords;
    }

    /*功能:按照日期查询记录
     *参数:日期字符串
     *返回值:轨迹实体的list
     */
    public ArrayList<PathRecord> getRecordByDate(String userId, String date){
        pathRecordDao.openDB();
        ArrayList<PathRecord> pathRecords = pathRecordDao.getRecordByDate(userId,date);
        pathRecordDao.closeDB();
        return pathRecords;
    }
}
