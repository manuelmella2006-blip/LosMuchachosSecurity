package com.example.losmuchachossecurity.data;

import androidx.annotation.NonNull;
import com.example.losmuchachossecurity.model.ControlEvento;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * 🔥 ControlEventoRepository (versión Firestore)
 * Reemplaza completamente el uso de Realtime Database.
 */
public class ControlEventoRepository {

    private static final String CONTROL_COLLECTION = "control_eventos";
    private static final String BARRERAS_COLLECTION = "barreras";

    private final FirebaseFirestore db;
    private final CollectionReference controlEventosRef;

    public ControlEventoRepository() {
        db = FirebaseConfig.getFirestore(); // Usa tu configuración centralizada
        controlEventosRef = db.collection(CONTROL_COLLECTION);
    }

    // Interfaces de callback
    public interface ControlCallback {
        void onEventoRegistrado();
        void onError(String mensaje);
    }

    public interface EventosCallback {
        void onEventosObtenidos(List<ControlEvento> eventos);
        void onError(String mensaje);
    }

    /**
     * Registra un evento de control (apertura/cierre de barrera)
     */
    public void registrarEvento(String tipo, String accion, String userId, ControlCallback callback) {
        String eventoId = controlEventosRef.document().getId();

        ControlEvento evento = new ControlEvento(
                eventoId,
                tipo,
                accion,
                userId,
                System.currentTimeMillis()
        );

        controlEventosRef.document(eventoId)
                .set(evento)
                .addOnSuccessListener(aVoid -> callback.onEventoRegistrado())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Obtiene todos los eventos de control desde Firestore
     */
    public void obtenerEventos(EventosCallback callback) {
        controlEventosRef
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<ControlEvento> eventos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        ControlEvento evento = doc.toObject(ControlEvento.class);
                        eventos.add(evento);
                    }
                    callback.onEventosObtenidos(eventos);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Actualiza el estado de una barrera en Firestore
     */
    public void actualizarEstadoBarrera(String tipo, String estado, ControlCallback callback) {
        db.collection(BARRERAS_COLLECTION)
                .document(tipo)
                .update("estado", estado)
                .addOnSuccessListener(aVoid -> callback.onEventoRegistrado())
                .addOnFailureListener(e -> {
                    // Si no existe, se crea automáticamente
                    db.collection(BARRERAS_COLLECTION)
                            .document(tipo)
                            .set(new BarreraEstado(estado))
                            .addOnSuccessListener(aVoid2 -> callback.onEventoRegistrado())
                            .addOnFailureListener(e2 -> callback.onError(e2.getMessage()));
                });
    }

    /**
     * Clase auxiliar para representar estado de barrera si no existe documento previo
     */
    private static class BarreraEstado {
        public String estado;

        public BarreraEstado() {} // Constructor vacío requerido
        public BarreraEstado(String estado) {
            this.estado = estado;
        }
    }
}
