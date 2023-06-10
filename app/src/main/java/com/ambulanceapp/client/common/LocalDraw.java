package com.ambulanceapp.client.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class LocalDraw {

    public static BitmapDescriptor getDescriptor(Drawable originalDrawable){
        // Define the desired width and height of the marker icon in pixels
        int width = 64;  // Change this value as per your requirement
        int height = 64; // Change this value as per your requirement

        // Resize the original drawable to the desired width and height
        Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) originalDrawable).getBitmap(), width, height, false);

        // Create a BitmapDescriptor from the resized bitmap
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        return bitmapDescriptor;
    }
}
