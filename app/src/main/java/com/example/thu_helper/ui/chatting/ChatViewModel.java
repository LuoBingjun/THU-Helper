package com.example.thu_helper.ui.chatting;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private MutableLiveData<List<ChatMsgEntity>> messages;
    private MutableLiveData<LocationData> otherLocation;

    public ChatViewModel(){
        messages = new MutableLiveData<>();
        otherLocation = new MutableLiveData<>();
    }
    public MutableLiveData<List<ChatMsgEntity>>  getMessages(){
        return messages;
    }

    public MutableLiveData<LocationData> getOtherLocation(){
        return otherLocation;
    }

    public static class LocationData {
        public double latitude;
        public double longitude;
        public String name;

        public LocationData(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }

        public LocationData(JSONObject res) throws JSONException {
            this.latitude = res.getDouble("latitude");
            this.longitude = res.getDouble("longitude");
            this.name = res.getString("name");
        }
    }

}
