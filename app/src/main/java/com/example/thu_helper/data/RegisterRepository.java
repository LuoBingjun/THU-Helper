package com.example.thu_helper.data;

//做和注册有关的数据操作
public class RegisterRepository {
    private static volatile RegisterRepository INSTANCE = null;

    private RegisterRepository(){}

    public static RegisterRepository getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RegisterRepository();
        }
        return INSTANCE;
    }

    public boolean register(String email,String name,String password){
        //此步应当检查数据库中是否有重名的用户，有则返回错误，否则插入用户数据库中
        return true;
    }
}
