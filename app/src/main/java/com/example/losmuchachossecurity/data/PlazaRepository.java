package com.example.losmuchachossecurity.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.losmuchachossecurity.model.Plaza;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlazaRepository {

    private static final String PLAZAS_COLLECTION = "plazas";
    private static final String RESERVAS_COLLECTION = "reservas";
    private final FirebaseFirestore db;
    private final CollectionReference plazasRef;
    private final CollectionReference reservasRef;

    public PlazaRepository() {
        db = FirebaseConfig.getFirestore();
        plazasRef = db.collection(PLAZAS_COLLECTION);
        reservasRef = db.collection(RESERVAS_COLLECTION);
    }

    // ========================================
    // INTERFACES DE CALLBACKS
    // ========================================

    public interface PlazaCallback {
        void onPlazasObtenidas(List<Plaza> plazas);
        void onError(String mensaje);
    }

    public interface ReservaCallback {
        void onReservaExitosa();
        void onError(String mensaje);
    }

    public interface PlazaUnicaCallback {
        void onPlazaObtenida(Plaza plaza);
        void onError(String mensaje);
    }

    // ========================================
    // MÉTODOS PARA OBTENER PLAZAS
    // ========================================

    public void obtenerPlazas(PlazaCallback callback) {
        plazasRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Plaza> plazas = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Plaza plaza = doc.toObject(Plaza.class);
                        plaza.setId(doc.getId());
                        plazas.add(plaza);
                    }
                    callback.onPlazasObtenidas(plazas);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void obtenerPlazaPorId(String plazaId, PlazaUnicaCallback callback) {
        plazasRef.document(plazaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Plaza plaza = documentSnapshot.toObject(Plaza.class);
                        if (plaza != null) plaza.setId(documentSnapshot.getId());
                        callback.onPlazaObtenida(plaza);
                    } else {
                        callback.onError("Plaza no encontrada");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void obtenerPlazasDisponibles(PlazaCallback callback) {
        plazasRef.whereEqualTo("ocupado", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Plaza> plazasDisponibles = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Plaza plaza = doc.toObject(Plaza.class);
                        plaza.setId(doc.getId());
                        plazasDisponibles.add(plaza);
                    }
                    callback.onPlazasObtenidas(plazasDisponibles);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========================================
    // MÉTODOS PARA RESERVAR PLAZAS
    // ========================================

    public void reservarPlaza(String plazaId, String userId, String fecha,
                              String horaInicio, String horaFin, ReservaCallback callback) {

        plazasRef.document(plazaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        callback.onError("Plaza no encontrada");
                        return;
                    }

                    Boolean ocupado = documentSnapshot.getBoolean("ocupado");
                    if (ocupado != null && ocupado) {
                        callback.onError("La plaza ya está ocupada");
                        return;
                    }

                    Map<String, Object> reservaData = new HashMap<>();
                    reservaData.put("plazaId", plazaId);
                    reservaData.put("userId", userId);
                    reservaData.put("fecha", fecha);
                    reservaData.put("horaInicio", horaInicio);
                    reservaData.put("horaFin", horaFin);
                    reservaData.put("estado", "activa");
                    reservaData.put("timestampCreacion", FieldValue.serverTimestamp());

                    reservasRef.add(reservaData)
                            .addOnSuccessListener(docRef -> {
                                Map<String, Object> plazaUpdate = new HashMap<>();
                                plazaUpdate.put("ocupado", true);
                                plazaUpdate.put("estado", "OCUPADO");
                                plazaUpdate.put("ultima_actualizacion", FieldValue.serverTimestamp());

                                plazasRef.document(plazaId)
                                        .update(plazaUpdate)
                                        .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                                        .addOnFailureListener(e -> callback.onError("Reserva creada pero error al actualizar plaza: " + e.getMessage()));
                            })
                            .addOnFailureListener(e -> callback.onError("Error al crear reserva: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void liberarPlaza(String plazaId, ReservaCallback callback) {
        reservasRef.whereEqualTo("plazaId", plazaId)
                .whereEqualTo("estado", "activa")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        doc.getReference().update("estado", "completada");
                    }

                    Map<String, Object> plazaUpdate = new HashMap<>();
                    plazaUpdate.put("ocupado", false);
                    plazaUpdate.put("estado", "LIBRE");
                    plazaUpdate.put("ultima_actualizacion", FieldValue.serverTimestamp());

                    plazasRef.document(plazaId)
                            .update(plazaUpdate)
                            .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========================================
    // MÉTODOS PARA ADMIN
    // ========================================

    public void crearPlaza(Plaza plaza, ReservaCallback callback) {
        String plazaId = plazasRef.document().getId();
        plaza.setId(plazaId);

        plazasRef.document(plazaId)
                .set(plaza)
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void actualizarEstadoPlaza(String plazaId, boolean ocupado, ReservaCallback callback) {
        Map<String, Object> update = new HashMap<>();
        update.put("ocupado", ocupado);
        update.put("estado", ocupado ? "OCUPADO" : "LIBRE");
        update.put("ultima_actualizacion", FieldValue.serverTimestamp());

        plazasRef.document(plazaId)
                .update(update)
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void eliminarPlaza(String plazaId, ReservaCallback callback) {
        plazasRef.document(plazaId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void verificarReservaUsuario(String userId, PlazaUnicaCallback callback) {
        reservasRef.whereEqualTo("userId", userId)
                .whereEqualTo("estado", "activa")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String plazaId = querySnapshot.getDocuments()
                                .get(0)
                                .getString("plazaId");
                        if (plazaId != null) {
                            obtenerPlazaPorId(plazaId, callback);
                        }
                    } else {
                        callback.onError("No tiene reservas activas");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========================================
    // NUEVO: CAMBIO MANUAL DE ESTADO (ADMIN)
    // ========================================

    public void cambiarEstadoPlazaManual(String plazaId, boolean ocupado, ReservaCallback callback) {
        Map<String, Object> update = new HashMap<>();
        update.put("ocupado", ocupado);
        update.put("estado", ocupado ? "OCUPADO" : "LIBRE");
        update.put("ultima_actualizacion", FieldValue.serverTimestamp());

        plazasRef.document(plazaId)
                .update(update)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PlazaRepository",
                            "Estado cambiado manualmente: " + plazaId +
                                    " -> " + (ocupado ? "OCUPADO" : "LIBRE"));
                    callback.onReservaExitosa();
                })
                .addOnFailureListener(e -> {
                    Log.e("PlazaRepository",
                            "Error al cambiar estado manual: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }
}
