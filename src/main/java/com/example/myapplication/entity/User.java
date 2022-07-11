package com.example.myapplication.entity;

public class User {
    public int id;
    public int sexual;
    public String username;
    public String password;
    public String img;
    public String tel;
    public String birthday;
    public String description;

    public String getUsername() {
        return username;
    }

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
}
