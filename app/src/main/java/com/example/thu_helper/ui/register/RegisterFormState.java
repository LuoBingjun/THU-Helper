package com.example.thu_helper.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer rePasswordError;

    private String rePasswordString = "请输入正确的密码";
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer emailError,@Nullable Integer usernameError,@Nullable Integer passwordError,@Nullable Integer rePasswordError){
        this.emailError = emailError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.rePasswordError = rePasswordError;
        this.isDataValid = false;
    }
    RegisterFormState(boolean isDataValid){
        this.emailError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.rePasswordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError(){
        return emailError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getRePasswordError(){
        return rePasswordError;
    }

    public String getRePasswordString(){
            return rePasswordString;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
