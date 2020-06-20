package com.example.thu_helper.ui.home;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Record {
    public String id;
    public String title;
    public String content;
    public String loc;
    public String reward;
    public Date time;

    public Record(String id, String title, String loc, Date time, String reward, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.loc = loc;
        this.time = time;
        this.reward = reward;
    }

    public Record(JSONObject object) throws JSONException, ParseException {
        this.id = object.getString("ID");
        this.title = object.getString("title");
        this.content = object.getString("activity_info");
        this.loc = object.getString("location");
        this.time =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(object.getString("start_time"));
        this.reward = object.getString("reward");
    }
}
