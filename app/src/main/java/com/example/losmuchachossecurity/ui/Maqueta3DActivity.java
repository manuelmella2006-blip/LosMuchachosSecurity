package com.example.losmuchachossecurity.ui;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.losmuchachossecurity.R;

/**
 * Activity para mostrar la visualización 3D de la maqueta
 * del sistema de estacionamiento inteligente
 */
public class Maqueta3DActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maqueta3d);

        // Configurar status bar
        getWindow().setStatusBarColor(getColor(R.color.ust_green_primary));

        // Botón Volver
        Button btnVolver3D = findViewById(R.id.btnVolver3D);
        btnVolver3D.setOnClickListener(v -> {
            // Simplemente cerrar la actividad para volver
            finish();
        });
    }
}