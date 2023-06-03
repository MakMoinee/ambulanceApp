package com.ambulanceapp.client.services;

import android.util.Log;

import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirebaseRequest {

    public static final String USERS_COLLECTION = "users";
    public static final String EMAIL_STRING = "email";

    FirebaseFirestore db;

    public FirebaseRequest() {
        db = FirebaseFirestore.getInstance();
    }

    public void findAllRequest(FirebaseRequestBody requestBody, FirebaseListener listener) {
        CollectionReference collection = db.collection(requestBody.getCollectionName());
        Query query = null;
        if (!requestBody.getWhereFromField().equals("") && !requestBody.getWhereValueField().equals("")) {
            query = collection.whereEqualTo(requestBody.getWhereFromField(), requestBody.getWhereValueField());
        }

        if (query != null) {
            query
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
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

}
