package com.example.thu_helper.ui.register;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thu_helper.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//做和注册页面有关的数据操作
public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();

    public RegisterViewModel(){
    }

    public void registerDataChanged(String email,String name,String password,String rePassword) {
        if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email,null, null,null));
        }
        else if(!isNameValid(name)){
            registerFormState.setValue(new RegisterFormState(null,R.string.invalid_username,null,null));
        }
        else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null,null,R.string.invalid_password,null));
        }
        else if(!checkPassword(password,rePassword)){
            registerFormState.setValue(new RegisterFormState(null,null,null,R.string.invalid_repassword));
        }
        else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isEmailValid(String email){
        if(email == null)
            return false;

        String rule = "\\w+([-+.]\\w+)*@(\\w+\\.)*tsinghua\\.edu\\.cn";
        Pattern pattern = Pattern.compile(rule);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private boolean isPasswordValid(String password){
        return password != null && (password.trim().length() > 5);
    }

    private boolean isNameValid(String name){
        if(name == null){
            return false;
        }
        String RULE = "([\u4e00-\u9fa5a-zA-Z0-9_.?!？！。]+)";
        Pattern pattern = Pattern.compile(RULE);
        Matcher matcher = pattern.matcher(name);
        if(!matcher.matches()){
            return false;
        }
        return true;
    }

    private boolean checkPassword(String password,String rePassword){
        if(password.equals(rePassword)){
            return true;
        }
        else
            return false;
    }

    public MutableLiveData<RegisterFormState> getRegisterFormState(){
        return registerFormState;
    }
}
