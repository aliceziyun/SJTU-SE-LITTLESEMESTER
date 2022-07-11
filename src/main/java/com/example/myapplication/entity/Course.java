package com.example.myapplication.entity;

public class Course {
    int id;
    String courseName;
    String description;
    int kcal;
    String type;
    int duration;
    String imgUrl;
    String srcUrl;

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDuration(int duration) { this.duration = duration; }


    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getKcal() {
        return kcal;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDescription() {
        return description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public int getDuration() { return duration; }

    public String getSrcUrl() {
        return srcUrl;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
