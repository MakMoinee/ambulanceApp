package com.ambulanceapp.client;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ambulanceapp.client.common.Common;
import com.ambulanceapp.client.common.MapForm;
import com.ambulanceapp.client.databinding.ActivityEnforcerBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.preference.TokenPref;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;
import com.ambulanceapp.client.ui.map.EnforcerMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class EnforcerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityEnforcerBinding binding;
    DrawerLayout drawer;

    Fragment fragment;
    FragmentManager fm;
    FragmentTransaction ft;

    Boolean locationPermissionGranted = false;

    FirebaseRequest request;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnforcerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        request = new FirebaseRequest();
        setSupportActionBar(binding.appBarEnforcer.toolbar);
        drawer = binding.drawerLayout;
        binding.navView.setNavigationItemSelectedListener(this);
        View headView = binding.navView.getHeaderView(0);
        Users users = new UserPref(EnforcerActivity.this).getUsers();
        TextView txtEmail = headView.findViewById(R.id.txtEmail);
        TextView txtName = headView.findViewById(R.id.txtName);
        ImageView imgProfile = headView.findViewById(R.id.imgPicture);
        txtEmail.setText(users.getEmail());
        txtName.setText(String.format("%S %S", users.getFirstName(), users.getLastName()));
        if (users.getPictureURI() != null && users.getPictureURI() != "") {
            Uri uri = Uri.parse(users.getPictureURI());
            Picasso.get().load(uri).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.user);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, binding.appBarEnforcer.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                toggle.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                toggle.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                toggle.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                toggle.onDrawerStateChanged(newState);
            }
        });
        toggle.syncState();
        setTitle("Home");
        getLocationPermission();
        String token = Common.deviceToken;
        if (token.equals("")) {
            token = new TokenPref(EnforcerActivity.this).getToken();
        }
        updateUserToken(token);
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(EnforcerActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(EnforcerActivity.this,
                    new String[]{ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enforcer, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EnforcerActivity.this);
            DialogInterface.OnClickListener dListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        updateUserToken("");
                        new UserPref(EnforcerActivity.this).storeUser(new Users());
                        Toast.makeText(EnforcerActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EnforcerActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            };
            mBuilder.setMessage("Are you sure you want to log out?")
                    .setNegativeButton("Yes, proceed", dListener)
                    .setPositiveButton("No", dListener)
                    .setCancelable(false)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.nav_map) {
            getLocationPermission();
            if (locationPermissionGranted) {
                setTitle("Maps");
                fragment = new EnforcerMapFragment(EnforcerActivity.this);
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fragment, fragment);
                ft.commit();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else {
                Toast.makeText(EnforcerActivity.this, "Allow location permission first", Toast.LENGTH_SHORT).show();
            }

        } else if (item.getItemId() == R.id.nav_home) {

        }
        return false;
    }

    private void updateUserToken(String token) {
        Users users = new UserPref(EnforcerActivity.this).getUsers();

        users.setToken(token);

        Log.e("user", new Gson().toJson(users));
        FirebaseRequestBody body = new FirebaseRequestBody.RequestBodyBuilder()
                .setCollectionName(FirebaseRequest.USERS_COLLECTION)
                .setParams(MapForm.convertObjectToMap(users))
                .setDocumentID(users.getDocumentID())
                .build();
        request.upsertWithUserID(body, new FirebaseListener() {
            @Override
            public <T> void onSuccessAny(T any) {
                Log.e("success_update_token", "true");
            }

            @Override
            public void onError() {
                Log.e("error_update_token", "true");
            }
        });
    }
}