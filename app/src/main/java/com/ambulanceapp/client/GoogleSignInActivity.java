package com.ambulanceapp.client;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ambulanceapp.client.common.MapForm;
import com.ambulanceapp.client.databinding.ActivityGoogleSigninBinding;
import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.ambulanceapp.client.models.Users;
import com.ambulanceapp.client.preference.UserPref;
import com.ambulanceapp.client.services.FirebaseRequest;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GoogleSignInActivity extends AppCompatActivity {

    ActivityGoogleSigninBinding binding;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private FirebaseAuth mAuth;
    private FirebaseRequest request;
    ActivityResultLauncher<IntentSenderRequest> oneTapLauncher;
    String role = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoogleSigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        request = new FirebaseRequest();
        role = getIntent().getStringExtra("role");
        setSignIn();
    }

    private void setSignIn() {

        oneTapLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {

            if (result.getResultCode() == RESULT_OK) {
                SignInCredential credential = null;
                try {
                    credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();


                    if (idToken != null) {
                        String picURI = "";
                        try {
                            picURI = credential.getProfilePictureUri().toString();
                        } catch (Exception e) {
                            picURI = "";
                        }
                        Users users = new Users.UserBuilder()
                                .setFirstName(credential.getGivenName())
                                .setLastName(credential.getFamilyName())
                                .setEmail(username)
                                .setPassword("default")
                                .setPictureURI(picURI)
                                .setRole(role)
                                .setPhoneNumber(credential.getPhoneNumber())
                                .build();
                        FirebaseRequestBody body = new FirebaseRequestBody.RequestBodyBuilder()
                                .setEmail(username)
                                .setParams(MapForm.convertObjectToMap(users))
                                .setCollectionName(FirebaseRequest.USERS_COLLECTION)
                                .setWhereFromField(FirebaseRequest.EMAIL_STRING)
                                .setWhereValueField(username)
                                .build();

                        request.findAllRequest(body, new FirebaseListener() {
                            @Override
                            public <T> void onSuccessAny(T any) {
                                if (any instanceof QuerySnapshot) {
                                    QuerySnapshot snapshots = (QuerySnapshot) any;
                                    Users u = null;
                                    for (QueryDocumentSnapshot documentSnapshot : snapshots) {
                                        u = documentSnapshot.toObject(Users.class);
                                        if (u != null) {
                                            u.setDocumentID(documentSnapshot.getId());

                                            break;
                                        }
                                    }

                                    if (u != null) {
                                        new UserPref(GoogleSignInActivity.this).storeUser(u);
                                        finish();
                                    } else {
                                        Toast.makeText(GoogleSignInActivity.this, "Failed to sign in account, please try again later", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                } else {
                                    Toast.makeText(GoogleSignInActivity.this, "Failed to sign in account, please try again later", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onError() {
                                request.insertData(body, new FirebaseListener() {
                                    @Override
                                    public <T> void onSuccessAny(T any) {
                                        if (any instanceof String) {
                                            String docID = (String) any;
                                            users.setDocumentID(docID);
                                            new UserPref(GoogleSignInActivity.this).storeUser(users);
                                            finish();
                                        } else {
                                            Toast.makeText(GoogleSignInActivity.this, "Failed to create account, please try again later", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(GoogleSignInActivity.this, "Failed to create account, please try again later", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });

                    } else if (password != null) {

                    }

                } catch (ApiException e) {
                    Log.e("API EXCEPTION", e.getLocalizedMessage());
                    Toast.makeText(GoogleSignInActivity.this, "Failed to login with Google, Please try again later", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                Toast.makeText(GoogleSignInActivity.this, "Failed to login with Google, Please try again later", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        oneTapClient = Identity.getSignInClient(GoogleSignInActivity.this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                ).build();

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(beginSignInResult -> {
                    try {
                        oneTapLauncher.launch(new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build());
                    } catch (Exception e) {
                        Log.e("oneTapClientFail", "Couldn't start One Tap UI: " + e.getLocalizedMessage());

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("oneTapClientFail", e.getLocalizedMessage());
                });

    }
}
