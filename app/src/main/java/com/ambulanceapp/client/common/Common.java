package com.ambulanceapp.client.common;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static final double DEFAULT_RADIUS = 1000;

    public static List<LatLng> createCircle(LatLng center, double radius) {
        List<LatLng> circle = new ArrayList<>();
        int numPoints = 100; // Number of points to approximate the circle

        double centerLat = center.latitude;
        double centerLng = center.longitude;
        double angle;

        for (int i = 0; i < numPoints; i++) {
            angle = 2 * Math.PI * i / numPoints;
            double x = centerLat + radius * Math.cos(angle);
            double y = centerLng + radius * Math.sin(angle);
            circle.add(new LatLng(x, y));
        }

        return circle;
    }
}
