package com.example.thu_helper.ui.detail;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDetail {
    public String id;
    public String title;
    public String content;
    public String loc;
    public Date start_time;
    public Date end_time;
    public int state;

//    public RecordDetail(String id, String title, String loc, Date time, String content) {
//        this.id = id;
//        this.title = title;
//        this.content = content;
//        this.loc = loc;
//        this.time = time;
//        this.state = 1;
//    }

    public RecordDetail(JSONObject res) throws JSONException, ParseException {
        this.id = res.getString("ID");
        this.content = res.getString("activity_info");
        this.start_time =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(res.getString("start_time"));
        this.end_time =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(res.getString("end_time"));
        this.title = res.getString("title");
        this.state = 1;
    }
}

