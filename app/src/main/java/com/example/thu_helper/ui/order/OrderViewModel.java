package com.example.thu_helper.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<OrderInputInfo> mOrder;

    public OrderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
        mOrder = new MutableLiveData<>();
        mOrder.setValue(new OrderInputInfo("标题","2019-06-27 17:37:00","2019-06-27 17:38:00"
                ,"紫荆14号楼后小树林","最近不在学校帮忙取快递","5元"));
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<OrderInputInfo> getOrder() { return mOrder; }
    public void setOrder(OrderInputInfo order){ this.mOrder.setValue(order);}

    public void setTitle(String title){
        mOrder.getValue().setTitle(title);
    }

    public void setBeginTime(String beginTime){
        mOrder.getValue().setBeginTime(beginTime);
    }

    public void setEndTime(String endTime){
        mOrder.getValue().setEndTime(endTime);
    }

    public void setLocation(String location){
        mOrder.getValue().setLocation(location);
    }

    public void setMyDetail(String detail){
        mOrder.getValue().setDetail(detail);
    }

    public void setMoney(String money){
        mOrder.getValue().setMoney(money);
    }

    public String getTitle(){
        return mOrder.getValue().getTitle();
    }

    public String getBeginTime(){
        return mOrder.getValue().getBeginTime();
    }

    public String getEndTime(){
        return mOrder.getValue().getEndTime();
    }

    public String getLocation(){
        return mOrder.getValue().getLocation();
    }

    public String getMyDetail(){
        return mOrder.getValue().getDetail();
    }

    public String getMoney(){
        return mOrder.getValue().getMoney();
    }

}