package com.example.thu_helper.ui.detail;

import java.util.Date;

public class RecordDetail {
    public int id;
    public String title;
    public String content;
    public String loc;
    public Date time;
    public int state;

    public RecordDetail(int id, String title, String loc, Date time, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.loc = loc;
        this.time = time;
        this.state = 1;
    }
}

