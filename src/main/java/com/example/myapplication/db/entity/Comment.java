package com.example.myapplication.db.entity;

public class Comment {
    private String dynamicId; //动态id
    private User fromUser; //评论者
    private User toUser; //被评论者
    private String content; //内容
    private String date;    //日期

    public String getDynamic() {
        return dynamicId;
    }
    public User getFromUser() {
        return fromUser;
    }
    public User getToUser() {
        return toUser;
    }
    public String getContent() {
        return content;
    }
    public void setDynamic(String dynamic) {
        this.dynamicId = dynamic;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
}
