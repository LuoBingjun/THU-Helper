package com.example.thu_helper.ui.chatting;

public class ChatMsgEntity
{
    final static int MSG_SEND = 0;
    final static int MSG_ACCEPT = 1;
    private String name;
    private String date;
    private String text;
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

    public ChatMsgEntity(String name,String date,String text,int type){
        this.name = name;
        this.date = date;
        this.text = text;
        this.type = type;
    }

}
