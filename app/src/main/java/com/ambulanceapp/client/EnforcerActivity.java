package com.ambulanceapp.client;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.drawerlayout.widget.DrawerLayout;

import com.ambulanceapp.client.databinding.ActivityEnforcerBinding;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.preference.UserPref;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class EnforcerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityEnforcerBinding binding;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnforcerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        }
        return false;
    }
}