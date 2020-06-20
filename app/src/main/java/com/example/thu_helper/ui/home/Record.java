package com.example.thu_helper.ui.home;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
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

    public String formatTime() {
        String res = "马上";
        Instant instNow = Instant.now();
        Instant instTime = time.toInstant();
        Duration duration = Duration.between(instNow, instTime);
        if (duration.toDays() > 0){
            res = duration.toDays() + "天后";
        }
        else if (duration.toHours() > 0) {
            res = duration.toHours() + "小时后";
        }
        else if (duration.toMinutes() > 0) {
            res = duration.toMinutes() + "分钟后";
        }
        else if (duration.toDays() < 0){
            res = -duration.toDays() + "天前";
        }
        else if (duration.toHours() < 0) {
            res = -duration.toHours() + "小时前";
        }
        else if (duration.toMinutes() < 0) {
            res = -duration.toMinutes() + "分钟前";
        }

        return res;
    }
}
