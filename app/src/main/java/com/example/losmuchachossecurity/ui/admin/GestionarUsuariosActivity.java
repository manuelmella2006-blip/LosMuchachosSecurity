package com.example.losmuchachossecurity.ui.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity para gestionar usuarios del sistema (CRUD completo)
 */
public class GestionarUsuariosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsuarios;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltrada;

    private EditText etBuscarUsuario;
    private ChipGroup chipGroupFiltros;
    private FloatingActionButton fabCrearUsuario;
    private LinearLayout layoutVacio;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_usuarios);

        // Configurar toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar vistas
        initViews();

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar búsqueda
        setupSearch();

        // Configurar filtros
        setupFilters();

        // Configurar FAB
        fabCrearUsuario.setOnClickListener(v -> mostrarDialogCrear());

        // Cargar usuarios (simulado - Naya conectará con Firebase)
        cargarUsuarios();
    }

    private void initViews() {
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        etBuscarUsuario = findViewById(R.id.etBuscarUsuario);
        chipGroupFiltros = findViewById(R.id.chipGroupFiltros);
        fabCrearUsuario = findViewById(R.id.fabCrearUsuario);
        layoutVacio = findViewById(R.id.layoutVacio);
    }

    private void setupRecyclerView() {
        listaUsuarios = new ArrayList<>();
        listaUsuariosFiltrada = new ArrayList<>();

        usuarioAdapter = new UsuarioAdapter(listaUsuariosFiltrada, this);
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsuarios.setAdapter(usuarioAdapter);

        // Callbacks del adapter
        usuarioAdapter.setOnEditarListener(this::mostrarDialogEditar);
        usuarioAdapter.setOnEliminarListener(this::confirmarEliminar);
    }

    private void setupSearch() {
        etBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarUsuarios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int selectedId = checkedIds.get(0);
                String filtro = "";

                if (selectedId == R.id.chipTodos) {
                    filtro = "todos";
                } else if (selectedId == R.id.chipAdmin) {
                    filtro = "admin";
                } else if (selectedId == R.id.chipUsuario) {
                    filtro = "usuario";
                }

                aplicarFiltroRol(filtro);
            }
        });
    }

    private void filtrarUsuarios(String texto) {
        listaUsuariosFiltrada.clear();

        if (texto.isEmpty()) {
            listaUsuariosFiltrada.addAll(listaUsuarios);
        } else {
            for (Usuario usuario : listaUsuarios) {
                if (usuario.getNombre().toLowerCase().contains(texto.toLowerCase()) ||
                        usuario.getEmail().toLowerCase().contains(texto.toLowerCase())) {
                    listaUsuariosFiltrada.add(usuario);
                }
            }
        }

        usuarioAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void aplicarFiltroRol(String rol) {
        listaUsuariosFiltrada.clear();

        if (rol.equals("todos")) {
            listaUsuariosFiltrada.addAll(listaUsuarios);
        } else {
            for (Usuario usuario : listaUsuarios) {
                if (usuario.getRol().equalsIgnoreCase(rol)) {
                    listaUsuariosFiltrada.add(usuario);
                }
            }
        }

        usuarioAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (listaUsuariosFiltrada.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            recyclerViewUsuarios.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            recyclerViewUsuarios.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogCrear() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_crear_usuario);
        dialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Obtener referencias
        TextInputEditText etNombre = dialog.findViewById(R.id.etNombreCrear);
        TextInputEditText etEmail = dialog.findViewById(R.id.etEmailCrear);
        TextInputEditText etPassword = dialog.findViewById(R.id.etPasswordCrear);
        TextInputEditText etTelefono = dialog.findViewById(R.id.etTelefonoCrear);
        RadioGroup radioGroupRol = dialog.findViewById(R.id.radioGroupRol);
        Button btnCancelar = dialog.findViewById(R.id.btnCancelarCrear);
        Button btnConfirmar = dialog.findViewById(R.id.btnConfirmarCrear);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnConfirmar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            int selectedRolId = radioGroupRol.getCheckedRadioButtonId();
            String rol = selectedRolId == R.id.radioAdmin ? "admin" : "usuario";

            // Validaciones básicas
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // AQUÍ NAYA CONECTARÁ CON FIREBASE
            // Por ahora solo simulamos creación
            Toast.makeText(this, "Usuario creado: " + nombre + " (" + rol + ")", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            // Recargar lista
            cargarUsuarios();
        });

        dialog.show();
    }

    private void mostrarDialogEditar(Usuario usuario) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_editar_usuario);
        dialog.getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // AQUÍ SE IMPLEMENTARÍA LA LÓGICA DE EDICIÓN
        // Naya conectará con Firebase

        Toast.makeText(this, "Editar: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
        dialog.show();
    }

    private void confirmarEliminar(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de eliminar a " + usuario.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    // AQUÍ NAYA CONECTARÁ CON FIREBASE
                    Toast.makeText(this, "Usuario eliminado: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                    cargarUsuarios();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Método simulado - Naya conectará con Firebase
     */
    private void cargarUsuarios() {
        listaUsuarios.clear();

        // Usuarios de ejemplo
        listaUsuarios.add(new Usuario("1", "Carlos Desarrollador", "carlos@santo.cl", "admin"));
        listaUsuarios.add(new Usuario("2", "Naya Firebase", "naya@santo.cl", "admin"));
        listaUsuarios.add(new Usuario("3", "Manu Arduino", "manu@santo.cl", "admin"));
        listaUsuarios.add(new Usuario("4", "Usuario Normal", "usuario@santo.cl", "usuario"));

        listaUsuariosFiltrada.clear();
        listaUsuariosFiltrada.addAll(listaUsuarios);
        usuarioAdapter.notifyDataSetChanged();
        updateEmptyState();
    }
}