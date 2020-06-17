package com.example.thu_helper.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<OrderInputType> mOrder;

    public OrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
        mOrder = new MutableLiveData<>();
        mOrder.setValue(new OrderInputType("标题","06-27 17:37","06-27 17:38"
                ,"紫荆14号楼后小树林","最近不在学校帮忙取快递","5元"));
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<OrderInputType> getOrder() { return mOrder; }
}