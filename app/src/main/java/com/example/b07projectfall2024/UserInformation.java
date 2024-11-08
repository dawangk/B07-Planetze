package com.example.b07projectfall2024;

import com.google.firebase.firestore.DocumentSnapshot;

public class UserInformation {
    public static DocumentSnapshot UserInfo;
    public static void setUserInfo(DocumentSnapshot NewUserInfo){
        UserInfo = NewUserInfo;
    }

    public static DocumentSnapshot getUserInfo(){
        return UserInfo;
    }


}
