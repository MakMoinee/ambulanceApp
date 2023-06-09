package com.ambulanceapp.client.models;


import com.ambulanceapp.client.services.NearbyPlaceRequest;

import lombok.Data;

@Data
public class Photos {
    int height;
    String photo_reference;
    int width;


    public String getPhotoUrl() {
        String fUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=720&photo_reference=%s&key=%s", this.photo_reference, NearbyPlaceRequest.MAP_KEY);
        return fUrl;
    }
}
