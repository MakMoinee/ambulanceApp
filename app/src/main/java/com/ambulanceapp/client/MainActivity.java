package com.ambulanceapp.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ambulanceapp.client.databinding.ActivityMainBinding;
import com.ambulanceapp.client.databinding.DialogSignInRoleBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.parser.CommonParser;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseRequest request;

    DialogSignInRoleBinding signInRoleBinding;

    AlertDialog signRoleAlert;
    String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Users mUsers = new UserPref(MainActivity.this).getUsers();
        if (mUsers.getDocumentID() != null && mUsers.getDocumentID() != "") {
            if (mUsers.getRole().equals("ambulance")) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else if (mUsers.getRole().equals("enforcer")) {
                Intent intent = new Intent(MainActivity.this, EnforcerActivity.class);
                startActivity(intent);
                finish();
            }
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

        binding.btnCreateAccount.setOnClickListener(v -> {
            signInRoleBinding = DialogSignInRoleBinding.inflate(getLayoutInflater(), null, false);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setView(signInRoleBinding.getRoot());
            initDialogViews();
            setCreateAccountDialogListeners();
            signRoleAlert = mBuilder.create();
            signRoleAlert.setCancelable(true);
            signRoleAlert.show();
        });

        binding.btnGoogleSign.setOnClickListener(v -> {

            signInRoleBinding = DialogSignInRoleBinding.inflate(getLayoutInflater(), null, false);
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            mBuilder.setView(signInRoleBinding.getRoot());
            initDialogViews();
            setDialogListeners();
            signRoleAlert = mBuilder.create();
            signRoleAlert.setCancelable(true);
            signRoleAlert.show();
        });
    }

    private void setCreateAccountDialogListeners() {
        signInRoleBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        signInRoleBinding.btnProceed.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
            intent.putExtra("role", selectedRole);
            startActivity(intent);
        });
    }

    private void initDialogViews() {
        List<String> optionsList = new ArrayList<>();
        optionsList.add("ambulance");
        optionsList.add("enforcer");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, optionsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signInRoleBinding.spinner.setAdapter(adapter);
    }

    private void setDialogListeners() {
        signInRoleBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        signInRoleBinding.btnProceed.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            intent.putExtra("role", selectedRole);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Users mUsers = new UserPref(MainActivity.this).getUsers();
        if (mUsers.getDocumentID() != null && mUsers.getDocumentID() != "") {
            Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
            if (mUsers.getRole().equals("ambulance")) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else if (mUsers.getRole().equals("enforcer")) {
                Intent intent = new Intent(MainActivity.this, EnforcerActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }
}