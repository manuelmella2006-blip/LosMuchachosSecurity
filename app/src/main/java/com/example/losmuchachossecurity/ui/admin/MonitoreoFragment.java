package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.PlazaRepository;
import com.example.losmuchachossecurity.model.Plaza;
import java.util.ArrayList;
import java.util.List;

public class MonitoreoFragment extends Fragment {

    private RecyclerView recyclerViewPlazas;
    private TextView tvEstadoGeneral, tvDisponibles, tvOcupados;
    private PlazaRepository plazaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        // Inicializar vistas
        recyclerViewPlazas = view.findViewById(R.id.recyclerViewPlazasAdmin);
        tvEstadoGeneral = view.findViewById(R.id.tvEstadoGeneral);
        tvDisponibles = view.findViewById(R.id.tvDisponiblesAdmin);
        tvOcupados = view.findViewById(R.id.tvOcupadosAdmin);

        // Configurar RecyclerView
        recyclerViewPlazas.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Inicializar repositorio
        plazaRepository = new PlazaRepository();

        // Cargar datos en tiempo real
        cargarMonitoreo();

        return view;
    }

    private void cargarMonitoreo() {
        plazaRepository.obtenerPlazas(new PlazaRepository.PlazaCallback() {
            @Override
            public void onPlazasObtenidas(List<Plaza> plazas) {
                if (plazas != null) {
                    actualizarEstadisticas(plazas);
                }
            }

            @Override
            public void onError(String mensaje) {
                // Manejar error
            }
        });
    }

    private void actualizarEstadisticas(List<Plaza> plazas) {
        int disponibles = 0;
        int ocupados = 0;

        for (Plaza plaza : plazas) {
            if (plaza.isDisponible()) {
                disponibles++;
            } else {
                ocupados++;
            }
        }

        tvDisponibles.setText(String.valueOf(disponibles));
        tvOcupados.setText(String.valueOf(ocupados));
        tvEstadoGeneral.setText("Sistema Operativo");
    }
}