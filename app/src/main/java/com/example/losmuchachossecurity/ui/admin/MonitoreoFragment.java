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

    // ✅ CORREGIDO: Solo los IDs que existen en fragment_monitoreo.xml
    private TextView tvDisponiblesAdmin, tvOcupadosAdmin;
    private CardView cardMaqueta;
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
        cargarMonitoreo();

        // Listener para ir a Maqueta 3D
        if (cardMaqueta != null) {
            cardMaqueta.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Maqueta3DActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    private void initViews(View view) {
        // ✅ Solo inicializamos lo que EXISTE en el XML nuevo
        tvDisponiblesAdmin = view.findViewById(R.id.tvDisponiblesAdmin);
        tvOcupadosAdmin = view.findViewById(R.id.tvOcupadosAdmin);
        cardMaqueta = view.findViewById(R.id.cardMaqueta);
        recyclerViewPlazasAdmin = view.findViewById(R.id.recyclerViewPlazasAdmin);

        // ✅ IMPORTANTE: Cambiar a 5 columnas para vista en tiempo real
        recyclerViewPlazasAdmin.setLayoutManager(new GridLayoutManager(getContext(), 5));
    }

    private void cargarMonitoreo() {
        plazaRepository.obtenerPlazas(new PlazaRepository.PlazaCallback() {
            @Override
            public void onPlazasObtenidas(List<Plaza> plazas) {
                if (plazas != null && !plazas.isEmpty()) {
                    actualizarEstadisticas(plazas);
                    // ✅ DESCOMENTAR cuando tengas PlazaAdminAdapter listo
                    // recyclerViewPlazasAdmin.setAdapter(new PlazaAdminAdapter(plazas));
                } else {
                    tvDisponiblesAdmin.setText("0");
                    tvOcupadosAdmin.setText("0");
                }
            }

            @Override
            public void onError(String mensaje) {
                tvDisponiblesAdmin.setText("0");
                tvOcupadosAdmin.setText("0");
            }
        });
    }

    private void actualizarEstadisticas(List<Plaza> plazas) {
        int disponibles = 0;
        int ocupados = 0;

        for (Plaza p : plazas) {
            if (p.isDisponible()) {
                disponibles++;
            } else {
                ocupados++;
            }
        }

        tvDisponiblesAdmin.setText(String.valueOf(disponibles));
        tvOcupadosAdmin.setText(String.valueOf(ocupados));
    }
}