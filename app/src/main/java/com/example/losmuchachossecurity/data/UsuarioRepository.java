package com.example.losmuchachossecurity.data;

import android.util.Log;

import com.example.losmuchachossecurity.model.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioRepository {
    private static final String TAG = "UsuarioRepository";
    private static final String COLLECTION_NAME = "usuarios";
    public final FirebaseFirestore db = FirebaseConfig.getFirestore();

    /**
     * 📝 Crea un nuevo usuario en Firestore
     */
    public void crearUsuario(Usuario usuario, OnOperacionListener listener) {
        db.collection(COLLECTION_NAME)
                .document(usuario.getUserId())
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onExito();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    /**
     * 🔍 Obtiene un usuario por su ID
     */
    public void obtenerUsuario(String id, OnUsuarioObtenidoListener listener) {
        db.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Usuario u = document.toObject(Usuario.class);
                        if (u != null) {
                            u.setUserId(document.getId());
                        }
                        listener.onObtenido(u);
                    } else {
                        listener.onObtenido(null);
                    }
                })
                .addOnFailureListener(e -> listener.onObtenido(null));
    }

    /**
     * 📋 Obtiene todos los usuarios (manejo robusto de deserialización)
     */
    public void obtenerTodosLosUsuarios(OnUsuariosObtenidosListener listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Usuario> usuarios = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Usuario usuario = document.toObject(Usuario.class);
                            usuario.setUserId(document.getId());
                            usuarios.add(usuario);
                        } catch (Exception e) {
                            // Deserialización manual
                            Usuario usuario = new Usuario();
                            usuario.setUserId(document.getId());
                            usuario.setNombre(document.getString("nombre"));
                            usuario.setEmail(document.getString("email"));
                            usuario.setRol(document.getString("rol"));
                            usuario.setTelefono(document.getString("telefono"));
                            usuario.setActivo(document.getBoolean("activo") != null
                                    ? document.getBoolean("activo") : true);

                            Object fechaObj = document.get("fechaRegistro");
                            usuario.setFechaRegistro(fechaObj);

                            usuarios.add(usuario);
                        }
                    }

                    listener.onObtenidos(usuarios);
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    /**
     * ✏️ Actualiza un usuario existente en Firestore
     */
    public void actualizarUsuario(Usuario usuario, UsuarioCallback callback) {
        if (usuario.getUserId() == null || usuario.getUserId().isEmpty()) {
            callback.onError("ID de usuario no válido");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", usuario.getNombre());
        updates.put("telefono", usuario.getTelefono());
        updates.put("rol", usuario.getRol());
        updates.put("activo", usuario.isActivo());
        updates.put("fechaActualizacion", FieldValue.serverTimestamp());

        db.collection(COLLECTION_NAME)
                .document(usuario.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Usuario actualizado correctamente: " + usuario.getUserId());
                    callback.onSuccess("Usuario actualizado correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar usuario", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * ✅ Actualiza solo el estado (activo/inactivo) del usuario
     */
    public void actualizarEstadoUsuario(String userId, boolean activo, UsuarioCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("ID de usuario no válido");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("activo", activo);
        updates.put("fechaActualizacion", FieldValue.serverTimestamp());

        db.collection(COLLECTION_NAME)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    String estado = activo ? "activado" : "desactivado";
                    callback.onSuccess("Usuario " + estado + " correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar estado", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 🔄 Actualiza el rol de un usuario
     */
    public void actualizarRolUsuario(String userId, String nuevoRol, UsuarioCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("ID de usuario no válido");
            return;
        }

        if (!nuevoRol.equals("usuario") && !nuevoRol.equals("admin")) {
            callback.onError("Rol no válido. Debe ser 'usuario' o 'admin'");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("rol", nuevoRol);
        updates.put("fechaActualizacion", FieldValue.serverTimestamp());

        db.collection(COLLECTION_NAME)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Rol actualizado correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar rol", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 🗑️ Elimina un usuario
     */
    public void eliminarUsuario(String userId, OnOperacionListener listener) {
        db.collection(COLLECTION_NAME)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onExito();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e.getMessage());
                });
    }

    // ==================== INTERFACES ====================

    public interface OnUsuarioObtenidoListener {
        void onObtenido(Usuario usuario);
    }

    public interface OnUsuariosObtenidosListener {
        void onObtenidos(List<Usuario> usuarios);
        void onError(String mensaje);
    }

    public interface OnOperacionListener {
        void onExito();
        void onError(String mensaje);
    }

    public interface UsuarioCallback {
        void onSuccess(String mensaje);
        void onError(String error);
    }
}
