package com.example.thu_helper.ui.list;

import androidx.lifecycle.ViewModel;

import com.example.thu_helper.ui.home.Record;

import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends ViewModel {
    private int type;
    private List<Record> mData;

    public ListViewModel() {
        this.mData = new ArrayList<>();
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public List<Record> getData(){
        return mData;
    }
}