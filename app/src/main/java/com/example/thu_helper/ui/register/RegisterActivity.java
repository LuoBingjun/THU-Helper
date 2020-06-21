package com.example.thu_helper.ui.register;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.thu_helper.R;
import com.example.thu_helper.data.Result;
import com.example.thu_helper.utils.Global;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;

    private EditText mEmailEdit;
    private EditText mNicknameEdit;
    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private EditText mRePasswardEdit;
    private Button registerButton;
    private ProgressBar registeringProcessBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        mEmailEdit = findViewById(R.id.emailEdit);
        mUsernameEdit = findViewById(R.id.usernameEdit);
        mNicknameEdit = findViewById(R.id.nicknameEdit);
        mPasswordEdit = findViewById(R.id.passwordEdit);
        mRePasswardEdit = findViewById(R.id.repasswordEdit);
        registerButton = findViewById(R.id.registerButton);
        registeringProcessBar = findViewById(R.id.registering);

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(RegisterFormState registerFormState) {
                if(registerFormState == null){
                    return;
                }
                registerButton.setEnabled(registerFormState.isDataValid());
                if(registerFormState.getEmailError() != null){
                    mEmailEdit.setError(getString(registerFormState.getEmailError()));
                }
                if(registerFormState.getUsernameError() != null){
                    mNicknameEdit.setError(getString(registerFormState.getUsernameError()));
                }
                if(registerFormState.getPasswordError() != null){
                    mPasswordEdit.setError(getString(registerFormState.getPasswordError()));
                }
                if(registerFormState.getRePasswordError() != null){
                    mRePasswardEdit.setError(registerFormState.getRePasswordString());
                }
            }
        }

);

        final TextWatcher registerTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(mEmailEdit.getText().toString(), mNicknameEdit.getText().toString()
                , mPasswordEdit.getText().toString(), mRePasswardEdit.getText().toString());
            }
        };

        mEmailEdit.addTextChangedListener(registerTextWatcher);
        mNicknameEdit.addTextChangedListener(registerTextWatcher);
        mPasswordEdit.addTextChangedListener(registerTextWatcher);
        mRePasswardEdit.addTextChangedListener(registerTextWatcher);

        registerButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                new RegisterTask().execute(
                        mEmailEdit.getText().toString(),
                        mNicknameEdit.getText().toString(),
                        mUsernameEdit.getText().toString(),
                        mPasswordEdit.getText().toString());
            }
        });
    }

    private class RegisterTask extends AsyncTask<String, Integer, Result<Boolean>> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作（UI线程）
        @Override
        protected void onPreExecute() {
            registeringProcessBar.setVisibility(View.VISIBLE);
        }

        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果（子线程）
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected Result<Boolean> doInBackground(String... params) {
            String email = params[0];
            String nickname = params[1];
            String username = params[2];
            String password = params[3];
            try {
                OkHttpClient client = new OkHttpClient();
                FormBody formBody = new FormBody
                        .Builder()
                        .add("ID", username)
                        .add("password", password)
                        .add("nickname", nickname)
                        .add("email", email)
                        .build();
                Request request = new Request.Builder()
                        .url(Global.url_prefix + "/user/register")
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return new Result.Success<>(true);
                }
                return new Result.Error(new Exception("注册失败，请检查填写信息后重试"));
            } catch (Exception e) {
                return new Result.Error(new Exception("网络请求失败，请稍后再试", e));
            }
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度（UI线程）
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件（UI线程）
        @Override
        protected void onPostExecute(Result<Boolean> result) {
            registeringProcessBar.setVisibility(View.INVISIBLE);
            if (result instanceof Result.Success) {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), ((Result.Error) result).getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态（UI线程）
        @Override
        protected void onCancelled() {
            registeringProcessBar.setVisibility(View.INVISIBLE);
        }
    }
}
