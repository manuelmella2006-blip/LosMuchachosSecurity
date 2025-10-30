package com.example.losmuchachossecurity.data;

import com.example.losmuchachossecurity.model.Registro;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegistroRepository {
    private final FirebaseFirestore db = FirebaseConfig.getFirestore();

    public void registrarEntrada(Registro registro) {
        db.collection("registros")
                .document(registro.getId())
                .set(registro);
    }

    public void obtenerRegistrosPorUsuario(String usuarioId, OnRegistrosCargadosListener listener) {
        db.collection("registros")
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Registro> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        lista.add(doc.toObject(Registro.class));
                    }
                    listener.onCargados(lista);
                });
    }

    public interface OnRegistrosCargadosListener {
        void onCargados(List<Registro> registros);
    }
}