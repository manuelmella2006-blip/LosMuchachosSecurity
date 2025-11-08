package com.example.losmuchachossecurity.ui.fragments;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminFragment extends Fragment {

    private Button btnResetRegistros, btnExportarPDF;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        db = FirebaseFirestore.getInstance();

        btnResetRegistros = view.findViewById(R.id.btnResetRegistros);
        btnExportarPDF = view.findViewById(R.id.btnExportarPDF);

        btnResetRegistros.setOnClickListener(v -> resetearRegistrosDelDia());
        btnExportarPDF.setOnClickListener(v -> exportarHistorialPDF());

        return view;
    }

    private void resetearRegistrosDelDia() {
        db.collection("registros")
                .get()
                .addOnSuccessListener(query -> {
                    int eliminados = 0;
                    for (var doc : query.getDocuments()) {
                        doc.getReference().delete();
                        eliminados++;
                    }
                    Toast.makeText(getContext(), "Registros eliminados: " + eliminados, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al eliminar registros", Toast.LENGTH_SHORT).show());
    }

    private void exportarHistorialPDF() {
        db.collection("registros")
                .orderBy("horaEntrada")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(getContext(), "No hay registros para exportar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        String nombreArchivo = "Historial_Estacionamiento_" +
                                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
                        File archivo = new File(requireContext().getExternalFilesDir(null), nombreArchivo);

                        com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
                        com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new FileOutputStream(archivo));
                        documento.open();

                        // Encabezado
                        documento.add(new com.itextpdf.text.Paragraph("Los Muchachos Security - Estacionamiento"));
                        documento.add(new com.itextpdf.text.Paragraph("Historial de Entradas y Salidas"));
                        documento.add(new com.itextpdf.text.Paragraph(
                                "Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date())
                        ));
                        documento.add(new com.itextpdf.text.Paragraph(" "));
                        documento.add(new com.itextpdf.text.Paragraph("──────────────────────────────"));
                        documento.add(new com.itextpdf.text.Paragraph(" "));

                        // Tabla
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

                        Toast.makeText(getContext(), "PDF generado correctamente", Toast.LENGTH_SHORT).show();
                        compartirPDF(archivo);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al generar PDF", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al leer datos para exportar", Toast.LENGTH_SHORT).show()
                );
    }

    private void compartirPDF(File archivo) {
        try {
            Uri uri = androidx.core.content.FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    archivo
            );

            Intent compartir = new Intent(Intent.ACTION_SEND);
            compartir.setType("application/pdf");
            compartir.putExtra(Intent.EXTRA_STREAM, uri);
            compartir.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(compartir, "Compartir PDF con..."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al compartir PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertirAString(Object valor) {
        if (valor == null) return "—";

        if (valor instanceof Timestamp) {
            Date fecha = ((Timestamp) valor).toDate();
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(fecha);
        }
        if (valor instanceof String) {
            return (String) valor;
        }
        if (valor instanceof Number) {
            Date fecha = new Date(((Number) valor).longValue());
            return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(fecha);
        }
        return valor.toString();
    }
}