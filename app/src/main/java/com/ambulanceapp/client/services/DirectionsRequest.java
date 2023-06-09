package com.ambulanceapp.client.services;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.ambulanceapp.client.interfaces.NearbyPlaceListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DirectionsRequest {
    Context mContext;
    private final String directionsURL = "https://maps.googleapis.com/maps/api/directions/json?destination=%s&origin=%s&key=%s";

    public DirectionsRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void getDirections(LatLng currentLocation, LatLng destination, NearbyPlaceListener listener) {
        final String url = String.format(directionsURL, String.format("%s,%s", currentLocation.latitude, currentLocation.longitude), String.format("%s,%s", destination.latitude, destination.longitude), NearbyPlaceRequest.MAP_KEY);
        Log.e("url", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parse the JSON response
                    JSONArray routes = response.getJSONArray("routes");
                    if (routes.length() > 0) {
                        JSONObject route = routes.getJSONObject(0);
                        JSONObject polyline = route.getJSONObject("overview_polyline");
                        String encodedPolyline = polyline.getString("points");

                        // Decode the polyline into a list of LatLng points
                        List<LatLng> polylinePoints = PolyUtil.decode(encodedPolyline);
                        listener.onSuccess(polylinePoints);
//                        // Draw the polyline on the map
//                        PolylineOptions polylineOptions = new PolylineOptions()
//                                .addAll(polylinePoints)
//                                .color(Color.RED);
//                        line = mMap.addPolyline(polylineOptions);
                    }
                } catch (JSONException e) {
                    if (e != null) {
                        Log.e("getDirections_err", e.getLocalizedMessage());
                    }
                    listener.onError();
                }
            }
        }, error -> {
            if (error != null) {
                Log.e("getDirections_err", error.getLocalizedMessage());
            }
            listener.onError();
        });


        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(jsonObjectRequest);
    }
}
