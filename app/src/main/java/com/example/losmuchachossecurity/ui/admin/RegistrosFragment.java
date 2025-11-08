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
import com.example.losmuchachossecurity.data.RegistroRepository;
import com.example.losmuchachossecurity.model.Registro;
import java.util.ArrayList;
import java.util.List;

public class RegistrosFragment extends Fragment {

    private RecyclerView recyclerViewRegistros;
    private RegistroAdapter registroAdapter;
    private RegistroRepository registroRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registros, container, false);

        // Inicializar vistas
        recyclerViewRegistros = view.findViewById(R.id.recyclerViewRegistros);
        recyclerViewRegistros.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar adapter
        registroAdapter = new RegistroAdapter(new ArrayList<>());
        recyclerViewRegistros.setAdapter(registroAdapter);

        // Inicializar repositorio
        registroRepository = new RegistroRepository();

        // Cargar registros
        cargarRegistros();

        return view;
    }

    private void cargarRegistros() {
        registroRepository.obtenerRegistros(new RegistroRepository.RegistrosCallback() {
            @Override
            public void onRegistrosObtenidos(List<Registro> registros) {
                if (registros != null && !registros.isEmpty()) {
                    registroAdapter.actualizarRegistros(registros);
                } else {
                    Toast.makeText(getContext(), "No hay registros disponibles",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(getContext(), "Error al cargar registros: " + mensaje,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}