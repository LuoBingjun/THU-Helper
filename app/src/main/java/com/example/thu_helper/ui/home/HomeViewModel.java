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
    private MutableLiveData<List<Record>> mData;

    public HomeViewModel() {
        List<Record> list=new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Record record1 = new Record(2 * i, "帮忙取快递"+i,
                    "紫荆14号楼后小树林", new Date(), "最近不在学校，求帮忙取快递" + i);
            list.add(record1);
            Record record2 = new Record(2 * i + 1, "示例订单"+i,
                    "紫荆操场", new Date(), "示例订单内容" + i);
            list.add(record2);
        }
        mData = new MutableLiveData<>();
        mData.setValue(list);
    }

    public LiveData<List<Record>> getData(){
        return mData;
    }
}