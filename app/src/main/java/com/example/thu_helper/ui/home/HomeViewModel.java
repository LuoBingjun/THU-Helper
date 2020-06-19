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
//    private MutableLiveData<List<Record>> mData;

    private List<Record> mData;
    public HomeViewModel() {
        mData = new ArrayList<>();
    }

    public List<Record> getData(){
        return mData;
    }
}