package com.example.losmuchachossecurity.ui.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.losmuchachossecurity.R;

public class NavegacionFragment extends Fragment {

    private TextView tvDireccion, tvInstrucciones;
    private Button btnVerMapa, btnCompartir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navegacion, container, false);

        // Inicializar vistas
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvInstrucciones = view.findViewById(R.id.tvInstrucciones);
        btnVerMapa = view.findViewById(R.id.btnVerMapa);
        btnCompartir = view.findViewById(R.id.btnCompartir);

        // Configurar información del estacionamiento
        tvDireccion.setText("Av. Principal #123, Ciudad");
        tvInstrucciones.setText("Instrucciones:\n" +
                "1. Desde la entrada principal, gire a la derecha\n" +
                "2. Siga por el pasillo hasta encontrar su plaza reservada\n" +
                "3. Las plazas están numeradas claramente");

        // Configurar botones
        btnVerMapa.setOnClickListener(v -> {
            // Aquí puedes abrir Google Maps o una Activity con mapa
            // Intent intent = new Intent(getContext(), MapaActivity.class);
            // startActivity(intent);
        });

        btnCompartir.setOnClickListener(v -> {
            // Compartir ubicación
        });

        return view;
    }
}