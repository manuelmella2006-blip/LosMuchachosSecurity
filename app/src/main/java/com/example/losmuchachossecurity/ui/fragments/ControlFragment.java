package com.example.losmuchachossecurity.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ControlFragment extends Fragment {

    private Button btnAbrirBarrera, btnCerrarBarrera, btnActivarSensores, btnDesactivarSensores;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        db = FirebaseFirestore.getInstance();

        // Vincular botones
        btnAbrirBarrera = view.findViewById(R.id.btnAbrirBarrera);
        btnCerrarBarrera = view.findViewById(R.id.btnCerrarBarrera);
        btnActivarSensores = view.findViewById(R.id.btnActivarSensores);
        btnDesactivarSensores = view.findViewById(R.id.btnDesactivarSensores);

        // Configurar listeners
        btnAbrirBarrera.setOnClickListener(v -> enviarComando("BARRERA_ABRIR"));
        btnCerrarBarrera.setOnClickListener(v -> enviarComando("BARRERA_CERRAR"));
        btnActivarSensores.setOnClickListener(v -> enviarComando("SENSORES_ON"));
        btnDesactivarSensores.setOnClickListener(v -> enviarComando("SENSORES_OFF"));

        return view;
    }

    private void enviarComando(String tipo) {
        Map<String, Object> comando = new HashMap<>();
        comando.put("tipo", tipo);
        comando.put("fechaHora", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        db.collection("controles")
                .add(comando)
                .addOnSuccessListener(doc ->
                        Toast.makeText(getContext(), "Comando enviado: " + tipo, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al enviar comando", Toast.LENGTH_SHORT).show());
    }
}