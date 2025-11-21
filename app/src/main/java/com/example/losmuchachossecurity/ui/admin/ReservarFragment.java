package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.PlazaRepository;
import com.example.losmuchachossecurity.model.Plaza;
import com.example.losmuchachossecurity.ui.usuario.PlazaReservaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class ReservarFragment extends Fragment {

    private RecyclerView recyclerViewDisponibles;
    private PlazaReservaAdapter reservaAdapter;
    private PlazaRepository plazaRepository;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservar, container, false);

        mAuth = FirebaseAuth.getInstance();
        plazaRepository = new PlazaRepository();

        // Configurar RecyclerView
        recyclerViewDisponibles = view.findViewById(R.id.recyclerViewDisponibles);
        recyclerViewDisponibles.setLayoutManager(new LinearLayoutManager(getContext()));

        reservaAdapter = new PlazaReservaAdapter(new ArrayList<>(), this::reservarPlaza);
        recyclerViewDisponibles.setAdapter(reservaAdapter);

        // Cargar plazas disponibles
        cargarPlazasDisponibles();

        return view;
    }

    private void cargarPlazasDisponibles() {
        plazaRepository.obtenerPlazas(new PlazaRepository.PlazaCallback() {
            @Override
            public void onPlazasObtenidas(List<Plaza> plazas) {
                if (plazas != null) {
                    List<Plaza> disponibles = new ArrayList<>();
                    for (Plaza plaza : plazas) {
                        if (plaza.isDisponible()) {
                            disponibles.add(plaza);
                        }
                    }
                    reservaAdapter.actualizarPlazas(disponibles);
                }
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(getContext(), "Error al cargar plazas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reservarPlaza(Plaza plaza) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            // Actualizar estado de la plaza en Firebase
            plazaRepository.reservarPlaza(plaza.getId(), userId, new PlazaRepository.ReservaCallback() {
                @Override
                public void onReservaExitosa() {
                    Toast.makeText(getContext(), "Plaza " + plaza.getNumero() + " reservada exitosamente",
                            Toast.LENGTH_SHORT).show();
                    cargarPlazasDisponibles();
                }

                @Override
                public void onError(String mensaje) {
                    Toast.makeText(getContext(), "Error al reservar: " + mensaje,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}