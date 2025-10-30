package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;

public class Maqueta3DActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maqueta3d);

        Button btnVolver3D = findViewById(R.id.btnVolver3D);

        btnVolver3D.setOnClickListener(v -> {
            Intent intent = new Intent(Maqueta3DActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
