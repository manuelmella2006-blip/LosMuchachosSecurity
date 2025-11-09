package com.example.losmuchachossecurity.data;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * 🔥 Configuración centralizada de Firebase
 */
public class FirebaseConfig {

    private static FirebaseFirestore firestoreInstance;

    /**
     * Obtiene la instancia única de Firestore (Singleton)
     */
    public static FirebaseFirestore getFirestore() {
        if (firestoreInstance == null) {
            firestoreInstance = FirebaseFirestore.getInstance();

            // Configuración opcional de Firestore
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true) // Cache offline
                    .build();

            firestoreInstance.setFirestoreSettings(settings);
        }
        return firestoreInstance;
    }
}