package com.example.losmuchachossecurity.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;

public class HistorialFragment extends Fragment {

    private TableLayout tabla;
    private TextView tvResumen;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistros;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        db = FirebaseFirestore.getInstance();
        tabla = view.findViewById(R.id.tableHistorial);
        tvResumen = view.findViewById(R.id.tvResumen);

        // Escuchar cambios en tiempo real
        escucharCambiosEnTiempoReal();

        return view;
    }

    private void escucharCambiosEnTiempoReal() {
        listenerRegistros = db.collection("registros")
                .orderBy("horaEntrada")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("HistorialFragment", "❌ Error al escuchar Firestore", e);
                            mostrarFilaMensaje("❌ Error al conectar con Firestore.");
                            return;
                        }

                        if (snapshots == null || snapshots.isEmpty()) {
                            mostrarFilaMensaje("⚠️ No hay registros disponibles.");
                            tvResumen.setText("Entradas activas: 0 | Completadas: 0");
                            return;
                        }

                        int activas = 0;
                        int completadas = 0;

                        // Limpiar tabla (mantener encabezado)
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
                            TableRow fila = new TableRow(getContext());
                            fila.setPadding(12, 12, 12, 12);

                            // Colorear por estado
                            if (enCurso) {
                                fila.setBackgroundColor(Color.parseColor("#FFF8D8"));
                            } else {
                                fila.setBackgroundColor(Color.parseColor("#E6F8E0"));
                            }

                            fila.addView(crearCelda(usuario));
                            fila.addView(crearCelda(plazaTexto));
                            fila.addView(crearCelda(entrada));
                            fila.addView(crearCelda(salida));

                            tabla.addView(fila);
                        }

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
        TextView tv = new TextView(getContext());
        tv.setText(texto);
        tv.setTextColor(Color.parseColor("#222222"));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 12, 16, 12);
        tv.setTextSize(16);
        return tv;
    }

    private void mostrarFilaMensaje(String mensaje) {
        TableRow fila = new TableRow(getContext());
        TextView tv = crearCelda(mensaje);
        tv.setTextColor(Color.parseColor("#555555"));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        fila.addView(tv);
        tabla.addView(fila);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistros != null) {
            listenerRegistros.remove();
        }
    }
}