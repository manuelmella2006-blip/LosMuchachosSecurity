package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.PlazaRepository;
import com.example.losmuchachossecurity.model.Plaza;
import com.example.losmuchachossecurity.ui.Maqueta3DActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MonitoreoFragment extends Fragment {

    private TextView tvWelcome, tvEstadoGeneral, tvDisponiblesAdmin, tvOcupadosAdmin;
    private CardView cardMaqueta, cardDisponibles, cardOcupadas;
    private RecyclerView recyclerViewPlazasAdmin;
    private PlazaRepository plazaRepository;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        mAuth = FirebaseAuth.getInstance();
        plazaRepository = new PlazaRepository();

        initViews(view);
        loadUserInfo();
        cargarMonitoreo();

        if (cardMaqueta != null) {
            cardMaqueta.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Maqueta3DActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvEstadoGeneral = view.findViewById(R.id.tvEstadoGeneral);
        tvDisponiblesAdmin = view.findViewById(R.id.tvDisponiblesAdmin);
        tvOcupadosAdmin = view.findViewById(R.id.tvOcupadosAdmin);

        cardMaqueta = view.findViewById(R.id.cardMaqueta);
        cardDisponibles = view.findViewById(R.id.cardDisponibles);
        cardOcupadas = view.findViewById(R.id.cardOcupadas);

        recyclerViewPlazasAdmin = view.findViewById(R.id.recyclerViewPlazasAdmin);
        recyclerViewPlazasAdmin.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String nombre = email != null ? email.split("@")[0] : "Administrador";
            if (tvWelcome != null) {
                tvWelcome.setText("¡Hola, " + capitalize(nombre) + "!");
            }
        }
    }

    private void cargarMonitoreo() {
        plazaRepository.obtenerPlazas(new PlazaRepository.PlazaCallback() {
            @Override
            public void onPlazasObtenidas(List<Plaza> plazas) {
                if (plazas != null && !plazas.isEmpty()) {
                    actualizarEstadisticas(plazas);
                    // recyclerViewPlazasAdmin.setAdapter(new PlazaAdapter(plazas));
                } else {
                    tvDisponiblesAdmin.setText("0");
                    tvOcupadosAdmin.setText("0");
                    if (tvEstadoGeneral != null) tvEstadoGeneral.setText("Sin datos");
                }
            }

            @Override
            public void onError(String mensaje) {
                tvDisponiblesAdmin.setText("0");
                tvOcupadosAdmin.setText("0");
                if (tvEstadoGeneral != null) tvEstadoGeneral.setText("Sin conexión");
            }
        });
    }

    private void actualizarEstadisticas(List<Plaza> plazas) {
        int disponibles = 0;
        int ocupados = 0;
        for (Plaza p : plazas) {
            if (p.isDisponible()) disponibles++; else ocupados++;
        }
        tvDisponiblesAdmin.setText(String.valueOf(disponibles));
        tvOcupadosAdmin.setText(String.valueOf(ocupados));
        if (tvEstadoGeneral != null) tvEstadoGeneral.setText("Sistema Operativo");
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
