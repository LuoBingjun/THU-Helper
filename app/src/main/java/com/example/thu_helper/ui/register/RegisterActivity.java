package com.example.thu_helper.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.thu_helper.R;
import com.example.thu_helper.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel registerViewModel;

    private EditText registerEmail;
    private EditText registerName;
    private EditText registerPassword;
    private EditText rePassward;
    private Button registerButton;
    private ProgressBar registeringProcessBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        registerEmail = findViewById(R.id.registerEmail);
        registerName = findViewById(R.id.registerName);
        registerPassword = findViewById(R.id.registerPassword);
        rePassward = findViewById(R.id.rePassword);
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
                    registerEmail.setError(getString(registerFormState.getEmailError()));
                }
                if(registerFormState.getUsernameError() != null){
                    registerName.setError(getString(registerFormState.getUsernameError()));
                }
                if(registerFormState.getPasswordError() != null){
                    registerPassword.setError(getString(registerFormState.getPasswordError()));
                }
                if(registerFormState.getRePasswordError() != null){
                    rePassward.setError(registerFormState.getRePasswordString());
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
                registerViewModel.registerDataChanged(registerEmail.getText().toString(),registerName.getText().toString()
                ,registerPassword.getText().toString(),rePassward.getText().toString());
            }
        };

        registerEmail.addTextChangedListener(registerTextWatcher);
        registerName.addTextChangedListener(registerTextWatcher);
        registerPassword.addTextChangedListener(registerTextWatcher);
        rePassward.addTextChangedListener(registerTextWatcher);

        registerButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                registeringProcessBar.setVisibility(View.VISIBLE);
                System.out.println("点击注册按钮");
                if(registerViewModel.register(registerEmail.getText().toString(),registerName.getText().toString()
                        ,registerPassword.getText().toString())){
                    finish();
                };
            }
        });
    }
}
