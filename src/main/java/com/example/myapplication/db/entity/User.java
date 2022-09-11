package com.example.myapplication.db.entity;

public class User{
    private String id;
    private int sexual;
    private String username;
    private String password;
    private String img;
    private String tel;
    private String birthday;
    private String description;
    private int totalTime;

    public String getId(){return id;}

    public void setId(String id){this.id = id;}

    public String getPassword(){return password;}

    public void setPassword(String password){this.password = password;}

    public String getUserName(){return username;}

    public void setUserName(String userName) {this.username = userName;}

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return tel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getSexual() {
        return sexual;
    }

    public void setSexual(int sexual) {
        this.sexual = sexual;
    }

    public int getTotalTime(){return totalTime;}

    public void setTotalTime(int setTotalTime) {this.totalTime += setTotalTime;}

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User u = (User)o;
            return this.getId().equals(u.getId());
        }
        return super.equals(o);
    }
}
