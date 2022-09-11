package com.example.myapplication.db.entity;

import java.util.List;
import java.util.Map;

public class Dynamic{
    private String id;
    private String fromUser; //发布者
    private String theme; //主题
    private String content; //内容
    private String date;    //发布时间
    private List<String> image; //图片url集合
    private Map<String,Boolean> likes; //点赞人集合
    private Integer likesCount; //点赞数
    private Integer commentCount; //评论数

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getFromUserId() {
        return fromUser;
    }

    public String getTheme() {
        return theme;
    }

    public String getContent() {
        return content;
    }

    public List<String> getImage() {
        return image;
    }

    public Map<String,Boolean> getLikes() {
        return likes;
    }

    public String getDate(){ return date;}

    public void setFromUserId(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setLikes(Map<String,Boolean> likes) {
        this.likes = likes;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public void setDate(String date){ this.date = date;}

    public Integer getLikesCount() {

        return likesCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
