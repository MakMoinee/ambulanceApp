package com.ambulanceapp.client.ui.map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ambulanceapp.client.R;
import com.ambulanceapp.client.common.Common;
import com.ambulanceapp.client.common.MapForm;
import com.ambulanceapp.client.databinding.FragmentEnforcerBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.Enforcers;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;

public class EnforcerMapFragment extends Fragment {

    GoogleMap gMap;

    FragmentEnforcerBinding binding;
    Context mContext;

    Boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    LatLng currentLocation;

    private FusedLocationProviderClient providerClient;

    FirebaseRequest request;

    public EnforcerMapFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnforcerBinding.inflate(inflater, null, false);
        providerClient = LocationServices.getFusedLocationProviderClient(mContext);
        request = new FirebaseRequest();
        getLocationPermission();
        initMap();
        return binding.getRoot();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            locationPermissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            gMap = googleMap;
            getCurrentLocation();
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        } else {
            providerClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            new Handler().postDelayed(() -> {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                gMap.addMarker(new MarkerOptions()
                                        .position(currentLocation)
                                        .title("Your Location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                                List<LatLng> circle = Common.createCircle(currentLocation, Common.DEFAULT_RADIUS);
                                Users users = new UserPref(mContext).getUsers();
                                Enforcers enforcers = new Enforcers.EnforcerBuilder()
                                        .setUserID(users.getDocumentID())
                                        .setCircle(new Gson().toJson(circle))
                                        .build();

                                FirebaseRequestBody requestBody = new FirebaseRequestBody.RequestBodyBuilder()
                                        .setCollectionName(FirebaseRequest.ENFORCERS_COLLECTION)
                                        .setDocumentID(users.getDocumentID())
                                        .setParams(MapForm.convertObjectToMap(enforcers))
                                        .build();

                                request.upsertWithUserID(requestBody, new FirebaseListener() {
                                    @Override
                                    public <T> void onSuccessAny(T any) {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });

                            }, 3000);
                        } else {
                            initValues();
                        }
                    });
        }
    }

    private void initValues() {
        new Handler().postDelayed(() -> {
            if (currentLocation == null) {
                Toast.makeText(mContext, "Current Location couldn't detected. Please turn on location services or move to an open space", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }
}
