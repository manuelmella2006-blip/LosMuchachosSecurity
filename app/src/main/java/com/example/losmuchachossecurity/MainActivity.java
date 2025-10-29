package com.example.losmuchachossecurity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnMaqueta3D, btnHistorial, btnControl;
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
    }

    /**
     * 🔍 Prueba de conexión con Cloud Firestore
     * Crea un documento temporal y luego lee algunos datos de Firestore.
     */
    private void probarConexionFirestore() {
        // Escribir documento de prueba
        Map<String, Object> datosPrueba = new HashMap<>();
        datosPrueba.put("fecha", "30/10/2025");
        datosPrueba.put("mensaje", "Conexión Firestore OK 💙");

        db.collection("testConexion")
                .add(datosPrueba)
                .addOnSuccessListener(docRef -> {
                    Log.d("FirestoreTest", "✅ Documento agregado: " + docRef.getId());
                    // Luego leer algunos datos de la colección "vehicles"
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
