package com.ambulanceapp.client.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ambulanceapp.client.interfaces.FirebaseListener;
import com.ambulanceapp.client.models.FirebaseRequestBody;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class FirebaseRequest {

    public static final String USERS_COLLECTION = "users";
    public static final String ENFORCERS_COLLECTION = "enforcers";
    public static final String AMBULANCE_REFERENCE = "ambulance";
    public static final String EMAIL_STRING = "email";

    FirebaseFirestore db;
    FirebaseDatabase rdb;

    public FirebaseRequest() {
        db = FirebaseFirestore.getInstance();
        rdb = FirebaseDatabase.getInstance();
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
        }else{
            collection.get()
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

    public DatabaseReference getReference(String name){
        return rdb.getReference(name);
    }

}
