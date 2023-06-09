package com.ambulanceapp.client.models;

import java.util.List;

import lombok.Data;

@Data
public class NearbyPlaceResponse {
    String business_status;
    Geometry geometry;
    String name;
    List<Photos> photos;
    OpenHours opening_hours;
}
