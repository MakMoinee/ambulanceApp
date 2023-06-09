package com.ambulanceapp.client.ui.hospitals;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.ambulanceapp.client.databinding.FragmentHospitalsBinding;
import com.ambulanceapp.client.interfaces.NearbyPlaceListener;
import com.ambulanceapp.client.models.FullNearbyPlaceResponse;
import com.ambulanceapp.client.models.NearbyPlaceResponse;
import com.ambulanceapp.client.services.DirectionsRequest;
import com.ambulanceapp.client.services.NearbyPlaceRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalFragment extends Fragment {

    FragmentHospitalsBinding binding;

    Context mContext;
    GoogleMap gMap;
    LatLng currentLocation;

    private FusedLocationProviderClient providerClient;
    Boolean locationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private NearbyPlaceRequest request;
    List<Marker> markerList = new ArrayList<>();
    List<LatLng> listOfHospitalLocations = new ArrayList<>();
    Polyline line = null;
    ProgressDialog pdLoad;
    DirectionsRequest directionsRequest;

    public HospitalFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHospitalsBinding.inflate(inflater, container, false);
        providerClient = LocationServices.getFusedLocationProviderClient(mContext);
        directionsRequest = new DirectionsRequest(mContext);
        pdLoad = new ProgressDialog(mContext);
        pdLoad.setMessage("Loading Directions ...");
        pdLoad.setCancelable(false);
        request = new NearbyPlaceRequest(mContext);
        initMap(binding.getRoot());
        return binding.getRoot();
    }

    private void initMap(View root) {
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

                                request.getNearbyPlace(currentLocation, new NearbyPlaceListener() {
                                    @Override
                                    public <T> void onSuccess(T any) {
                                        if (any instanceof FullNearbyPlaceResponse) {
                                            FullNearbyPlaceResponse fullNearbyPlaceResponse = (FullNearbyPlaceResponse) any;
                                            if (fullNearbyPlaceResponse.getResults().size() > 0) {
                                                List<NearbyPlaceResponse> results = removeDuplicates(fullNearbyPlaceResponse.getResults());
                                                for (NearbyPlaceResponse resp : results) {
                                                    LatLng latLng = new LatLng(resp.getGeometry().getLocation().getLat(), resp.getGeometry().getLocation().getLng());
                                                    Marker marker = gMap.addMarker(new MarkerOptions()
                                                            .position(latLng)
                                                            .title(resp.getName())
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                                    markerList.add(marker);
                                                    addListenersToMarkers();
                                                    listOfHospitalLocations.add(latLng);
                                                }
                                            } else {
                                                Toast.makeText(mContext, "There are no nearby hospital, please take a walk or drive to the nearest possible accessible location ", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(mContext, "Failed to retrieve nearby hospitals", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }, 3000);

                        } else {
                            initValues();
                        }
                    });
        }

    }

    @SuppressLint("PotentialBehaviorOverride")
    private void addListenersToMarkers() {
        gMap.setOnMarkerClickListener(marker -> {
            if (marker.getTitle().equalsIgnoreCase("Your Location")) {
//                binding.relativePopup.setVisibility(View.GONE);
//                selectedPlace = "";
            } else {
                binding.btnGetDirections.setVisibility(View.VISIBLE);
                binding.btnGetDirections.setOnClickListener(v -> {
                    pdLoad.show();
                    directionsRequest.getDirections(currentLocation, marker.getPosition(), new NearbyPlaceListener() {
                        @Override
                        public <T> void onSuccess(T any) {
                            pdLoad.dismiss();

                            if (any instanceof List<?>) {
                                List<?> tmpList = (List<?>) any;
                                if (tmpList.size() > 0) {
                                    binding.btnGetDirections.setVisibility(View.GONE);
                                    if (line != null) line.remove();
                                    List<LatLng> polyLines = (List<LatLng>) any;
                                    PolylineOptions polylineOptions = new PolylineOptions()
                                            .addAll(polyLines)
                                            .color(Color.RED);
                                    line = gMap.addPolyline(polylineOptions);
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                                }
                            }


                        }

                        @Override
                        public void onError() {
                            pdLoad.dismiss();
                            Toast.makeText(mContext, "Failed to load directions", Toast.LENGTH_SHORT).show();
                        }
                    });


                });
//                binding.relativePopup.setVisibility(View.VISIBLE);
//                binding.txtLocation.setText(marker.getTitle());
//                binding.txtLatLng.setText(String.format("Latitude:%s , Longitude:%s ", marker.getPosition().latitude, marker.getPosition().longitude));
//                selectedPlace = marker.getTitle();
            }

            return false;
        });
    }

    private List<NearbyPlaceResponse> removeDuplicates(List<NearbyPlaceResponse> results) {
        List<NearbyPlaceResponse> uniqueList = new ArrayList<>();
        Map<String, NearbyPlaceResponse> uniqueMap = new HashMap<>();
        for (NearbyPlaceResponse res : results) {
            if (uniqueMap != null) {
                if (uniqueMap.containsKey(res.getName())) {
                    continue;
                }
            }
            uniqueMap.put(res.getName(), res);
        }
        for (Map.Entry<String, NearbyPlaceResponse> r : uniqueMap.entrySet())
            uniqueList.add(r.getValue());

        return uniqueList;
    }

    private void initValues() {
        new Handler().postDelayed(() -> {
            if (currentLocation == null) {
                Toast.makeText(mContext, "Current Location couldn't detected. Please turn on location services or move to an open space", Toast.LENGTH_SHORT).show();

            }
        }, 5000);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }


}
