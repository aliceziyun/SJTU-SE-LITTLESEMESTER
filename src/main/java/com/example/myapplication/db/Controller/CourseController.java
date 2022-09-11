package com.example.myapplication.db.Controller;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.db.Dao.CourseDao;
import com.example.myapplication.db.entity.Course;

import java.util.ArrayList;

public class CourseController {
    private CourseDao courseDao;
    private Context context;

    public CourseController(Context context){
        this.context = context;
        courseDao = new CourseDao(this.context);
    }

    /*功能:打包课程
     *参数:课程名称,课程描述，消耗kcal,课程类型,课程时长，封面url，视频url，
     *返回值:课程实体
     */
    public Course packCourse(String courseName, String description, int kcal, String type,
                             int duration, String imgUrl ,String srcUrl, int isFavored){
        Course course = new Course();
        course.setCourseName(courseName);
        course.setDescription(description);
        course.setKcal(kcal);
        course.setType(type);
        course.setDuration(duration);
        course.setImgUrl(imgUrl);
        course.setSrcUrl(srcUrl);
        course.setFavored(isFavored);
        Log.d("dao", "packCourse: pack ok");
        return course;
    }

    /*功能:添加新的课程
     *参数:课程
     *返回值:成功返回非负值,失败返回-1
     */
    public long addCourse(Course course){
        courseDao.openDB();
        long result = courseDao.addCourse(course);
        courseDao.closeDB();
        return result;
    }

    /*功能:根据type返回课程列表，按时长从短到长排序
     *参数:课程type
     *返回值:课程列表
     */
    public ArrayList<Course> getCourseByType(String type){
        courseDao.openDB();
        return courseDao.getCourseByType(type);
    }

    /*功能:清空课程表
     *调试用
     */
    public void clearCourse(){
        courseDao.openDB();
        courseDao.clearCourse();
        courseDao.closeDB();
    }

    /* 功能：收藏课程
       参数：课程id
     */
    public void favorCourse(String userId, int courseId){
        courseDao.openDB();
        courseDao.favorCourse(userId, courseId);
        courseDao.closeDB();
    }
//
    /* 功能：取消收藏课程
       参数：课程id
     */
    public void disfavorCourse(String userId, int courseId){
        courseDao.openDB();
        courseDao.disfavorCourse(userId, courseId);
        courseDao.closeDB();
    }

    /* 功能：判断是否是收藏课程
       参数：用户id, 课程id
     */
    public int checkFavor(String userId, int courseId){
        courseDao.openDB();
        int result = courseDao.checkFavor(userId, courseId);
        courseDao.closeDB();
        return result;
    }

    public String getSrcById(int courseId){
        courseDao.openDB();
        String src  = courseDao.getSrcById(courseId);
        courseDao.closeDB();
        return src;
    }

    public Course getCourseById(int courseId){
        courseDao.openDB();
        Course course = courseDao.getCourseById(courseId);
        courseDao.closeDB();
        return course;
    }

    public ArrayList<Course> getFaovrCourse(String userId){
        courseDao.openDB();
        return courseDao.getFavorCourse(userId);
    }

    public ArrayList<Course> getCourseByCondition(String type, int min, int max, String input){
        courseDao.openDB();
        return courseDao.getCourseByCondition(type, min, max, input);
    }


}
