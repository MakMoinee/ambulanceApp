package com.ambulanceapp.client.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.ambulanceapp.client.models.Users;

public class UserPref {
    Context mContext;
    SharedPreferences pref;

    public UserPref(Context mContext) {
        this.mContext = mContext;
        this.pref = this.mContext.getSharedPreferences("users", Context.MODE_PRIVATE);
    }

    public void storeUser(Users users) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("documentID", users.getDocumentID());
        editor.putString("email", users.getEmail());
        editor.putString("password", users.getPassword());
        editor.putString("firstName", users.getFirstName());
        editor.putString("middleName", users.getMiddleName());
        editor.putString("lastName", users.getLastName());
        editor.putString("address", users.getAddress());
        editor.putString("birthDate", users.getBirthDate());
        editor.putString("role", users.getRole());
        String picURI ="";
        try{
            picURI = users.getPictureURI();
        }catch (Exception e){
            picURI = "";
        }
        editor.putString("pictureURI", picURI);
        editor.commit();
        editor.apply();
    }

    public Users getUsers() {
        Users users = new Users.UserBuilder()
                .setDocumentID(pref.getString("documentID", ""))
                .setEmail(pref.getString("email", ""))
                .setPassword(pref.getString("password", ""))
                .setFirstName(pref.getString("firstName", ""))
                .setMiddleName(pref.getString("middleName", ""))
                .setLastName(pref.getString("lastName", ""))
                .setAddress(pref.getString("address", ""))
                .setBirthDate(pref.getString("birthDate", ""))
                .setPictureURI(pref.getString("pictureURI", ""))
                .setRole(pref.getString("role", ""))
                .build();

        return users;
    }
}
