package com.example.thu_helper.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.thu_helper.BR;
import com.example.thu_helper.utils.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser extends BaseObservable {
    public String token;
    public String username;
    public String email;
    public String nickname = null;
    public String avater_name = null;
    public String phone = null;
    public byte[] avater = null;

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
        setAvater_name(user.getString("head_portrait"));
        setNickname(user.getString("nickname"));
        setPhone(user.getString("phone"));
    }

    public void update(JSONObject res) throws JSONException {
        this.username = res.getString("ID");
        this.email = res.getString("email");
        setNickname(res.getString("nickname"));
        setAvater_name(res.getString("head_portrait"));
        setPhone(res.getString("phone"));
    }

    public void setNickname(String nickname) {
        if(!nickname.equals(this.nickname)){
            this.nickname = nickname;
            notifyPropertyChanged(BR.nickname);
        }
    }

    public void setPhone(String phone) {
        if(!phone.equals(this.phone)){
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }
    }

    public void setAvater_name(String avater_name) {
        if (!avater_name.equals(this.avater_name)) {
            this.avater_name = avater_name;
            notifyPropertyChanged(BR.avater_name);
            updateAvater();
        }
    }

    private void updateAvater(){
        try{
            String path = Global.url_prefix + "/static/images/" + this.avater_name;
            final LoggedInUser that = this;
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(path)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    that.avater = response.body().bytes();
                }
            });
        }
        catch(Exception e){

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
    public String getAvater_name() {
        return this.avater_name;
    }
}
