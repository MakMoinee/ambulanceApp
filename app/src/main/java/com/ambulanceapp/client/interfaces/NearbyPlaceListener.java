package com.ambulanceapp.client.interfaces;

public interface NearbyPlaceListener {
    <T> void onSuccess(T any);

    void onError();
}
