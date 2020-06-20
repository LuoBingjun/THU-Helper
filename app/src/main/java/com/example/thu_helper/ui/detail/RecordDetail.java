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
    public String reward;
    public String publisher_id;
    public String publisher_nickname;
    public String acceptor_id = null;
    public String acceptor_nickname = null;
    public Date start_time;
    public Date end_time;
    public int state;

    public RecordDetail(JSONObject res) throws JSONException, ParseException {
        this.id = res.getString("ID");
        this.content = res.getString("activity_info");
        this.start_time =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(res.getString("start_time"));
        this.end_time =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(res.getString("end_time"));
        this.title = res.getString("title");
        this.state = res.getInt("state");
        this.reward = res.getString("reward");
        this.loc = res.getString("location");

        this.publisher_id = res.getString("publisher_id");
        this.publisher_nickname = res.getString("publisher_nickname");
        if (!res.get("acceptor_id").equals(null)){
            this.acceptor_id = res.getString("acceptor_id");
        }
        if (!res.get("acceptor_nickname").equals(null)){
            this.acceptor_nickname = res.getString("acceptor_nickname");
        }
    }
}

