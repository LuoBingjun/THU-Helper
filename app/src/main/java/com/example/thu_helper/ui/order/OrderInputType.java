package com.example.thu_helper.ui.order;

public class OrderInputType {
    final static public String Title = "标题";
    final static public String BeginTime = "开始时间";
    final static public String EndTime = "结束时间";
    final static public String Location = "地点";
    final static public String Detail = "详情";
    final static public String Money = "赏金";

    public String title;
    public String beginTime;
    public String endTime;
    public String location;
    public String detail;
    public String money;

    public OrderInputType(String title,String beginTime,String endTime,String location,String detail,String money){
        this.title = title;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.location = location;
        this.detail = detail;
        this.money = money;
    }
}
