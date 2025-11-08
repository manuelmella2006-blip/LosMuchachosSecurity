package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfigAdminFragment extends Fragment {

    private TextView tvNombreAdmin, tvEmailAdmin;
    private Button btnGestionarUsuarios, btnConfiguracionSistema, btnCerrarSesion;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_admin, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        tvNombreAdmin = view.findViewById(R.id.tvNombreAdmin);
        tvEmailAdmin = view.findViewById(R.id.tvEmailAdmin);
        btnGestionarUsuarios = view.findViewById(R.id.btnGestionarUsuarios);
        btnConfiguracionSistema = view.findViewById(R.id.btnConfiguracionSistema);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesionAdmin);

        // Cargar datos del admin
        cargarDatosAdmin();

        // Configurar botones
        btnGestionarUsuarios.setOnClickListener(v -> {
            // Abrir activity para gestionar usuarios
            Toast.makeText(getContext(), "Gestionar usuarios", Toast.LENGTH_SHORT).show();
        });

        btnConfiguracionSistema.setOnClickListener(v -> {
            // Abrir configuración del sistema
            Toast.makeText(getContext(), "Configuración del sistema", Toast.LENGTH_SHORT).show();
        });

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        return view;
    }

    private void cargarDatosAdmin() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvNombreAdmin.setText(user.getDisplayName() != null ? user.getDisplayName() : "Administrador");
            tvEmailAdmin.setText(user.getEmail());
        }
    }

    private void cerrarSesion() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}