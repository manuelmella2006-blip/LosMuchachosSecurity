package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;

public class ControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control);

        Button btnVolverControl = findViewById(R.id.btnVolverControl);

        btnVolverControl.setOnClickListener(v -> {
            Intent intent = new Intent(ControlActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
