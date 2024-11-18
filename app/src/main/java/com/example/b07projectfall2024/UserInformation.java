package com.example.b07projectfall2024;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class UserInformation {
    public static HashMap<String, Object> UserInfo;
    public static void setUserInfo(DataSnapshot NewUserInfo){
        UserInfo = (HashMap<String, Object>) NewUserInfo.getValue();
    }

    public static HashMap<String, Object> getUserInfo(){
        return UserInfo;
    }


}
