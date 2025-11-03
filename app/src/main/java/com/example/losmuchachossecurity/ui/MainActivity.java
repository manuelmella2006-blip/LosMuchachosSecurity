package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.fragments.InicioFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Status bar verde
        getWindow().setStatusBarColor(getColor(R.color.ust_green_primary));
        getWindow().getDecorView().setSystemUiVisibility(0);

        mAuth = FirebaseAuth.getInstance();

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bottom Navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Cargar fragment inicial (InicioFragment como dashboard)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InicioFragment())
                    .commit();
        }
    }

    /**
     * Listener del Bottom Navigation
     * Ahora abre Activities en lugar de Fragments
     */
    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            // Inicio = Fragment (dashboard)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new InicioFragment())
                    .commit();
        } else if (itemId == R.id.nav_history) {
            // Historial = Activity
            startActivity(new Intent(MainActivity.this, HistorialActivity.class));
        } else if (itemId == R.id.nav_control) {
            // Control = Activity
            startActivity(new Intent(MainActivity.this, ControlActivity.class));
        } else if (itemId == R.id.nav_admin) {
            // Admin = Activity
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
        }

        return true;
    };

    /**
     * Crear menú con opción de logout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Manejar clic en logout
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Diálogo de confirmación para cerrar sesión
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> logout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Cerrar sesión y volver al login
     */
    private void logout() {
        mAuth.signOut();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("FROM_LOGOUT", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Prevenir cierre accidental
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Deseas salir de la aplicación?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    super.onBackPressed();
                    finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Cuando vuelve de una Activity, resetear Bottom Nav a "Inicio"
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Resetear selección al volver
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}