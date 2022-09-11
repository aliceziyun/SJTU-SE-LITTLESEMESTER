package com.example.myapplication.db.entity;

public class SportsPlan {
    int id;
    String user_id;
    String remindTime;
    int kcalMon;
    int kcalTue;
    int kcalWed;
    int kcalThur;
    int kcalFri;
    int kcalSat;
    int kcalSun;
    int goalReachDay;


    public String getRemindTime() { return remindTime; }

    public String getUser_id() { return user_id; }


    public void setRemindTime(String remindTime) { this.remindTime = remindTime; }

    public void setUser_id(String user_id) { this.user_id = user_id; }

    public int getKcalMon() {
        return kcalMon;
    }

    public int getKcalTue() {
        return kcalTue;
    }

    public int getKcalWed() {
        return kcalWed;
    }

    public int getKcalThur() {
        return kcalThur;
    }

    public int getKcalFri() {
        return kcalFri;
    }

    public int getKcalSat() {
        return kcalSat;
    }

    public int getKcalSun() {
        return kcalSun;
    }

    public void setKcalMon(int kcalMon) {
        this.kcalMon = kcalMon;
    }

    public void setKcalTue(int kcalTue) {
        this.kcalTue = kcalTue;
    }

    public void setKcalWed(int kcalWed) {
        this.kcalWed = kcalWed;
    }

    public void setKcalThur(int kcalThur) {
        this.kcalThur = kcalThur;
    }

    public void setKcalFri(int kcalFri) {
        this.kcalFri = kcalFri;
    }

    public void setKcalSat(int kcalSat) {
        this.kcalSat = kcalSat;
    }

    public void setKcalSun(int kcalSun) {
        this.kcalSun = kcalSun;
    }

    public int getGoalReachDay() {
        return goalReachDay;
    }

    public void setGoalReachDay(int goalReachDay) {
        this.goalReachDay = goalReachDay;
    }
}
