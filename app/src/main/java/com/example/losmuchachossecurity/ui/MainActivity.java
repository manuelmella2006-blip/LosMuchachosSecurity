package com.example.losmuchachossecurity.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.fragments.AdminFragment;
import com.example.losmuchachossecurity.ui.fragments.ControlFragment;
import com.example.losmuchachossecurity.ui.fragments.HistorialFragment;
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
        setContentView(R.layout.activity_main); // activity_main contiene @id/fragment_container

        // Status bar (verde Santo Tomás)
        getWindow().setStatusBarColor(getColor(R.color.ust_green_primary));
        getWindow().getDecorView().setSystemUiVisibility(0);

        mAuth = FirebaseAuth.getInstance();

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bottom Navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new InicioFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistorialFragment();
            } else if (itemId == R.id.nav_control) {
                selectedFragment = new ControlFragment();
            } else if (itemId == R.id.nav_admin) {
                selectedFragment = new AdminFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Cargar InicioFragment por defecto al iniciar
        if (savedInstanceState == null) {
            loadFragment(new InicioFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * Método para cargar Fragments en el contenedor
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro que deseas cerrar sesión?")
                    .setPositiveButton("Sí", (d, w) -> {
                        mAuth.signOut();
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
}
