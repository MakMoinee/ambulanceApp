package com.ambulanceapp.client.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseRequest {

    public static final String USERS_COLLECTION = "users";
    public static final String ENFORCERS_COLLECTION = "enforcers";
    public static final String AMBULANCE_REFERENCE = "ambulance";
    public static final String EMAIL_STRING = "email";
    public static final String USER_ID_STRING = "userID";
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static final String privateKey = "-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDPBXSmBBAdj7GT\\nnaVAI+CMN2hoMq+bJ1X+9EWM63QADQ/BN97QDd4WETr6rb2EOkw4yOqSZ4QBmx7Y\\nk+2GDPT28TNkQ+LXx/wzLhdAj7O8uxepUi7/Dq6+OWmbccXF56mNZhsficlr4DzI\\ndmcgxZHnphV4IAzbjJTxk/Bl/RxN4apLQamOJVV8jxlRx8rGqN2dT5lyBm4GXD8h\\nKSuxiIgRzzBL2UBLAeyJxs0J9FQxqRxKC23ysmRaXk88ZCJ6vjdQrjI2aNWVdxHr\\nKMvuYyqhli/vuJP8kflKPoVHWne2zBLXD1ybWyqxK7CAxU/G9aYGtVfmL2WjdYU4\\nKHgXmdgpAgMBAAECggEAT1tGBcWU79//MBjEGbwm/VjX+ulDC3SesGauqoAvLT3U\\nhjqGfLZ0JX1hYGptNVyrjzDlSk3H+l2eC2NZL4OT+30mOA8Vy6Vrdar7WtI7EeOe\\nBFfuj/Lu5RhY9S89oslU41D6oLJOtb11T1qj2ZoifevaWvveMeLHzXrqMg4+ZVOl\\nOqQKWA7zbYeU0PFLCKaRb8fALYYqimovUWuxEwo4Mu0kHYXW9QjKejF7F3ZvZktg\\n9om/7Q6tfZM1lnYQQFiylsK+LpPmmOcy05EXOAkF7OQDxusrfVfEwTN4uW/idEiz\\n7Ej7Y+f2qPLG/p8XEQxasV9/oO2jerBciKfCI3llKQKBgQDuiF6pObo8nvp+F3Q+\\n3O9qMdEMmNsgh1G5JUM0XTefs22yLOABy7m5JtYhZqKXq136g3oldu1pVIKBWAqT\\nCqqRdh7zi1TfoIiQl9UEJ0BfRMETannaW6t9ZAMNOTmwkJf7b5dhkV9xg3oGj15w\\ndEbmNA+71z8g9UeFCj2DYYTOHwKBgQDeLlxbfaz+HxLCJjri5dC+s7sz/x09TuUW\\nSEEAP6MRxS/gNK3pqhqnup4dvS/gJHBF2Z215fyX7huvd5pSiVv9QEPooOhAvsMj\\nXQd87dYKqlxUWonDGamgboj6GBu5g09126DodpkI9YK1KIz8tdcA5pO+/HsywzbK\\n6iZGjAWAtwKBgDHpJO2B378pgL57h95TemEdFWHuZgNzbR0xd9NwIeBvuedLTn/x\\nseXVA38vq5vSxrJRanCGZvzgKwKHeobz92bCcY2CPPsh6xFeQ7s/v09v4np+kAqp\\nncN59s2AiNqyFqsM2X8X3QGyggj3XMpo+iDXCefDDTmHOTTURLTF5fT/AoGALAgM\\nDaWubv5BfXCOIwFgiSqdYwSgCtJj3DKdjysNaiwl/Mzz2mb3uV3mkAY6QrV1+qHR\\n+4aEtKdfnTqavtix/lKKIsQgwxQLICsDnOKVlK+GdMEspBml4EGHq+izNeSsDrNk\\nBC3czkW5jaVnq31uCqOnjCYFVF4TscsNotWFEakCgYEA7AroyGZDAVkFclatrfd0\\nAZErFxElGcZhYwD8nDNOlg91/NohjMprl16gwQCrmkl8x66PG7Z3XsMyz6gkJq0U\\n3aFNbl6hxo4WJoV1CobBojYesXT4pd9cfn+u1wu8muJVtHt6Pj3U9SBFP4DUmiZK\\nSScgfb9dPTUc65R2fwi8lv0=\\n-----END PRIVATE KEY-----\\n";

    FirebaseFirestore db;
    FirebaseDatabase rdb;

    public FirebaseRequest() {
        db = FirebaseFirestore.getInstance();
        rdb = FirebaseDatabase.getInstance();
    }

    public void findAllRequest(FirebaseRequestBody requestBody, FirebaseListener listener) {
        CollectionReference collection = db.collection(requestBody.getCollectionName());
        Query query = null;
        if (requestBody.getWhereFromField() != null && requestBody.getWhereValueField() != null) {
            if (!requestBody.getWhereFromField().equals("") && !requestBody.getWhereValueField().equals("")) {
                query = collection.whereEqualTo(requestBody.getWhereFromField(), requestBody.getWhereValueField());
            }
        } else {
            if (requestBody.getDocumentID() != null) {
                if (!requestBody.getDocumentID().equals("")) {
                    collection.document(requestBody.getDocumentID())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    listener.onSuccessAny(documentSnapshot);
                                } else {
                                    Log.e("findAllRequest_req", "empty");
                                    listener.onError();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (e != null) {
                                    Log.e("findAllRequest_req", e.getMessage());
                                }
                                listener.onError();
                            });
                }
            }
        }


        if (query != null) {
            query
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.e("findAllRequest_req", "empty");
                            listener.onError();
                        } else {
                            Log.e("onSuccessAny", "yehey");
                            listener.onSuccessAny(queryDocumentSnapshots);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (e != null) {
                            Log.e("findAllRequest_req", e.getMessage());
                        }
                        listener.onError();
                    });
        } else {
            collection.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.e("findAllRequest_req", "empty");
                            listener.onError();
                        } else {
                            listener.onSuccessAny(queryDocumentSnapshots);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (e != null) {
                            Log.e("findAllRequest_req", e.getMessage());
                        }
                        listener.onError();
                    });
        }
    }

    public void insertData(FirebaseRequestBody body, FirebaseListener listener) {
        CollectionReference collectionReference = db.collection(body.getCollectionName());
        this.findAllRequest(body, new FirebaseListener() {
            @Override
            public <T> void onSuccessAny(T any) {
                listener.onError();
            }

            @Override
            public void onError() {
                String docID = collectionReference.document().getId();
                collectionReference.document(docID)
                        .set(body.getParams())
                        .addOnSuccessListener(unused -> listener.onSuccessAny(docID))
                        .addOnFailureListener(e -> {
                            if (e != null) {
                                Log.e("insertData_err", e.getLocalizedMessage());
                            }
                            listener.onError();
                        });
            }
        });
    }

    public void upsertWithUserID(FirebaseRequestBody body, FirebaseListener listener) {
        db.collection(body.getCollectionName())
                .document(body.getDocumentID())
                .set(body.getParams(), SetOptions.merge())
                .addOnSuccessListener(unused -> listener.onSuccessAny(null))
                .addOnFailureListener(e -> {
                    if (e != null) {
                        Log.e("upsertWithUserID_err", e.getMessage());
                    }
                    listener.onError();
                });
    }

    public DatabaseReference getReference(String name) {
        return rdb.getReference(name);
    }


}
