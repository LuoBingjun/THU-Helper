package com.example.thu_helper.ui.new_order;

public class OrderInputInfo {
    final static public String Title = "标题";
    final static public String BeginTime = "开始时间";
    final static public String EndTime = "结束时间";
    final static public String Location = "地点";
    final static public String Detail = "详情";
    final static public String Money = "报酬";

    private String title;
    private String beginTime;
    private String endTime;
    private String location;
    private String detail;
    private String money;

    public OrderInputInfo(String title, String beginTime, String endTime, String location, String detail, String money){
        this.title = title;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.location = location;
        this.detail = detail;
        this.money = money;
    }

    public OrderInputInfo(){
        this.title = null;
        this.beginTime = null;
        this.endTime = null;
        this.location = null;
        this.detail = null;
        this.money = null;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setBeginTime(String beginTime){
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }

    public void setMoney(String money){
        this.money = money;
    }

    public String getTitle(){
        return this.title;
    }

    public String getBeginTime(){
        return this.beginTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public String getLocation(){
        return this.location;
    }

    public String getDetail(){
        return this.detail;
    }

    public String getMoney(){
        return this.money;
    }
}
