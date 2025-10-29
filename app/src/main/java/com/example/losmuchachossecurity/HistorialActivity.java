package com.example.losmuchachossecurity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HistorialActivity extends AppCompatActivity {

    private TableLayout tabla;
    private Button btnVolverHistorial;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        // 🔹 Inicializar Firestore y componentes
        db = FirebaseFirestore.getInstance();
        tabla = findViewById(R.id.tableHistorial);
        btnVolverHistorial = findViewById(R.id.btnVolverHistorial);

        // 🔹 Cargar datos reales desde Firestore
        cargarDatosDesdeFirestore();

        // 🔹 Botón volver
        btnVolverHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarDatosDesdeFirestore() {
        db.collection("vehicles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        mostrarFilaMensaje("⚠️ No hay vehículos registrados en Firestore.");
                        return;
                    }

                    // Limpia filas antiguas excepto encabezado
                    if (tabla.getChildCount() > 1) {
                        tabla.removeViews(1, tabla.getChildCount() - 1);
                    }

                    // Recorre los documentos de la colección
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String id = document.getId();
                        Object expirationDate = document.get("expirationDate");

                        TableRow fila = new TableRow(this);
                        fila.setPadding(8, 8, 8, 8);

                        TextView col1 = crearCelda(id);
                        TextView col2 = crearCelda(expirationDate != null ? expirationDate.toString() : "Sin fecha");

                        fila.addView(col1);
                        fila.addView(col2);
                        tabla.addView(fila);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHistorial", "❌ Error al leer Firestore", e);
                    mostrarFilaMensaje("❌ Error al cargar los datos de Firestore.");
                });
    }

    private TextView crearCelda(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(0xFF333333);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private void mostrarFilaMensaje(String mensaje) {
        TableRow fila = new TableRow(this);
        TextView tv = crearCelda(mensaje);
        tv.setTextColor(0xFF555555);
        tv.setGravity(Gravity.CENTER);
        fila.addView(tv);
        tabla.addView(fila);
    }
}
