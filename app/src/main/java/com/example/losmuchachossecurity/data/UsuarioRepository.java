package com.example.losmuchachossecurity.data;

import com.example.losmuchachossecurity.model.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuarioRepository {
    private final FirebaseFirestore db = FirebaseConfig.getFirestore();

    public void crearUsuario(Usuario usuario) {
        db.collection("usuarios")
                .document(usuario.getUserId()) // ✅ Aquí se corrige
                .set(usuario);
    }

    public void obtenerUsuario(String id, OnUsuarioObtenidoListener listener) {
        db.collection("usuarios")
                .document(id)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Usuario u = document.toObject(Usuario.class);
                        listener.onObtenido(u);
                    } else {
                        listener.onObtenido(null);
                    }
                });
    }

    public interface OnUsuarioObtenidoListener {
        void onObtenido(Usuario usuario);
    }
}
