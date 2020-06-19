package com.example.thu_helper.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.thu_helper.BR;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser extends BaseObservable {
    public String token;
    public String username;
    public String email;
    public String nickname;
    public String avater;
    public String phone;

    public LoggedInUser(String username, String nickname, String token) {
        this.username = username;
        this.nickname = nickname;
        this.nickname = nickname;
        this.token = token;
    }

    public LoggedInUser(JSONObject res) throws JSONException {
        this.token = res.getString("token");
        JSONObject user = res.getJSONObject("user");
        this.username = user.getString("ID");
        this.email = user.getString("email");
        this.nickname = user.getString("nickname");
        this.avater = user.getString("head_portrait");
        this.phone = user.getString("phone");
    }

    public void update(JSONObject res) throws JSONException {
        this.username = res.getString("ID");
        this.email = res.getString("email");
        setNickname(res.getString("nickname"));
        setAvater(res.getString("head_portrait"));
        setPhone(res.getString("phone"));
    }

    public void setNickname(String nickname) {
        if(!this.nickname.equals(nickname)){
            this.nickname = nickname;
            notifyPropertyChanged(BR.nickname);
        }
    }

    public void setPhone(String phone) {
        if(!this.phone.equals(phone)){
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }
    }

    public void setAvater(String avater) {
        if (!this.avater.equals(avater)) {
            this.avater = avater;
            notifyPropertyChanged(BR.avater);
        }
    }

    @Bindable
    public String getUsername() {
        return this.username;
    }

    @Bindable
    public String getNickname() {
        return this.nickname;
    }

    @Bindable
    public String getPhone() {
        return this.phone;
    }

    @Bindable
    public String getAvater() {
        return this.avater;
    }
}
