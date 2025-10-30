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

public class AdminActivity extends AppCompatActivity {

    private Button btnActivarSensores, btnDesactivarSensores;
    private Button btnAbrirBarrera, btnCerrarBarrera;
    private Button btnResetRegistros, btnExportarPDF, btnExportarExcel, btnVolver;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();

        // Referencias a los botones
        btnActivarSensores = findViewById(R.id.btnActivarSensores);
        btnDesactivarSensores = findViewById(R.id.btnDesactivarSensores);
        btnAbrirBarrera = findViewById(R.id.btnAbrirBarrera);
        btnCerrarBarrera = findViewById(R.id.btnCerrarBarrera);
        btnResetRegistros = findViewById(R.id.btnResetRegistros);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);
        btnExportarExcel = findViewById(R.id.btnExportarExcel);
        btnVolver = findViewById(R.id.btnVolverAdmin);

        // 🔹 Acciones de control manual
        btnActivarSensores.setOnClickListener(v -> enviarComando("SENSORES_ON"));
        btnDesactivarSensores.setOnClickListener(v -> enviarComando("SENSORES_OFF"));
        btnAbrirBarrera.setOnClickListener(v -> enviarComando("BARRERA_ABRIR"));
        btnCerrarBarrera.setOnClickListener(v -> enviarComando("BARRERA_CERRAR"));

        // 🔹 Reset de registros del día
        btnResetRegistros.setOnClickListener(v -> resetearRegistrosDelDia());

        // 🔹 Exportar historial
        btnExportarPDF.setOnClickListener(v -> exportarHistorial("pdf"));
        btnExportarExcel.setOnClickListener(v -> exportarHistorial("excel"));

        // 🔹 Volver al menú
        btnVolver.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, MainActivity.class));
            finish();
        });
    }

    /**
     * Envía un comando de control a Firestore (leído luego por Arduino o app)
     */
    private void enviarComando(String tipo) {
        Map<String, Object> comando = new HashMap<>();
        comando.put("tipo", tipo);
        comando.put("fechaHora", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        db.collection("controles")
                .add(comando)
                .addOnSuccessListener(doc -> Toast.makeText(this, "Comando enviado: " + tipo, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al enviar comando", Toast.LENGTH_SHORT).show());
    }

    /**
     * Elimina los registros del día actual
     */
    private void resetearRegistrosDelDia() {
        db.collection("registros")
                .get()
                .addOnSuccessListener(query -> {
                    int eliminados = 0;
                    for (var doc : query.getDocuments()) {
                        doc.getReference().delete();
                        eliminados++;
                    }
                    Toast.makeText(this, "Registros eliminados: " + eliminados, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar registros", Toast.LENGTH_SHORT).show());
    }

    /**
     * Exporta el historial a PDF o Excel (placeholder)
     */
    private void exportarHistorial(String formato) {
        Toast.makeText(this, "Exportando historial a " + formato.toUpperCase() + "...", Toast.LENGTH_SHORT).show();
        // 🔜 Aquí se implementará la generación del archivo (PDF o XLSX)
    }
}
