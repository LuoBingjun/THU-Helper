package com.example.thu_helper.ui.home;

import java.util.Date;

public class Record {
    public int id;
    public String title;
    public String content;
    public String loc;
    public Date time;

    public Record(int id, String title, String loc, Date time, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.loc = loc;
        this.time = time;
    }
}
