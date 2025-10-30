package com.example.losmuchachossecurity.data;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseConfig {

    private static FirebaseFirestore db;

    public static FirebaseFirestore getFirestore() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }
}