package com.example.thu_helper.data.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
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

    public String getNickname() {
        return nickname;
    }
}
