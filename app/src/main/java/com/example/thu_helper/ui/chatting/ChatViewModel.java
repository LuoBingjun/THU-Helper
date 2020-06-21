package com.example.thu_helper.ui.chatting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<List<ChatMsgEntity>> messages;

    public ChatViewModel(){
        messages = new MutableLiveData<>();
    }
    public MutableLiveData<List<ChatMsgEntity>>  getMessages(){
        return messages;
    }

}
