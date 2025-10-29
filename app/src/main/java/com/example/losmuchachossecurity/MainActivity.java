package com.example.losmuchachossecurity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declaramos los botones
    private Button btnMaqueta3D, btnHistorial, btnControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ✅ Eliminamos ViewCompat: no es necesario para este diseño.
        // Si lo dejas, dará error porque el XML no tiene un id "mainMenu".

        // Referencias a los botones del XML
        btnMaqueta3D = findViewById(R.id.btnMaqueta3D);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnControl = findViewById(R.id.btnControl);

        // Navegación entre pantallas
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
}
