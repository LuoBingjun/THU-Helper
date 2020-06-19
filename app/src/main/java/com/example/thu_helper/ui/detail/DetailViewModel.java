package com.example.thu_helper.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailViewModel extends ViewModel {
    private MutableLiveData<String> id;
    private MutableLiveData<RecordDetail> record;

    public DetailViewModel(){
        id = new MutableLiveData<>();
        record = new MutableLiveData<>();
    }

    public MutableLiveData<String> getId() {
        return id;
    }

    public MutableLiveData<RecordDetail> getRecord() {
        return record;
    }
}
