package com.ambulanceapp.client;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ambulanceapp.client.databinding.ActivityGoogleSigninBinding;

public class GoogleSignInActivity extends AppCompatActivity {

    ActivityGoogleSigninBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleSigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
