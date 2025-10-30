package com.example.losmuchachossecurity.data;

import com.example.losmuchachossecurity.model.Plaza;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlazaRepository {
    private final FirebaseFirestore db = FirebaseConfig.getFirestore();

    public void actualizarPlaza(Plaza plaza) {
        db.collection("plazas")
                .document(plaza.getId())
                .set(plaza);
    }

    public void obtenerPlazas(OnPlazasCargadasListener listener) {
        db.collection("plazas")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Plaza> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Plaza p = doc.toObject(Plaza.class);
                        lista.add(p);
                    }
                    listener.onCargadas(lista);
                });
    }

    public interface OnPlazasCargadasListener {
        void onCargadas(List<Plaza> plazas);
    }
}