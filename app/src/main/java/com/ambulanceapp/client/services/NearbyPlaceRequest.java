package com.ambulanceapp.client.services;

import android.content.Context;
import android.util.Log;

import com.ambulanceapp.client.interfaces.NearbyPlaceListener;
import com.ambulanceapp.client.models.FullNearbyPlaceResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NearbyPlaceRequest {

    Context mContext;
    private final String nearbyPlaceLink = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&keyword=%s&radius=1500&type=hospital&key=%s";
    private final String HOSPITAL_KEYWORD = "hospitals";
    public static final String MAP_KEY = "AIzaSyBWg7I7EJZ8iAbUJ6Z3s2yKnqWbN0qiWWs";

    public NearbyPlaceRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void getNearbyPlace(LatLng currentLocation, NearbyPlaceListener listener) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, String.format(nearbyPlaceLink, String.format("%s,%s", currentLocation.latitude, currentLocation.longitude), HOSPITAL_KEYWORD, MAP_KEY), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("")) {
                    listener.onError();
                } else {
                    try {
                        FullNearbyPlaceResponse nearbyPlaceResponse = new Gson().fromJson(response, new TypeToken<FullNearbyPlaceResponse>() {
                        }.getType());
                        if (nearbyPlaceResponse != null) {
                            listener.onSuccess(nearbyPlaceResponse);
                        } else {
                            listener.onError();
                        }
                    } catch (Exception e) {
                        if (e != null) {
                            Log.e("getNearbyPlace_err", e.getLocalizedMessage());
                        }
                        listener.onError();
                    }
                }
            }
        }, error -> {
            if (error != null) {
                Log.e("getNearbyPlace_err", error.getLocalizedMessage());
            }
            listener.onError();
        });

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }
}
