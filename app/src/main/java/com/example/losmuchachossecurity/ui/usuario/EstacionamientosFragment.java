package com.example.losmuchachossecurity.ui.usuario;

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

public class EstacionamientosFragment extends Fragment {

    private RecyclerView recyclerView;
    private PlazaAdapter plazaAdapter;
    private TextView tvDisponibles, tvOcupados, tvTotal;
    private PlazaRepository plazaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estacionamientos, container, false);

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recyclerPlazas); // ✅ corregido
        tvDisponibles = view.findViewById(R.id.tvDisponibles);
        tvOcupados = view.findViewById(R.id.tvOcupadas); // ✅ corregido (coincide con XML)
        tvTotal = view.findViewById(R.id.tvTotal); // ⚠️ si no existe en tu XML, puedes eliminar esta línea

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        plazaAdapter = new PlazaAdapter(new ArrayList<>());
        recyclerView.setAdapter(plazaAdapter);

        // Inicializar repositorio
        plazaRepository = new PlazaRepository();

        // Cargar datos
        cargarEstacionamientos();

        return view;
    }

    private void cargarEstacionamientos() {
        plazaRepository.obtenerPlazas(new PlazaRepository.PlazaCallback() {
            @Override
            public void onPlazasObtenidas(List<Plaza> plazas) {
                if (plazas != null && !plazas.isEmpty()) {
                    plazaAdapter.actualizarPlazas(plazas);
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

        tvDisponibles.setText("Disponibles: " + disponibles);
        tvOcupados.setText("Ocupadas: " + ocupados);
        if (tvTotal != null) {
            tvTotal.setText("Total: " + plazas.size());
        }
    }
}
