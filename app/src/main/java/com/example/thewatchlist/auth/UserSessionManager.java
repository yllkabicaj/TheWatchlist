package com.example.thewatchlist.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "UserSession";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "user_id";

    public UserSessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int user_id){
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, user_id);
        editor.commit();
    }

    public boolean checkLogin(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public int getUserId(){
        return pref.getInt(KEY_USER_ID, -1);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }
}
