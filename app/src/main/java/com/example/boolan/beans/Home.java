package com.example.boolan.beans;

import java.io.Serializable;

public class Home implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String[] getPhoto() {
        return photo;
    }

    public void setPhoto(String[] photo) {
        this.photo = photo;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getZnickname() {
        return znickname;
    }

    public void setZnickname(String znickname) {
        this.znickname = znickname;
    }

    public String getZphoto() {
        return zphoto;
    }

    public void setZphoto(String zphoto) {
        this.zphoto = zphoto;
    }

    public String getZtime() {
        return ztime;
    }

    public void setZtime(String ztime) {
        this.ztime = ztime;
    }

    public String getZnumber() {
        return znumber;
    }

    public void setZnumber(String znumber) {
        this.znumber = znumber;
    }

    public String getCommentnumber() {
        return commentnumber;
    }

    public void setCommentnumber(String commentnumber) {
        this.commentnumber = commentnumber;
    }

    public String getZfnumber() {
        return zfnumber;
    }

    public void setZfnumber(String zfnumber) {
        this.zfnumber = zfnumber;
    }

    public String getYnickname() {
        return ynickname;
    }

    public void setYnickname(String ynickname) {
        this.ynickname = ynickname;
    }

    public String getZcontent() {
        return zcontent;
    }

    public void setZcontent(String zcontent) {
        this.zcontent = zcontent;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getYtime() {
        return ytime;
    }

    public void setYtime(String ytime) {
        this.ytime = ytime;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getPdpraise() {
        return pdpraise;
    }

    public void setPdpraise(String pdpraise) {
        this.pdpraise = pdpraise;
    }

    private String id;//文章id
    private String name;//发布文章用户名
    private String head;//发布文章用户头像名
    private String time;//发布文章时间
    private String connect;//发布文章内容
    private String[] photo;//发布的图片
    private String synopsis;//用户简介
    private String znickname;//点赞文章用户名
    private String zphoto;//点赞文章用户头像名
    private String ztime;//点赞文章时间
    private String znumber;//点赞数量
    private String commentnumber;//评论数量
    private String zfnumber;//转发数量
    private String ynickname;
    private String zcontent;
    private String img;
    private String ytime;
    private String u_id;
    private String yid;
    private String follow;
    private String pdpraise;//判断是否点赞
}
