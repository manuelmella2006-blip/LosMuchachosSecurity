package com.example.losmuchachossecurity.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.Maqueta3DActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class InicioFragment extends Fragment {

    private TextView tvWelcome, tvPlazasDisponibles, tvEntradasHoy, tvPlazasOcupadas;
    private CardView cardDisponibles, cardOcupadas, cardEntradas, cardMaqueta;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration listenerPlazas, listenerRegistros;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Vincular vistas
        initViews(view);

        // Cargar datos
        loadUserInfo();
        loadEstadisticas();

        // Configurar clic en card Maqueta 3D
        cardMaqueta.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Maqueta3DActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvPlazasDisponibles = view.findViewById(R.id.tvPlazasDisponibles);
        tvPlazasOcupadas = view.findViewById(R.id.tvPlazasOcupadas);
        tvEntradasHoy = view.findViewById(R.id.tvEntradasHoy);
        cardDisponibles = view.findViewById(R.id.cardDisponibles);
        cardOcupadas = view.findViewById(R.id.cardOcupadas);
        cardEntradas = view.findViewById(R.id.cardEntradas);
        cardMaqueta = view.findViewById(R.id.cardMaqueta);
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String nombre = email != null ? email.split("@")[0] : "Usuario";
            tvWelcome.setText("¡Hola, " + capitalize(nombre) + "!");
        }
    }

    private void loadEstadisticas() {
        // Escuchar plazas en tiempo real
        listenerPlazas = db.collection("estacionamientos")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("InicioFragment", "Error al cargar plazas", error);
                        return;
                    }

                    if (snapshots != null) {
                        int ocupadas = 0;
                        int disponibles = 0;

                        for (QueryDocumentSnapshot doc : snapshots) {
                            Boolean ocupada = doc.getBoolean("ocupada");
                            if (ocupada != null && ocupada) {
                                ocupadas++;
                            } else {
                                disponibles++;
                            }
                        }

                        tvPlazasDisponibles.setText(String.valueOf(disponibles));
                        tvPlazasOcupadas.setText(String.valueOf(ocupadas));

                        // Cambiar color según disponibilidad
                        updateCardColors(disponibles);
                    }
                });

        // Escuchar entradas de hoy
        listenerRegistros = db.collection("registros")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("InicioFragment", "Error al cargar registros", error);
                        return;
                    }

                    if (snapshots != null) {
                        int entradas = snapshots.size();
                        tvEntradasHoy.setText(String.valueOf(entradas));
                    }
                });
    }

    private void updateCardColors(int disponibles) {
        if (disponibles > 0) {
            cardDisponibles.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.ust_green_light)
            );
        } else {
            cardDisponibles.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.error)
            );
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerPlazas != null) listenerPlazas.remove();
        if (listenerRegistros != null) listenerRegistros.remove();
    }
}
