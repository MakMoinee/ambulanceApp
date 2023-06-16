package com.ambulanceapp.client.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenPref {
    Context mContext;
    SharedPreferences pref;

    public TokenPref(Context mContext) {
        this.mContext = mContext;
        this.pref = mContext.getSharedPreferences("token", Context.MODE_PRIVATE);
    }


    public void storeToken(String token) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
        editor.apply();
    }

    public String getToken() {
        return pref.getString("token", "");
    }
}
