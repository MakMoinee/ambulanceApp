package com.ambulanceapp.client.services;

import androidx.annotation.NonNull;

import com.ambulanceapp.client.common.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotifService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        Common.deviceToken = token;
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
    }
}
