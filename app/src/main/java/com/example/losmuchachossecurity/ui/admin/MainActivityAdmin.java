package com.example.losmuchachossecurity.ui.admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityAdmin extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        // 🔐 Configurar Status Bar verde
        setupStatusBar();

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupToolbar();
        setupBottomNavigation();

        // Cargar fragment inicial (Monitoreo)
        if (savedInstanceState == null) {
            loadFragment(new MonitoreoFragment());
        }
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.ust_green_primary));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        } else {
            window.getDecorView().setSystemUiVisibility(0);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin - Los Muchachos Security");
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_monitoreo) {
                selectedFragment = new MonitoreoFragment();
                updateToolbarTitle("Monitoreo");
            } else if (itemId == R.id.nav_barreras) {
                selectedFragment = new BarrerasFragment();
                updateToolbarTitle("Control de Barreras");
            } else if (itemId == R.id.nav_registros) {
                selectedFragment = new RegistrosFragment();
                updateToolbarTitle("Registros");
            } else if (itemId == R.id.nav_config_admin) {
                selectedFragment = new ConfigAdminFragment();
                updateToolbarTitle("Configuración Admin");
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin - " + title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_notificaciones) {
            mostrarNotificaciones();
            return true;
        } else if (itemId == R.id.action_estadisticas) {
            mostrarEstadisticas();
            return true;
        } else if (itemId == R.id.action_logout) {
            mostrarDialogoCerrarSesion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 🔔 Muestra las notificaciones del sistema
     */
    private void mostrarNotificaciones() {
        new AlertDialog.Builder(this)
                .setTitle("Notificaciones")
                .setMessage("• Plaza #12 liberada\n• Nueva reserva: Usuario 'Juan Pérez'\n• Barrera entrada: Activada correctamente")
                .setPositiveButton("Entendido", null)
                .show();
    }

    /**
     * 📊 Muestra estadísticas rápidas del sistema
     */
    private void mostrarEstadisticas() {
        new AlertDialog.Builder(this)
                .setTitle("Estadísticas del Sistema")
                .setMessage(
                        "📊 Resumen del día:\n\n" +
                                "• Plazas ocupadas: 45/100\n" +
                                "• Reservas activas: 23\n" +
                                "• Ingresos hoy: 67\n" +
                                "• Salidas hoy: 42\n" +
                                "• Tiempo promedio: 2.5 hrs"
                )
                .setPositiveButton("Ver más", null)
                .setNegativeButton("Cerrar", null)
                .show();
    }

    /**
     * 🚪 Muestra un diálogo de confirmación para cerrar sesión
     */
    private void mostrarDialogoCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión como administrador?")
                .setPositiveButton("Sí, salir", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * 🔓 Cierra la sesión del administrador y vuelve al LoginActivity
     */
    private void cerrarSesion() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("FROM_LOGOUT", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Si está en el fragment de monitoreo, mostrar diálogo de salida
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof MonitoreoFragment) {
            new AlertDialog.Builder(this)
                    .setTitle("Salir")
                    .setMessage("¿Deseas salir del panel de administración?")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            // Volver al fragment de monitoreo
            bottomNavigation.setSelectedItemId(R.id.nav_monitoreo);
        }
    }
}