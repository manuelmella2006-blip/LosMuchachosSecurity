package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.ControlEventoRepository;

public class BarrerasFragment extends Fragment {

    private Button btnAbrirEntrada, btnCerrarEntrada;
    private Button btnAbrirSalida, btnCerrarSalida;
    private Switch switchModoAutomatico;
    private TextView tvEstadoEntrada, tvEstadoSalida;
    private ControlEventoRepository controlRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barreras, container, false);

        // Inicializar vistas
        btnAbrirEntrada = view.findViewById(R.id.btnAbrirEntrada);
        btnCerrarEntrada = view.findViewById(R.id.btnCerrarEntrada);
        btnAbrirSalida = view.findViewById(R.id.btnAbrirSalida);
        btnCerrarSalida = view.findViewById(R.id.btnCerrarSalida);
        switchModoAutomatico = view.findViewById(R.id.switchModoAutomatico);
        tvEstadoEntrada = view.findViewById(R.id.tvEstadoEntrada);
        tvEstadoSalida = view.findViewById(R.id.tvEstadoSalida);

        controlRepository = new ControlEventoRepository();

        // Configurar listeners
        configurarListeners();

        return view;
    }

    private void configurarListeners() {
        btnAbrirEntrada.setOnClickListener(v -> controlarBarrera("entrada", "abrir"));
        btnCerrarEntrada.setOnClickListener(v -> controlarBarrera("entrada", "cerrar"));
        btnAbrirSalida.setOnClickListener(v -> controlarBarrera("salida", "abrir"));
        btnCerrarSalida.setOnClickListener(v -> controlarBarrera("salida", "cerrar"));

        switchModoAutomatico.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String modo = isChecked ? "automático" : "manual";
            Toast.makeText(getContext(), "Modo " + modo + " activado", Toast.LENGTH_SHORT).show();
        });
    }

    private void controlarBarrera(String tipo, String accion) {
        // Aquí conectarías con Arduino/Firebase para controlar la barrera
        String mensaje = "Barrera de " + tipo + " " + accion + "da";
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();

        if (tipo.equals("entrada")) {
            tvEstadoEntrada.setText(accion.equals("abrir") ? "ABIERTA" : "CERRADA");
        } else {
            tvEstadoSalida.setText(accion.equals("abrir") ? "ABIERTA" : "CERRADA");
        }
    }
}