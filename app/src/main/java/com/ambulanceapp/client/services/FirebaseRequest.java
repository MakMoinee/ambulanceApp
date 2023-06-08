package com.ambulanceapp.client.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public void insertData(FirebaseRequestBody body, FirebaseListener listener) {
        CollectionReference collectionReference = db.collection(body.getCollectionName());
        body.setWhereFromField("email");
        body.setWhereValueField(body.getEmail());
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

}
