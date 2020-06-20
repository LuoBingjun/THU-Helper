package com.example.thu_helper.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends ViewModel {
    private List<Record> mData;
    private MutableLiveData<String> mTime;
    private MutableLiveData<String> mWord;
    public HomeViewModel() {
        this.mData = new ArrayList<>();
        this.mTime = new MutableLiveData<>();
        this.mWord = new MutableLiveData<>();
//        for (int i = 0; i < 10; i++){
//            mData.add(new Record("123", "清华帮帮忙" + i, "紫荆14号楼小树林", new Date(), "15元", "这里是详情第一段\n这里是详情第二段"));
//        }
//        mData.add(new Record("5d971834b2fc11ea9973525400ea15a1", "清华帮帮忙", "紫荆14号楼小树林", new Date(), "15元", "这里是详情第一段\n这里是详情第二段"));
    }

    public void setTime(String str){
        switch(str){
            case "三小时内":
                this.mTime.setValue("3hours");
                break;
            case "一天内":
                this.mTime.setValue("1day");
                break;
            case "三天内":
                this.mTime.setValue("3days");
                break;
            case "一周内":
                this.mTime.setValue("7days");
                break;
            case "一个月内":
                this.mTime.setValue("30days");
                break;
            default:
                this.mTime.setValue(null);
                break;
        }
    }

    public void setWord(String newVal) {
        if (newVal.isEmpty()){
            mWord.setValue(null);
        }
        else{
            mWord.setValue(newVal);
        }
    }

    public LiveData<String> getTime() {
        return mTime;
    }

    public LiveData<String> getWord() {
        return mWord;
    }

    public List<Record> getData(){
        return mData;
    }
}