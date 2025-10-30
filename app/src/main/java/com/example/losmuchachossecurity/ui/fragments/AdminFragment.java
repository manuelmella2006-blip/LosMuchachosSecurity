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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminFragment extends Fragment {

    private Button btnResetRegistros, btnExportarPDF;
    private FirebaseFirestore db;

    public AdminFragment() {
        // constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        db = FirebaseFirestore.getInstance();

        btnResetRegistros = view.findViewById(R.id.btnResetRegistros);
        btnExportarPDF = view.findViewById(R.id.btnExportarPDF);

        btnResetRegistros.setOnClickListener(v -> resetearRegistrosDelDia());
        btnExportarPDF.setOnClickListener(v -> exportarHistorial());

        return view;
    }

    private void resetearRegistrosDelDia() {
        db.collection("registros")
                .get()
                .addOnSuccessListener(query -> {
                    int eliminados = 0;
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        doc.getReference().delete();
                        eliminados++;
                    }
                    Toast.makeText(getContext(),
                            "Registros eliminados: " + eliminados,
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Error al eliminar registros",
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void exportarHistorial() {
        db.collection("registros")
                .orderBy("horaEntrada")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No hay registros para exportar",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        String nombreArchivo = "Historial_" +
                                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) +
                                ".pdf";

                        File archivo = new File(requireContext().getExternalFilesDir(null), nombreArchivo);

                        // Crear documento PDF
                        com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
                        com.itextpdf.text.pdf.PdfWriter.getInstance(
                                documento,
                                new FileOutputStream(archivo)
                        );
                        documento.open();

                        documento.add(new com.itextpdf.text.Paragraph("Los Muchachos Security"));
                        documento.add(new com.itextpdf.text.Paragraph("Historial de Estacionamiento"));
                        documento.add(new com.itextpdf.text.Paragraph(" "));

                        com.itextpdf.text.pdf.PdfPTable tabla = new com.itextpdf.text.pdf.PdfPTable(4);
                        tabla.addCell("Usuario");
                        tabla.addCell("Plaza");
                        tabla.addCell("Entrada");
                        tabla.addCell("Salida");

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String usuario = doc.getString("usuarioNombre");
                            Object plaza = doc.get("plazaNumero");
                            Object entrada = doc.get("horaEntrada");
                            Object salida = doc.get("horaSalida");

                            tabla.addCell(usuario != null ? usuario : "—");
                            tabla.addCell(plaza != null ? plaza.toString() : "—");
                            tabla.addCell(entrada != null ? entrada.toString() : "—");
                            tabla.addCell(salida != null ? salida.toString() : "En curso");
                        }

                        documento.add(tabla);
                        documento.close();

                        Toast.makeText(getContext(),
                                "PDF generado",
                                Toast.LENGTH_SHORT).show();

                        compartirPDF(archivo);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),
                                "Error al generar PDF",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Error al consultar registros",
                        Toast.LENGTH_SHORT).show());
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

            startActivity(Intent.createChooser(compartir, "Compartir PDF"));
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Error al compartir PDF",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
