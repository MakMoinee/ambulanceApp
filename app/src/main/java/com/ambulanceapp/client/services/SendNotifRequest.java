package com.ambulanceapp.client.services;

import android.content.Context;
import android.util.Log;

import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class SendNotifRequest {
    Context mContext;

    private final String TEMPLATE_MSG = "An Ambulance is near you, please act accordingly";
    private final String MSG_PATH = "/sendMessage";
    private final String MSG_URL = "http://192.168.1.2:8443" + MSG_PATH;


    public SendNotifRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void pushNotification(String deviceToken, FirebaseListener listener) {


        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("token", deviceToken);
            jsonBody.put("msg", TEMPLATE_MSG);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, MSG_URL, jsonBody, response -> {
                try {
                    if (response.getString("message").equals("Successfully Send Message")) {
                        listener.onSuccessAny(null);
                    } else {
                        listener.onError();
                    }
                } catch (JSONException e) {
                    if (e != null) {
                        Log.e("pushNotification_err", e.getLocalizedMessage());
                    }
                    listener.onError();
                }
            }, error -> {
                if (error != null) {
                    Log.e("pushNotification_err", error.getLocalizedMessage());
                }
                listener.onError();
            });

            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            if (e != null) {
                Log.e("pushNotification_err", e.getLocalizedMessage());
            }
            listener.onError();
        }
    }
}
