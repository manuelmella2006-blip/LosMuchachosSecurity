package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfigAdminFragment extends Fragment {

    private FirebaseAuth mAuth;
    private UsuarioRepository usuarioRepository;

    private TextView tvNombreAdmin;
    private TextView tvEmailAdmin;
    private LinearLayout btnGestionarUsuarios;
    private LinearLayout btnConfiguracionSistema;
    private LinearLayout btnVerReportes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        usuarioRepository = new UsuarioRepository();

        initViews(view);
        cargarDatosUsuario();
        setupButtons();

        return view;
    }

    /**
     * 🎯 Inicializa todas las vistas del fragment
     */
    private void initViews(View view) {
        tvNombreAdmin = view.findViewById(R.id.tvNombreAdmin);
        tvEmailAdmin = view.findViewById(R.id.tvEmailAdmin);
        btnGestionarUsuarios = view.findViewById(R.id.btnGestionarUsuarios);
        btnConfiguracionSistema = view.findViewById(R.id.btnConfiguracionSistema);
        btnVerReportes = view.findViewById(R.id.btnVerReportes);
    }

    /**
     * 📝 Carga los datos del usuario admin desde Firestore
     */
    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvNombreAdmin.setText("Cargando...");
            tvEmailAdmin.setText(user.getEmail());

            usuarioRepository.obtenerUsuario(user.getUid(), usuario -> {
                if (usuario != null && isAdded()) {
                    tvNombreAdmin.setText(usuario.getNombre());
                    tvEmailAdmin.setText(usuario.getEmail());
                } else if (isAdded()) {
                    tvNombreAdmin.setText("Administrador");
                    tvEmailAdmin.setText(user.getEmail());
                }
            });
        } else {
            tvNombreAdmin.setText("No hay sesión activa");
            tvEmailAdmin.setText("-");
        }
    }

    /**
     * 🔘 Configura los listeners de todos los botones
     */
    private void setupButtons() {
        // Botón Gestionar Usuarios
        if (btnGestionarUsuarios != null) {
            btnGestionarUsuarios.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), GestionarUsuariosActivity.class);
                startActivity(intent);
            });
        }

        // Botón Configuración del Sistema
        if (btnConfiguracionSistema != null) {
            btnConfiguracionSistema.setOnClickListener(v -> {
                Toast.makeText(requireContext(),
                        "⚙️ Configuración del sistema - Próximamente",
                        Toast.LENGTH_SHORT).show();
            });
        }

        // Botón Ver Reportes
        if (btnVerReportes != null) {
            btnVerReportes.setOnClickListener(v -> {
                Toast.makeText(requireContext(),
                        "📊 Reportes - Próximamente",
                        Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar datos cuando el fragment vuelve a estar visible
        cargarDatosUsuario();
    }
}
