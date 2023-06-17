package com.ambulanceapp.client;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ambulanceapp.client.common.MapForm;
import com.ambulanceapp.client.databinding.ActivityCreateAccountBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding binding;
    String role = "";
    FirebaseRequest request;
    ProgressDialog pdLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        role = getIntent().getStringExtra("role");
        request = new FirebaseRequest();
        pdLoad = new ProgressDialog(CreateAccountActivity.this);
        pdLoad.setMessage("Sending Request ...");
        pdLoad.setCancelable(false);
        setListeners();
    }

    private void setListeners() {
        binding.btnCreateAccount.setOnClickListener(v -> {
            String email = binding.editEmail.getText().toString();
            String password = binding.editPassword.getText().toString();
            String confirmPass = binding.editConfirmPassword.getText().toString();
            String firstName = binding.editFirstName.getText().toString();
            String lastName = binding.editLastName.getText().toString();

            if (email.equals("") || password.equals("") || confirmPass.equals("") || firstName.equals("") || lastName.equals("")) {
                Toast.makeText(CreateAccountActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                if (confirmPass.equals(password)) {
                    pdLoad.show();
                    Users users = new Users.UserBuilder()
                            .setEmail(email)
                            .setPassword(password)
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setRole(role)
                            .build();
                    FirebaseRequestBody body = new FirebaseRequestBody.RequestBodyBuilder()
                            .setEmail(email)
                            .setCollectionName(FirebaseRequest.USERS_COLLECTION)
                            .setParams(MapForm.convertObjectToMap(users))
                            .setWhereFromField(FirebaseRequest.EMAIL_STRING)
                            .setWhereValueField(email)
                            .build();
                    request.insertData(body, new FirebaseListener() {
                        @Override
                        public <T> void onSuccessAny(T any) {
                            pdLoad.dismiss();
                            if (any instanceof String) {
                                String docID = (String) any;
                                users.setDocumentID(docID);
                                Toast.makeText(CreateAccountActivity.this, "Successfully Created Account, Logging in ...", Toast.LENGTH_SHORT).show();
                                new UserPref(CreateAccountActivity.this).storeUser(users);
                                finish();
                            }
                        }

                        @Override
                        public void onError() {
                            pdLoad.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CreateAccountActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
