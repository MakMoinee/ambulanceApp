package com.ambulanceapp.client.models;

import java.util.List;

import lombok.Data;

@Data
public class FullNearbyPlaceResponse {
    List<NearbyPlaceResponse> results;
}
