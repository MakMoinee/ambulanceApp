package com.ambulanceapp.client.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ambulanceapp.client.DashboardActivity;
import com.ambulanceapp.client.R;
import com.ambulanceapp.client.common.Common;
import com.ambulanceapp.client.preference.TokenPref;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotifService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "MyNotificationChannel";

    @Override
    public void onNewToken(@NonNull String token) {
        Common.deviceToken = token;
        new TokenPref(this).storeToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);


        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("My Channel Description");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ambulance)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }
}
