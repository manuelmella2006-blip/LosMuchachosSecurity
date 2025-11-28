package com.example.losmuchachossecurity.ui.admin; // <-- CAMBIA si tu paquete es otro
import com.google.firebase.firestore.FieldValue;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservarFragment extends Fragment {

    // UI
    private RecyclerView recyclerViewDisponibles;
    private Spinner spinnerPlaza;
    private EditText etFecha, etHoraInicio, etHoraFin;
    private ImageButton btnCalendar, btnTimeInicio, btnTimeFin;
    private Button btnReservar;

    // Datos
    private final List<Plaza> plazasDisponibles = new ArrayList<>();
    private final List<String> plazasIds = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private PlazaDisponibleAdapter plazasAdapter;

    // Firebase
    private FirebaseFirestore db;
    private CollectionReference plazasRef;
    private CollectionReference reservasRef;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reservar, container, false);

        // Firebase
        db = FirebaseFirestore.getInstance();
        plazasRef = db.collection("plazas");
        reservasRef = db.collection("reservas");
        mAuth = FirebaseAuth.getInstance();

        // Referencias UI
        recyclerViewDisponibles = view.findViewById(R.id.recyclerViewDisponibles);
        spinnerPlaza          = view.findViewById(R.id.spinnerPlaza);
        etFecha               = view.findViewById(R.id.etFecha);
        etHoraInicio          = view.findViewById(R.id.etHoraInicio);
        etHoraFin             = view.findViewById(R.id.etHoraFin);
        btnCalendar           = view.findViewById(R.id.btnCalendar);
        btnTimeInicio         = view.findViewById(R.id.btnTimeInicio);
        btnTimeFin            = view.findViewById(R.id.btnTimeFin);
        btnReservar           = view.findViewById(R.id.btnReservar);

        // RecyclerView
        recyclerViewDisponibles.setLayoutManager(new LinearLayoutManager(getContext()));
        plazasAdapter = new PlazaDisponibleAdapter(plazasDisponibles, plaza -> {
            // Cuando se toca una plaza en la lista, la seleccionamos en el spinner
            int index = plazasIds.indexOf(plaza.getId());
            if (index >= 0) spinnerPlaza.setSelection(index);
        });
        recyclerViewDisponibles.setAdapter(plazasAdapter);

        // Spinner
        spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                plazasIds
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaza.setAdapter(spinnerAdapter);

        configurarFechaHora();
        configurarBotonReservar();

        // Cargar plazas disponibles
        cargarPlazasDisponibles();

        return view;
    }

    // =========================
    // CONFIGURAR FECHA / HORA
    // =========================
    private void configurarFechaHora() {
        // Abrir DatePicker al tocar el EditText o el botón
        View.OnClickListener fechaClickListener = v -> mostrarDatePicker();
        etFecha.setOnClickListener(fechaClickListener);
        btnCalendar.setOnClickListener(fechaClickListener);

        // Hora inicio
        View.OnClickListener horaInicioClick = v -> mostrarTimePicker(etHoraInicio);
        etHoraInicio.setOnClickListener(horaInicioClick);
        btnTimeInicio.setOnClickListener(horaInicioClick);

        // Hora fin
        View.OnClickListener horaFinClick = v -> mostrarTimePicker(etHoraFin);
        etHoraFin.setOnClickListener(horaFinClick);
        btnTimeFin.setOnClickListener(horaFinClick);
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    // Formato simple: dd/MM/yyyy
                    String fecha = String.format("%02d/%02d/%04d", dayOfMonth, (month1 + 1), year1);
                    etFecha.setText(fecha);
                },
                year, month, day
        );
        dialog.show();
    }

    private void mostrarTimePicker(EditText target) {
        final Calendar c = Calendar.getInstance();
        int hour   = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                requireContext(),
                (timePicker, hourOfDay, minute1) -> {
                    String hora = String.format("%02d:%02d", hourOfDay, minute1);
                    target.setText(hora);
                },
                hour, minute, true
        );
        dialog.show();
    }

    // =========================
    // CARGAR PLAZAS DISPONIBLES
    // =========================
    private void cargarPlazasDisponibles() {
        plazasDisponibles.clear();
        plazasIds.clear();
        plazasAdapter.notifyDataSetChanged();
        spinnerAdapter.notifyDataSetChanged();

        // Se asume estructura: plazas/{id} con campo boolean "ocupado"
        plazasRef.whereEqualTo("ocupado", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        // Podrías leer más campos si los tienes (ej: nivel, tipo, etc.)
                        Plaza plaza = new Plaza(id);
                        plazasDisponibles.add(plaza);
                        plazasIds.add(id);
                    }
                    plazasAdapter.notifyDataSetChanged();
                    spinnerAdapter.notifyDataSetChanged();

                    if (plazasIds.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No hay plazas disponibles actualmente",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error cargando plazas: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // =========================
    // BOTÓN RESERVAR
    // =========================
    private void configurarBotonReservar() {
        btnReservar.setOnClickListener(v -> {
            String plazaSeleccionada = (String) spinnerPlaza.getSelectedItem();
            String fecha = etFecha.getText().toString().trim();
            String horaInicio = etHoraInicio.getText().toString().trim();
            String horaFin = etHoraFin.getText().toString().trim();

            if (TextUtils.isEmpty(plazaSeleccionada)) {
                Toast.makeText(getContext(), "Selecciona una plaza", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(fecha)) {
                Toast.makeText(getContext(), "Selecciona la fecha", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(horaInicio)) {
                Toast.makeText(getContext(), "Selecciona la hora de inicio", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(horaFin)) {
                Toast.makeText(getContext(), "Selecciona la hora de fin", Toast.LENGTH_SHORT).show();
                return;
            }

            guardarReserva(plazaSeleccionada, fecha, horaInicio, horaFin);
        });
    }

    private void guardarReserva(String plazaId, String fecha,
                                String horaInicio, String horaFin) {

        String userId = null;
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        // Datos de la reserva
        Map<String, Object> data = new HashMap<>();
        data.put("plazaId", plazaId);
        data.put("fecha", fecha);
        data.put("horaInicio", horaInicio);
        data.put("horaFin", horaFin);
        data.put("estado", "pendiente"); // o "confirmada" según quieras
        data.put("timestampCreacion", FieldValue.serverTimestamp());
        if (userId != null) {
            data.put("userId", userId);
        }

        reservasRef.add(data)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(getContext(),
                            "Reserva registrada correctamente",
                            Toast.LENGTH_SHORT).show();

                    // 🔥 Marcar la plaza como OCUPADA / RESERVADA en Firestore
                    plazasRef.document(plazaId)
                            .update(
                                    "ocupado", true,
                                    "estado", "RESERVADA",
                                    "ultima_actualizacion", FieldValue.serverTimestamp()
                            );

                    // Limpiar campos del formulario
                    etFecha.setText("");
                    etHoraInicio.setText("");
                    etHoraFin.setText("");

                    // Recargar plazas disponibles (ya no debe aparecer la recién reservada)
                    cargarPlazasDisponibles();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error al registrar reserva: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }


    // =========================
    // CLASE PLAZA
    // =========================
    private static class Plaza {
        private final String id;

        public Plaza(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    // =========================
    // ADAPTER RECYCLERVIEW
    // =========================
    private static class PlazaDisponibleAdapter
            extends RecyclerView.Adapter<PlazaDisponibleAdapter.PlazaViewHolder> {

        interface OnPlazaClickListener {
            void onPlazaClick(Plaza plaza);
        }

        private final List<Plaza> plazas;
        private final OnPlazaClickListener listener;

        PlazaDisponibleAdapter(List<Plaza> plazas, OnPlazaClickListener listener) {
            this.plazas = plazas;
            this.listener = listener;
        }

        @NonNull
        @Override
        public PlazaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_plaza_disponible, parent, false);
            return new PlazaViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PlazaViewHolder holder, int position) {
            Plaza plaza = plazas.get(position);
            holder.bind(plaza, listener);
        }

        @Override
        public int getItemCount() {
            return plazas.size();
        }

        static class PlazaViewHolder extends RecyclerView.ViewHolder {

            TextView tvNombrePlaza;

            PlazaViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombrePlaza = itemView.findViewById(R.id.tvNombrePlaza);
            }

            void bind(Plaza plaza, OnPlazaClickListener listener) {
                tvNombrePlaza.setText("Plaza " + plaza.getId());
                itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onPlazaClick(plaza);
                });
            }
        }
    }
}
