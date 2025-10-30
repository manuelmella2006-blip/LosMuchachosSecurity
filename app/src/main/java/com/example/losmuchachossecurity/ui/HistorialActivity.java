package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;

public class HistorialActivity extends AppCompatActivity {

    private TableLayout tabla;
    private Button btnVolverHistorial;
    private TextView tvResumen;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        db = FirebaseFirestore.getInstance();
        tabla = findViewById(R.id.tableHistorial);
        btnVolverHistorial = findViewById(R.id.btnVolverHistorial);
        tvResumen = findViewById(R.id.tvResumen);

        // 🔹 Escuchar Firestore en tiempo real
        escucharCambiosEnTiempoReal();

        btnVolverHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * 🔄 Escucha en tiempo real los cambios en la colección "registros"
     */
    private void escucharCambiosEnTiempoReal() {
        listenerRegistros = db.collection("registros")
                .orderBy("horaEntrada")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("FirestoreHistorial", "❌ Error al escuchar Firestore", e);
                            mostrarFilaMensaje("❌ Error al conectar con Firestore.");
                            return;
                        }

                        if (snapshots == null || snapshots.isEmpty()) {
                            mostrarFilaMensaje("⚠️ No hay registros disponibles.");
                            tvResumen.setText("Entradas activas: 0 | Completadas: 0");
                            return;
                        }

                        // 🔹 Contadores
                        int activas = 0;
                        int completadas = 0;

                        // 🔹 Limpiar tabla (mantener encabezado)
                        if (tabla.getChildCount() > 1) {
                            tabla.removeViews(1, tabla.getChildCount() - 1);
                        }

                        for (QueryDocumentSnapshot doc : snapshots) {
                            String usuario = doc.getString("usuarioNombre");
                            Object plazaField = doc.get("plazaNumero");
                            Object entradaField = doc.get("horaEntrada");
                            Object salidaField = doc.get("horaSalida");

                            String entrada = convertirAString(entradaField);
                            String salida = convertirAString(salidaField);

                            if (usuario == null) usuario = "Desconocido";
                            if (entrada == null) entrada = "—";

                            boolean enCurso = (salida == null);
                            if (enCurso) {
                                salida = "En curso ⏳";
                                activas++;
                            } else {
                                completadas++;
                            }

                            String plazaTexto;
                            if (plazaField instanceof Number) {
                                plazaTexto = String.valueOf(((Number) plazaField).intValue());
                            } else if (plazaField instanceof String) {
                                plazaTexto = (String) plazaField;
                            } else {
                                plazaTexto = "—";
                            }

                            // Crear fila
                            TableRow fila = new TableRow(HistorialActivity.this);
                            fila.setPadding(12, 12, 12, 12);

                            // Colorear por estado
                            if (enCurso) {
                                fila.setBackgroundColor(Color.parseColor("#FFF8D8")); // Amarillo claro
                            } else {
                                fila.setBackgroundColor(Color.parseColor("#E6F8E0")); // Verde suave
                            }

                            fila.addView(crearCelda(usuario));
                            fila.addView(crearCelda(plazaTexto));
                            fila.addView(crearCelda(entrada));
                            fila.addView(crearCelda(salida));

                            tabla.addView(fila);
                        }

                        // 🔹 Actualizar resumen
                        tvResumen.setText("Entradas activas: " + activas + " | Completadas: " + completadas);
                    }
                });
    }

    private String convertirAString(Object campo) {
        if (campo == null) return null;
        if (campo instanceof Timestamp) {
            Timestamp ts = (Timestamp) campo;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(ts.toDate());
        }
        if (campo instanceof String) return (String) campo;
        return campo.toString();
    }

    private TextView crearCelda(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(Color.parseColor("#222222"));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 12, 16, 12);
        tv.setTextSize(16);
        return tv;
    }

    private void mostrarFilaMensaje(String mensaje) {
        TableRow fila = new TableRow(this);
        TextView tv = crearCelda(mensaje);
        tv.setTextColor(Color.parseColor("#555555"));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        fila.addView(tv);
        tabla.addView(fila);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistros != null) {
            listenerRegistros.remove();
        }
    }
}
