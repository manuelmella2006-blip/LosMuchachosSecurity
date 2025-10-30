package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnMaqueta3D, btnHistorial, btnControl, btnAdmin;
    private FirebaseFirestore db; // 🔹 Conexión a Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🔹 Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // 🔹 Probar conexión (escribir y leer datos)
        probarConexionFirestore();

        // 🔹 Referencias a los botones
        btnMaqueta3D = findViewById(R.id.btnMaqueta3D);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnControl = findViewById(R.id.btnControl);
        btnAdmin = findViewById(R.id.btnAdmin); // 👈 Nuevo botón administrador

        // 🔹 Navegación entre pantallas
        btnMaqueta3D.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Maqueta3DActivity.class);
            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });

        btnControl.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            startActivity(intent);
        });

        // 🔹 Modo administrador
        btnAdmin.setOnClickListener(v -> {
            // Aquí podrías validar el rol del usuario (si lo deseas)
            // Por ahora entra directo al panel admin
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Modo administrador", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 🔍 Prueba de conexión con Cloud Firestore
     */
    private void probarConexionFirestore() {
        Map<String, Object> datosPrueba = new HashMap<>();
        datosPrueba.put("fecha", "30/10/2025");
        datosPrueba.put("mensaje", "Conexión Firestore OK 💙");

        db.collection("testConexion")
                .add(datosPrueba)
                .addOnSuccessListener(docRef -> {
                    Log.d("FirestoreTest", "✅ Documento agregado: " + docRef.getId());
                    leerDatosDeVehicles();
                })
                .addOnFailureListener(e -> Log.e("FirestoreTest", "❌ Error al escribir", e));
    }

    /**
     * 🔍 Leer datos reales de la colección "vehicles"
     */
    private void leerDatosDeVehicles() {
        db.collection("vehicles")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            Log.d("FirestoreVehicles", "🚗 ID: " + document.getId() + " → " + document.getData());
                        }
                    } else {
                        Log.w("FirestoreVehicles", "⚠️ No hay documentos en 'vehicles'.");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreVehicles", "❌ Error al leer 'vehicles'", e));
    }
}
