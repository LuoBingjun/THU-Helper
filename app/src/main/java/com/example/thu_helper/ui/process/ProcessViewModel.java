package com.example.thu_helper.ui.process;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thu_helper.ui.home.Record;

import java.util.ArrayList;
import java.util.List;

public class ProcessViewModel extends ViewModel {
    private MutableLiveData<List<Record>> mDataPublish;
    private MutableLiveData<List<Record>> mDataAccept;

    public ProcessViewModel() {
        mDataPublish = new MutableLiveData<>();
        mDataAccept = new MutableLiveData<>();
    }

    public MutableLiveData<List<Record>> getDataPublish() {
        return mDataPublish;
    }

    public MutableLiveData<List<Record>> getDataAccept() {
        return mDataAccept;
    }
}