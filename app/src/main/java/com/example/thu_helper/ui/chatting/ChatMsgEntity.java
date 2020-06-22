package com.example.thu_helper.ui.chatting;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatMsgEntity
{
    final static int MSG_SEND = 0;
    final static int MSG_RECEIVED = 1;
    private String name;
    private String date;
    private String text;
    private String activity_title;
    private int type;

    public String getName(){
        return name;
    }

    public String getDate(){
        return date;
    }

    public String getText(){
        return text;
    }

    public int getType(){
        return type;
    }

    public String getActivityTitle(){return activity_title;}

    public void setName(String name){
        this.name = name;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setText(String text){
        this.text = text;
    }

    public void setType(int type){
        this.type = type;
    }

    public ChatMsgEntity(){}

    public ChatMsgEntity(String name,String date,String text,int type,String activity_title){
        this.name = name;
        this.date = date;
        this.text = text;
        this.type = type;
        this.activity_title = activity_title;
    }

    @NonNull
    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name",name);
            jsonObject.put("date",date);
            jsonObject.put("text",text);
            jsonObject.put("type",type);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
