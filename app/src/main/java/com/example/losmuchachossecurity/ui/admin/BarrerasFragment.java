package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BarrerasFragment extends Fragment {

    private static final String TAG = "BarrerasFragment";

    private FirebaseFirestore db;
    private DocumentReference comandosRef;

    private TextView tvEstadoEntrada, tvEstadoSalida;
    private Button btnAbrirEntrada, btnAbrirSalida;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barreras, container, false);

        db = FirebaseFirestore.getInstance();
        // comandos / arduinoControl
        comandosRef = db.collection("comandos").document("arduinoControl");

        // Referencias UI
        tvEstadoEntrada = view.findViewById(R.id.tvEstadoEntrada);
        tvEstadoSalida  = view.findViewById(R.id.tvEstadoSalida);
        btnAbrirEntrada = view.findViewById(R.id.btnAbrirEntrada);
        btnAbrirSalida  = view.findViewById(R.id.btnAbrirSalida);

        configurarBotones();
        actualizarEstadoUI();

        return view;
    }

    private void configurarBotones() {

        // ===== ABRIR ENTRADA =====
        btnAbrirEntrada.setOnClickListener(v -> {
            enviarComando("abrirEntrada", true, "Abriendo barrera de entrada");

            // A los 2 segundos, poner en false para que no quede pegado el comando
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                enviarComando("abrirEntrada", false, "");
            }, 2000);
        });

        // ===== ABRIR SALIDA =====
        btnAbrirSalida.setOnClickListener(v -> {
            enviarComando("abrirSalida", true, "Abriendo barrera de salida");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                enviarComando("abrirSalida", false, "");
            }, 2000);
        });
    }

    /**
     * Envía un campo booleano al documento comandos/arduinoControl
     */
    private void enviarComando(String campo, boolean valor, String mensajeOk) {
        if (comandosRef == null || getContext() == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put(campo, valor);
        data.put("timestamp", FieldValue.serverTimestamp());
        data.put("ultimaActualizacion", true);

        comandosRef.update(data)
                .addOnSuccessListener(unused -> {
                    if (!mensajeOk.isEmpty()) {
                        Toast.makeText(getContext(), mensajeOk, Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "Comando enviado: " + campo + " = " + valor);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error al enviar comando: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error enviando comando: " + e.getMessage());
                });
    }

    private void actualizarEstadoUI() {
        String estadoBase = "Estado: ";
        String estadoControlado = "Controlado por Arduino";

        tvEstadoEntrada.setText(estadoBase + estadoControlado);
        tvEstadoSalida.setText(estadoBase + estadoControlado);
    }
}
