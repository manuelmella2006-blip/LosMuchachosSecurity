package com.example.losmuchachossecurity.data;

import androidx.annotation.NonNull;

import com.example.losmuchachossecurity.model.Plaza;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * 🔥 PlazaRepository (versión Firestore)
 * Convierte toda la lógica de Realtime Database a Cloud Firestore.
 */
public class PlazaRepository {

    private static final String PLAZAS_COLLECTION = "plazas";
    private final FirebaseFirestore db;
    private final CollectionReference plazasRef;

    public PlazaRepository() {
        db = FirebaseConfig.getFirestore(); // usa tu clase centralizada FirebaseConfig
        plazasRef = db.collection(PLAZAS_COLLECTION);
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

    /**
     * Obtiene todas las plazas de estacionamiento.
     */
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

    /**
     * Obtiene una plaza específica por ID.
     */
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

    /**
     * Obtiene solo las plazas disponibles.
     */
    public void obtenerPlazasDisponibles(PlazaCallback callback) {
        plazasRef.whereEqualTo("disponible", true)
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

    /**
     * Reserva una plaza de estacionamiento.
     */
    public void reservarPlaza(String plazaId, String userId, ReservaCallback callback) {
        DocumentReference plazaRef = plazasRef.document(plazaId);

        plazaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Plaza plaza = documentSnapshot.toObject(Plaza.class);
                if (plaza != null && plaza.isDisponible()) {
                    plazaRef.update(
                                    "disponible", false,
                                    "usuarioId", userId,
                                    "fechaReserva", System.currentTimeMillis()
                            ).addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                } else {
                    callback.onError("La plaza no está disponible");
                }
            } else {
                callback.onError("Plaza no encontrada");
            }
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Libera una plaza de estacionamiento.
     */
    public void liberarPlaza(String plazaId, ReservaCallback callback) {
        plazasRef.document(plazaId)
                .update(
                        "disponible", true,
                        "usuarioId", null,
                        "fechaReserva", null
                )
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========================================
    // MÉTODOS PARA ADMIN
    // ========================================

    /**
     * Crea una nueva plaza (para uso del administrador).
     */
    public void crearPlaza(Plaza plaza, ReservaCallback callback) {
        String plazaId = plazasRef.document().getId();
        plaza.setId(plazaId);

        plazasRef.document(plazaId)
                .set(plaza)
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Actualiza el estado de una plaza.
     */
    public void actualizarEstadoPlaza(String plazaId, boolean disponible, ReservaCallback callback) {
        plazasRef.document(plazaId)
                .update("disponible", disponible)
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Elimina una plaza (para uso del administrador).
     */
    public void eliminarPlaza(String plazaId, ReservaCallback callback) {
        plazasRef.document(plazaId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onReservaExitosa())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    /**
     * Obtiene el número de plazas disponibles.
     */
    public void obtenerNumeroPlazasDisponibles(PlazaCallback callback) {
        plazasRef.whereEqualTo("disponible", true)
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

    /**
     * Verifica si el usuario tiene una reserva activa.
     */
    public void verificarReservaUsuario(String userId, PlazaUnicaCallback callback) {
        plazasRef.whereEqualTo("usuarioId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Plaza plaza = querySnapshot.getDocuments().get(0).toObject(Plaza.class);
                        if (plaza != null) {
                            plaza.setId(querySnapshot.getDocuments().get(0).getId());
                            callback.onPlazaObtenida(plaza);
                        }
                    } else {
                        callback.onError("No tiene reservas activas");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
