package com.ambulanceapp.client.parser;

import com.ambulanceapp.client.models.Users;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommonParser {

    public static List<Users> parseUsers(QuerySnapshot queryDocumentSnapshots) {
        List<Users> usersList = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            if (documentSnapshot.exists()) {
                Users users = documentSnapshot.toObject(Users.class);
                if (users != null) {
                    users.setDocumentID(documentSnapshot.getId());
                    usersList.add(users);
                }
            }
        }
        return usersList;
    }
}
