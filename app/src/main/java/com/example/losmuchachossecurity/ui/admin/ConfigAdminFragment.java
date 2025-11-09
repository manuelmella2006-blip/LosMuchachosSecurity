package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Fragment de Configuración del Administrador
 * Incluye gestión de usuarios, configuración del sistema y cierre de sesión
 */
public class ConfigAdminFragment extends Fragment {

    private TextView tvNombreAdmin, tvEmailAdmin;
    private LinearLayout btnGestionarUsuarios, btnConfiguracionSistema, btnVerReportes;
    private Button btnCerrarSesionAdmin;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_admin, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        initViews(view);

        // Cargar datos del administrador
        cargarDatosAdmin();

        // Configurar botones
        setupButtons();

        return view;
    }

    /**
     * Inicializa todas las vistas del fragment
     */
    private void initViews(View view) {
        tvNombreAdmin = view.findViewById(R.id.tvNombreAdmin);
        tvEmailAdmin = view.findViewById(R.id.tvEmailAdmin);
        btnGestionarUsuarios = view.findViewById(R.id.btnGestionarUsuarios);
        btnConfiguracionSistema = view.findViewById(R.id.btnConfiguracionSistema);
        btnVerReportes = view.findViewById(R.id.btnVerReportes);
        btnCerrarSesionAdmin = view.findViewById(R.id.btnCerrarSesionAdmin);
    }

    /**
     * Configura los listeners de los botones
     */
    private void setupButtons() {
        // Gestionar Usuarios
        btnGestionarUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GestionarUsuariosActivity.class);
            startActivity(intent);
        });

        // Configuración del Sistema
        btnConfiguracionSistema.setOnClickListener(v -> {
            // TODO: Implementar pantalla de configuración del sistema
            Toast.makeText(getContext(),
                    "Configuración del Sistema\n(Próximamente)",
                    Toast.LENGTH_SHORT).show();
        });

        // Ver Reportes
        btnVerReportes.setOnClickListener(v -> {
            // TODO: Implementar pantalla de reportes
            Toast.makeText(getContext(),
                    "Ver Reportes\n(Próximamente)",
                    Toast.LENGTH_SHORT).show();
        });

        // Cerrar Sesión
        btnCerrarSesionAdmin.setOnClickListener(v -> mostrarDialogCerrarSesion());
    }

    /**
     * Carga los datos del administrador desde Firebase Authentication
     */
    private void cargarDatosAdmin() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Obtener nombre (si existe displayName, sino usar email)
            String nombre = user.getDisplayName();
            if (nombre == null || nombre.isEmpty()) {
                // Si no hay displayName, usar parte del email
                String email = user.getEmail();
                if (email != null) {
                    nombre = email.split("@")[0];
                    nombre = capitalize(nombre);
                } else {
                    nombre = "Administrador";
                }
            }

            tvNombreAdmin.setText(nombre);
            tvEmailAdmin.setText(user.getEmail() != null ? user.getEmail() : "No disponible");
        } else {
            tvNombreAdmin.setText("Administrador");
            tvEmailAdmin.setText("No disponible");
        }
    }

    /**
     * Muestra un dialog de confirmación para cerrar sesión
     */
    private void mostrarDialogCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí, cerrar sesión", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Cierra la sesión del usuario y redirige al Login
     */
    private void cerrarSesion() {
        // Cerrar sesión en Firebase
        mAuth.signOut();

        // Mostrar mensaje
        Toast.makeText(getContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // Redirigir al Login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finalizar actividad actual
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Capitaliza la primera letra de un texto
     */
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar datos al volver al fragment
        cargarDatosAdmin();
    }
}