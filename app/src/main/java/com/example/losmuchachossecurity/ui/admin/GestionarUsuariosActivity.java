package com.example.losmuchachossecurity.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.data.UsuarioRepository;
import com.example.losmuchachossecurity.model.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GestionarUsuariosActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etBuscarUsuario;
    private ChipGroup chipGroupFiltros;
    private Chip chipTodos, chipAdmin, chipUsuario;
    private RecyclerView recyclerViewUsuarios;
    private FloatingActionButton fabCrearUsuario;
    private View layoutVacio;
    private ProgressBar progressBar;

    private UsuarioRepository usuarioRepository;
    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltrada;

    private String filtroActual = "todos"; // "todos", "admin", "usuario"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_usuarios);

        usuarioRepository = new UsuarioRepository();
        listaUsuarios = new ArrayList<>();
        listaUsuariosFiltrada = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearchBar();
        setupFiltros();
        setupFAB();

        cargarUsuarios();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etBuscarUsuario = findViewById(R.id.etBuscarUsuario);
        chipGroupFiltros = findViewById(R.id.chipGroupFiltros);
        chipTodos = findViewById(R.id.chipTodos);
        chipAdmin = findViewById(R.id.chipAdmin);
        chipUsuario = findViewById(R.id.chipUsuario);
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        fabCrearUsuario = findViewById(R.id.fabCrearUsuario);
        layoutVacio = findViewById(R.id.layoutVacio);

        // Si el ProgressBar no existe en el XML, no lanza error
        try {
            progressBar = findViewById(R.id.progressBar);
        } catch (Exception e) {
            progressBar = null;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        usuarioAdapter = new UsuarioAdapter(listaUsuariosFiltrada, new UsuarioAdapter.OnUsuarioClickListener() {
            @Override
            public void onEditarClick(Usuario usuario) {
                mostrarDialogoEditarUsuario(usuario);
            }

            @Override
            public void onEliminarClick(Usuario usuario) {
                confirmarEliminarUsuario(usuario);
            }

            @Override
            public void onCambiarRolClick(Usuario usuario) {
                cambiarRolUsuario(usuario);
            }
        });

        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void setupSearchBar() {
        etBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarUsuarios(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFiltros() {
        chipGroupFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipTodos.setChecked(true);
                return;
            }

            int selectedId = checkedIds.get(0);
            if (selectedId == R.id.chipTodos) filtroActual = "todos";
            else if (selectedId == R.id.chipAdmin) filtroActual = "admin";
            else if (selectedId == R.id.chipUsuario) filtroActual = "usuario";

            filtrarUsuarios(etBuscarUsuario.getText().toString());
        });
    }

    private void setupFAB() {
        fabCrearUsuario.setOnClickListener(v -> mostrarDialogoCrearUsuario());
    }

    /**
     * ✅ Diálogo para crear un nuevo usuario
     */
    private void mostrarDialogoCrearUsuario() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crear_usuario, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombre);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etTelefono = dialogView.findViewById(R.id.etTelefono);
        RadioGroup rgRol = dialogView.findViewById(R.id.rgRolNuevo);
        RadioButton rbUsuario = dialogView.findViewById(R.id.rbUsuarioNuevo);
        RadioButton rbAdmin = dialogView.findViewById(R.id.rbAdminNuevo);

        new AlertDialog.Builder(this)
                .setTitle("Crear Nuevo Usuario")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String telefono = etTelefono.getText().toString().trim();
                    String rol = rbAdmin.isChecked() ? "admin" : "usuario";

                    if (nombre.isEmpty() || email.isEmpty()) {
                        Toast.makeText(this, "Nombre y email son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Usuario nuevo = new Usuario(
                            usuarioRepository.db.collection("usuarios").document().getId(),
                            nombre, email, rol
                    );
                    nuevo.setTelefono(telefono);

                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

                    usuarioRepository.crearUsuario(nuevo, new UsuarioRepository.OnOperacionListener() {
                        @Override
                        public void onExito() {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            Toast.makeText(GestionarUsuariosActivity.this,
                                    "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                            cargarUsuarios();
                        }

                        @Override
                        public void onError(String mensaje) {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            Toast.makeText(GestionarUsuariosActivity.this,
                                    "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cargarUsuarios() {
        usuarioRepository.obtenerTodosLosUsuarios(new UsuarioRepository.OnUsuariosObtenidosListener() {
            @Override
            public void onObtenidos(List<Usuario> usuarios) {
                listaUsuarios.clear();
                listaUsuarios.addAll(usuarios);
                filtrarUsuarios(etBuscarUsuario.getText().toString());
                actualizarVistaVacia();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(GestionarUsuariosActivity.this,
                        "Error al cargar usuarios: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarUsuarios(String busqueda) {
        listaUsuariosFiltrada.clear();
        for (Usuario usuario : listaUsuarios) {
            boolean coincideBusqueda = busqueda.isEmpty() ||
                    usuario.getNombre().toLowerCase().contains(busqueda.toLowerCase()) ||
                    usuario.getEmail().toLowerCase().contains(busqueda.toLowerCase());
            boolean coincideFiltro = filtroActual.equals("todos") ||
                    usuario.getRol().equalsIgnoreCase(filtroActual);

            if (coincideBusqueda && coincideFiltro) {
                listaUsuariosFiltrada.add(usuario);
            }
        }

        usuarioAdapter.notifyDataSetChanged();
        actualizarVistaVacia();
    }

    private void actualizarVistaVacia() {
        if (listaUsuariosFiltrada.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            recyclerViewUsuarios.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            recyclerViewUsuarios.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoEditarUsuario(Usuario usuario) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_editar_usuario, null);

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreEditar);
        TextInputEditText etTelefono = dialogView.findViewById(R.id.etTelefonoEditar);
        RadioButton rbAdmin = dialogView.findViewById(R.id.rbAdminEditar);
        RadioButton rbUsuario = dialogView.findViewById(R.id.rbUsuarioEditar);
        RadioButton rbActivo = dialogView.findViewById(R.id.rbActivoEditar);
        RadioButton rbInactivo = dialogView.findViewById(R.id.rbInactivoEditar);

        etNombre.setText(usuario.getNombre());
        etTelefono.setText(usuario.getTelefono());
        if ("admin".equalsIgnoreCase(usuario.getRol())) rbAdmin.setChecked(true);
        else rbUsuario.setChecked(true);
        if (usuario.isActivo()) rbActivo.setChecked(true);
        else rbInactivo.setChecked(true);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        Button btnGuardar = dialogView.findViewById(R.id.btnGuardarEditar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarEditar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnGuardar.setOnClickListener(v -> {
            usuario.setNombre(etNombre.getText().toString().trim());
            usuario.setTelefono(etTelefono.getText().toString().trim());
            usuario.setRol(rbAdmin.isChecked() ? "admin" : "usuario");
            usuario.setActivo(rbActivo.isChecked());

            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            usuarioRepository.actualizarUsuario(usuario, new UsuarioRepository.UsuarioCallback() {
                @Override
                public void onSuccess(String mensaje) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(GestionarUsuariosActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarUsuarios();
                }

                @Override
                public void onError(String error) {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(GestionarUsuariosActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void cambiarRolUsuario(Usuario usuario) {
        String nuevoRol = usuario.isAdmin() ? "usuario" : "admin";
        String mensaje = usuario.isAdmin() ? "¿Cambiar a rol Usuario?" : "¿Cambiar a rol Administrador?";

        new AlertDialog.Builder(this)
                .setTitle("Cambiar Rol")
                .setMessage(mensaje + "\n\nUsuario: " + usuario.getNombre())
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    usuarioRepository.actualizarRolUsuario(
                            usuario.getUserId(),
                            nuevoRol,
                            new UsuarioRepository.UsuarioCallback() {
                                @Override
                                public void onSuccess(String msg) {
                                    Toast.makeText(GestionarUsuariosActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    cargarUsuarios();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(GestionarUsuariosActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarUsuario(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Eliminar a " + usuario.getNombre() + "?\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    usuarioRepository.eliminarUsuario(usuario.getUserId(), new UsuarioRepository.OnOperacionListener() {
                        @Override
                        public void onExito() {
                            Toast.makeText(GestionarUsuariosActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                            cargarUsuarios();
                        }

                        @Override
                        public void onError(String mensaje) {
                            Toast.makeText(GestionarUsuariosActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
