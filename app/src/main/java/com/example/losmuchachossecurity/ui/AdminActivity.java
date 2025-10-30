package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
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
        if (!formato.equalsIgnoreCase("pdf")) {
            Toast.makeText(this, "Solo disponible en PDF por ahora", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("registros")
                .orderBy("horaEntrada")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No hay registros para exportar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        // 🗂️ Ruta donde se guardará el archivo
                        String nombreArchivo = "Historial_Estacionamiento_" +
                                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
                        File archivo = new File(getExternalFilesDir(null), nombreArchivo);

                        // 🖨️ Crear documento PDF
                        com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
                        com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new FileOutputStream(archivo));
                        documento.open();

                        // 🏷️ Encabezado
                        documento.add(new com.itextpdf.text.Paragraph("Centro Integral Alerce - Estacionamiento"));
                        documento.add(new com.itextpdf.text.Paragraph("Historial de Entradas y Salidas"));
                        documento.add(new com.itextpdf.text.Paragraph(
                                "Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date())
                        ));
                        documento.add(new com.itextpdf.text.Paragraph(" "));
                        documento.add(new com.itextpdf.text.Paragraph("──────────────────────────────"));
                        documento.add(new com.itextpdf.text.Paragraph(" "));

                        // 🧾 Tabla
                        com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(4);
                        tabla.setWidthPercentage(100);
                        tabla.addCell("Usuario");
                        tabla.addCell("Plaza");
                        tabla.addCell("Entrada");
                        tabla.addCell("Salida");

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String usuario = doc.getString("usuarioNombre");
                            Object plaza = doc.get("plazaNumero");
                            Object entrada = doc.get("horaEntrada");
                            Object salida = doc.get("horaSalida");

                            if (usuario == null) usuario = "Desconocido";
                            String plazaTxt = (plaza != null) ? plaza.toString() : "—";
                            String entradaTxt = convertirAString(entrada);
                            String salidaTxt = convertirAString(salida);
                            if (salidaTxt == null) salidaTxt = "En curso";

                            tabla.addCell(usuario);
                            tabla.addCell(plazaTxt);
                            tabla.addCell(entradaTxt);
                            tabla.addCell(salidaTxt);
                        }

                        documento.add(tabla);
                        documento.close();

                        Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show();

                        // 🔗 Opción para compartir el archivo
                        compartirPDF(archivo);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al leer datos para exportar", Toast.LENGTH_SHORT).show()
                );
    }

    private void compartirPDF(File archivo) {
        try {
            Uri uri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    archivo
            );

            Intent compartir = new Intent(Intent.ACTION_SEND);
            compartir.setType("application/pdf");
            compartir.putExtra(Intent.EXTRA_STREAM, uri);
            compartir.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(compartir, "Compartir PDF con..."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al compartir PDF", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 🔹 Convierte un objeto (Timestamp, String, Long, etc.) a texto legible.
     */
    private String convertirAString(Object valor) {
        if (valor == null) return "—";

        // Si Firestore guarda la fecha como Timestamp
        if (valor instanceof com.google.firebase.Timestamp) {
            java.util.Date fecha = ((com.google.firebase.Timestamp) valor).toDate();
            return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(fecha);
        }

        // Si ya es String
        if (valor instanceof String) {
            return (String) valor;
        }

        // Si es número (por ejemplo, milisegundos)
        if (valor instanceof Number) {
            java.util.Date fecha = new java.util.Date(((Number) valor).longValue());
            return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(fecha);
        }

        // En cualquier otro caso, devuelve su texto
        return valor.toString();
    }

}
