package com.example.losmuchachossecurity.data;

import com.example.losmuchachossecurity.model.Registro;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * 🔥 RegistroRepository (versión Firestore)
 * Reemplaza Realtime Database con consultas Firestore más potentes.
 */
public class RegistroRepository {

    private static final String REGISTROS_COLLECTION = "registros";
    private final FirebaseFirestore db;
    private final CollectionReference registrosRef;

    public RegistroRepository() {
        db = FirebaseConfig.getFirestore(); // Usa tu clase de configuración centralizada
        registrosRef = db.collection(REGISTROS_COLLECTION);
    }

    // ========================================
    // INTERFACES DE CALLBACKS
    // ========================================

    public interface RegistroCallback {
        void onRegistroGuardado();
        void onError(String mensaje);
    }

    public interface RegistrosCallback {
        void onRegistrosObtenidos(List<Registro> registros);
        void onError(String mensaje);
    }

    // ========================================
    // MÉTODOS CRUD
    // ========================================

    /**
     * Guarda un nuevo registro.
     */
    public void guardarRegistro(Registro registro, RegistroCallback callback) {
        String registroId = registrosRef.document().getId();
        registro.setId(registroId);

        registrosRef.document(registroId)
                .set(registro)
                .addOnSuccessListener(aVoid -> callback.onRegistroGuardado())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Obtiene todos los registros, ordenados por timestamp ascendente.
     */
    public void obtenerRegistros(RegistrosCallback callback) {
        registrosRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registro> registros = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Registro registro = doc.toObject(Registro.class);
                        registro.setId(doc.getId());
                        registros.add(registro);
                    }
                    callback.onRegistrosObtenidos(registros);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Obtiene registros asociados a un usuario específico.
     */
    public void obtenerRegistrosPorUsuario(String userId, RegistrosCallback callback) {
        registrosRef.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registro> registros = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Registro registro = doc.toObject(Registro.class);
                        registro.setId(doc.getId());
                        registros.add(registro);
                    }
                    callback.onRegistrosObtenidos(registros);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Obtiene registros dentro de un rango de fechas (timestamp en milisegundos).
     */
    public void obtenerRegistrosPorFecha(long fechaInicio, long fechaFin, RegistrosCallback callback) {
        registrosRef.whereGreaterThanOrEqualTo("timestamp", fechaInicio)
                .whereLessThanOrEqualTo("timestamp", fechaFin)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Registro> registros = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Registro registro = doc.toObject(Registro.class);
                        registro.setId(doc.getId());
                        registros.add(registro);
                    }
                    callback.onRegistrosObtenidos(registros);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
