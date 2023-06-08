package com.ambulanceapp.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ambulanceapp.client.databinding.ActivityMainBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.parser.CommonParser;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Users mUsers = new UserPref(MainActivity.this).getUsers();
        if (mUsers.getDocumentID() != null && mUsers.getDocumentID() != "") {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        request = new FirebaseRequest();
        setListeners();
    }

    private void setListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString();
            String password = binding.editPassword.getText().toString();

            if (email.equals("") || password.equals("")) {
                Toast.makeText(MainActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {

                FirebaseRequestBody requestBody = new FirebaseRequestBody.RequestBodyBuilder()
                        .setCollectionName(FirebaseRequest.USERS_COLLECTION)
                        .setWhereFromField(FirebaseRequest.EMAIL_STRING)
                        .setWhereValueField(email)
                        .build();

                request.findAllRequest(requestBody, new FirebaseListener() {
                    @Override
                    public <T> void onSuccessAny(T any) {
                        if (any instanceof QuerySnapshot) {
                            QuerySnapshot queryDocumentSnapshots = (QuerySnapshot) any;
                            List<Users> tmpList = CommonParser.parseUsers(queryDocumentSnapshots);
                            if (tmpList.size() > 0) {
                                Boolean isValid = false;
                                for (Users users : tmpList) {
                                    if (users.getPassword().equals(password)) {
                                        new UserPref(MainActivity.this).storeUser(users);
                                        isValid = true;
                                        break;
                                    }
                                }

                                if (isValid) {
                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.btnGoogleSign.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Users mUsers = new UserPref(MainActivity.this).getUsers();
        if (mUsers.getDocumentID() != null && mUsers.getDocumentID() != "") {
            Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }
}