package com.example.losmuchachossecurity.data;

import com.example.losmuchachossecurity.model.ControlEvento;
import com.google.firebase.firestore.FirebaseFirestore;

public class ControlEventoRepository {
    private final FirebaseFirestore db = FirebaseConfig.getFirestore();

    public void enviarComando(ControlEvento evento) {
        db.collection("controles")
                .add(evento);
    }
}