package com.example.thu_helper.data;

import androidx.annotation.Nullable;

import com.example.thu_helper.R;
import com.example.thu_helper.data.model.LoggedInUser;
import com.example.thu_helper.utils.Global;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.provider.Settings.System.getString;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try {
            // TODO: handle loggedInUser authentication
            OkHttpClient client = new OkHttpClient();
//            RequestBody body = new FormBody.Builder().add("username", username).add("password", password).build();
            Request request = new Request.Builder()
                    .url(Global.url_prefix + "/user/login?ID=" + username + "&password=" + password)
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                JSONObject res = new JSONObject(response.body().string());
                LoggedInUser user = new LoggedInUser(res);
                return new Result.Success<>(user);
            }
            return new Result.Error(new IOException("Login Error"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
