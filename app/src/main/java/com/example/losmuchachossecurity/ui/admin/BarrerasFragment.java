package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BarrerasFragment extends Fragment {

    private FirebaseFirestore db;
    private DocumentReference comandosRef;

    private TextView tvEstadoEntrada, tvEstadoSalida;
    private Button btnAbrirEntrada, btnCerrarEntrada;
    private Button btnAbrirSalida, btnCerrarSalida;
    private Switch switchBloqueo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_barreras, container, false);

        db = FirebaseFirestore.getInstance();
        comandosRef = db.collection("comandos").document("arduinoControl");

        // Referencias UI
        tvEstadoEntrada = view.findViewById(R.id.tvEstadoEntrada);
        tvEstadoSalida  = view.findViewById(R.id.tvEstadoSalida);
        btnAbrirEntrada = view.findViewById(R.id.btnAbrirEntrada);
        btnCerrarEntrada = view.findViewById(R.id.btnCerrarEntrada);
        btnAbrirSalida = view.findViewById(R.id.btnAbrirSalida);
        btnCerrarSalida = view.findViewById(R.id.btnCerrarSalida);
        switchBloqueo = view.findViewById(R.id.switchBloqueo);

        configurarBotones();
        cargarEstadoInicial();

        return view;
    }

    private void configurarBotones() {
        btnAbrirEntrada.setOnClickListener(v ->
                enviarComando("abrirEntrada", true, "Abriendo barrera de entrada"));

        btnCerrarEntrada.setOnClickListener(v ->
                enviarComando("cerrarEntrada", true, "Cerrando barrera de entrada"));

        btnAbrirSalida.setOnClickListener(v ->
                enviarComando("abrirSalida", true, "Abriendo barrera de salida"));

        btnCerrarSalida.setOnClickListener(v ->
                enviarComando("cerrarSalida", true, "Cerrando barrera de salida"));

        switchBloqueo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            enviarComando("modoBloqueado", isChecked,
                    isChecked ? "Modo bloqueo activado" : "Modo bloqueo desactivado");
        });
    }

    private void enviarComando(String campo, Object valor, String mensajeOk) {
        if (comandosRef == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put(campo, valor);
        data.put("timestamp", FieldValue.serverTimestamp());

        comandosRef.set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), mensajeOk, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al enviar comando", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarEstadoInicial() {
        comandosRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Boolean bloqueo = documentSnapshot.getBoolean("modoBloqueado");
                            if (bloqueo != null) {
                                switchBloqueo.setChecked(bloqueo);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No es grave, se puede ignorar
                    }
                });

        // Los textos de estado por ahora son estáticos
        tvEstadoEntrada.setText("Estado: Controlado por Arduino");
        tvEstadoSalida.setText("Estado: Controlado por Arduino");
    }
}
