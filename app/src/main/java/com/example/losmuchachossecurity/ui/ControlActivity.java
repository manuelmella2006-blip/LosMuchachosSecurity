package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ControlActivity extends AppCompatActivity {

    private Button btnAbrirBarrera, btnCerrarBarrera, btnActivarSensores, btnDesactivarSensores, btnVolver;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control);

        db = FirebaseFirestore.getInstance();

        // Vincular botones
        btnAbrirBarrera = findViewById(R.id.btnAbrirBarrera);
        btnCerrarBarrera = findViewById(R.id.btnCerrarBarrera);
        btnActivarSensores = findViewById(R.id.btnActivarSensores);
        btnDesactivarSensores = findViewById(R.id.btnDesactivarSensores);
        btnVolver = findViewById(R.id.btnVolverControl);

        // Configurar listeners
        btnAbrirBarrera.setOnClickListener(v -> enviarComando("BARRERA_ABRIR"));
        btnCerrarBarrera.setOnClickListener(v -> enviarComando("BARRERA_CERRAR"));
        btnActivarSensores.setOnClickListener(v -> enviarComando("SENSORES_ON"));
        btnDesactivarSensores.setOnClickListener(v -> enviarComando("SENSORES_OFF"));

        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(ControlActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Envía un comando de control a Firestore
     */
    private void enviarComando(String tipo) {
        Map<String, Object> comando = new HashMap<>();
        comando.put("tipo", tipo);
        comando.put("fechaHora", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        db.collection("controles")
                .add(comando)
                .addOnSuccessListener(doc ->
                        Toast.makeText(this, "Comando enviado: " + tipo, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al enviar comando", Toast.LENGTH_SHORT).show());
    }
}