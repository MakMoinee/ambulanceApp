package com.ambulanceapp.client.interfaces;

public interface FirebaseListener {
    <T> void onSuccessAny(T any);

    void onError();
}
