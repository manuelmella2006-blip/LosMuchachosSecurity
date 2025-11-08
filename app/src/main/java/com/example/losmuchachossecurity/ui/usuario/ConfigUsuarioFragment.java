package com.example.losmuchachossecurity.ui.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfigUsuarioFragment extends Fragment {

    private TextView tvNombre, tvEmail;
    private Button btnEditarPerfil, btnCerrarSesion;
    private CardView cardMisReservas; // ✅ reemplazo correcto
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_usuario, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        tvNombre = view.findViewById(R.id.tvNombre);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        cardMisReservas = view.findViewById(R.id.cardMisReservas); // ✅ reemplazado
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Configurar botones
        btnEditarPerfil.setOnClickListener(v -> {
            // Abrir activity para editar perfil
        });

        cardMisReservas.setOnClickListener(v -> {
            // Mostrar historial de reservas
            // Por ejemplo:
            // Intent intent = new Intent(getActivity(), HistorialReservasActivity.class);
            // startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        return view;
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvNombre.setText(user.getDisplayName() != null ? user.getDisplayName() : "Usuario");
            tvEmail.setText(user.getEmail());
        }
    }

    private void cerrarSesion() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
