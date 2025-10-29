package com.example.losmuchachossecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    // Clase interna para los datos de cada evento
    static class Evento {
        String fecha;
        String descripcion;
        String estado;

        Evento(String fecha, String descripcion, String estado) {
            this.fecha = fecha;
            this.descripcion = descripcion;
            this.estado = estado;
        }
    }

    private final ArrayList<Evento> listaEventos = new ArrayList<>();
    private TableLayout tabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        tabla = findViewById(R.id.tableHistorial);
        Button btnVolverHistorial = findViewById(R.id.btnVolverHistorial);

        // ✅ Cargar datos de ejemplo
        listaEventos.add(new Evento("29/10/2025 14:30", "Puerta abierta", "Completado"));
        listaEventos.add(new Evento("29/10/2025 14:35", "Sensor de movimiento", "Detectado"));
        listaEventos.add(new Evento("29/10/2025 14:50", "Luz encendida", "Activo"));
        listaEventos.add(new Evento("30/10/2025 09:10", "Puerta cerrada", "Completado"));
        listaEventos.add(new Evento("30/10/2025 09:45", "Sensor apagado", "Inactivo"));

        mostrarTabla();

        // ✅ Botón para volver al menú principal
        btnVolverHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // 🔹 Método que muestra los eventos en la tabla
    private void mostrarTabla() {
        tabla.removeViews(1, tabla.getChildCount() - 1); // eliminar filas anteriores (mantiene encabezado)

        for (int i = 0; i < listaEventos.size(); i++) {
            Evento e = listaEventos.get(i);

            TableRow fila = new TableRow(this);
            fila.setPadding(8, 8, 8, 8);
            if (i % 2 == 0) fila.setBackgroundColor(0xFFF0F0F0);

            TextView fecha = crearCelda(e.fecha);
            TextView evento = crearCelda(e.descripcion);
            TextView estado = crearCelda(e.estado);

            fila.addView(fecha);
            fila.addView(evento);
            fila.addView(estado);

            int finalI = i;
            fila.setOnClickListener(v -> mostrarDialogoEdicion(finalI));

            tabla.addView(fila);
        }
    }

    // 🔹 Crear una celda visualmente uniforme
    private TextView crearCelda(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(0xFF333333);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    // 🔹 Diálogo para editar los datos
    private void mostrarDialogoEdicion(int index) {
        Evento evento = listaEventos.get(index);

        // Crear campos de texto editables
        EditText etFecha = new EditText(this);
        etFecha.setHint("Fecha y hora");
        etFecha.setText(evento.fecha);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción del evento");
        etDescripcion.setText(evento.descripcion);

        EditText etEstado = new EditText(this);
        etEstado.setHint("Estado");
        etEstado.setText(evento.estado);

        // Crear un layout vertical para los campos
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(etFecha);
        layout.addView(etDescripcion);
        layout.addView(etEstado);

        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar evento")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    evento.fecha = etFecha.getText().toString();
                    evento.descripcion = etDescripcion.getText().toString();
                    evento.estado = etEstado.getText().toString();
                    mostrarTabla(); // refrescar la tabla
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Eliminar", (dialog, which) -> {
                    listaEventos.remove(index);
                    mostrarTabla(); // refrescar
                })
                .show();
    }
}
