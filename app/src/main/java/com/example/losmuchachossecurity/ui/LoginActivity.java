package com.example.losmuchachossecurity.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.losmuchachossecurity.R;
import com.example.losmuchachossecurity.ui.admin.MainActivityAdmin;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private TextView tvForgot;
    private CheckBox cbRecordar;
    private ProgressBar progress;
    private CardView cardLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupStatusBar();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();
        animateCard();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background_primary));

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
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgot = findViewById(R.id.tvForgot);
        cbRecordar = findViewById(R.id.cbRecordar);
        progress = findViewById(R.id.progress);
        cardLogin = findViewById(R.id.cardLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v -> doRegister());
        tvForgot.setOnClickListener(v -> doResetPassword());
    }

    private void animateCard() {
        if (cardLogin != null) {
            cardLogin.setAlpha(0f);
            cardLogin.setTranslationY(50f);
            cardLogin.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(200)
                    .start();
        }
    }

    // =============================
    // LOGIN
    // =============================
    private void doLogin() {
        String email = safe(etEmail.getText());
        String pass = safe(etPassword.getText());

        if (!validate(email, pass)) {
            shakeCard();
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verificarRolYRedirigir(user.getUid());
                        } else {
                            setLoading(false);
                            showErrorMessage("Error al obtener usuario");
                        }

                    } else {
                        setLoading(false);
                        shakeCard();
                        showErrorMessage(parseAuthError(task.getException()));
                    }
                });
    }

    // =============================
    // REGISTRO COMO ADMIN
    // =============================
    private void doRegister() {
        String email = safe(etEmail.getText());
        String pass = safe(etPassword.getText());

        if (!validate(email, pass)) {
            shakeCard();
            return;
        }

        if (pass.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            shakeCard();
            return;
        }

        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            crearUsuarioAdminEnFirestore(user.getUid(), email);
                        } else {
                            setLoading(false);
                            showErrorMessage("Error al crear usuario");
                        }

                    } else {
                        setLoading(false);
                        shakeCard();
                        showErrorMessage(parseAuthError(task.getException()));
                    }
                });
    }

    // =============================
    // SIEMPRE REDIRIGIR A ADMIN
    // =============================
    private void verificarRolYRedirigir(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    setLoading(false);

                    // Siempre admin
                    Intent intent = new Intent(LoginActivity.this, MainActivityAdmin.class);
                    showSuccessMessage("¡Bienvenido Administrador!");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showErrorMessage("Error verificando usuario: " + e.getMessage());
                });
    }

    private void crearUsuarioAdminEnFirestore(String userId, String email) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("nombre", email != null ? email.split("@")[0] : "Admin");
        usuario.put("rol", "admin");
        usuario.put("fechaRegistro", System.currentTimeMillis());
        usuario.put("activo", true);

        db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    showSuccessMessage("Cuenta de administrador creada");

                    Intent intent = new Intent(LoginActivity.this, MainActivityAdmin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showErrorMessage("Error creando admin: " + e.getMessage());
                });
    }

    // =============================
    // RESET CONTRASEÑA
    // =============================
    private void doResetPassword() {
        String email = safe(etEmail.getText());

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresa tu correo");
            shakeCard();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo inválido");
            shakeCard();
            return;
        }

        setLoading(true);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        showSuccessMessage("Correo enviado");
                    } else {
                        showErrorMessage("No se pudo enviar");
                    }
                });
    }

    // =============================
    // UTILIDADES
    // =============================
    private boolean validate(String email, String pass) {
        boolean ok = true;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("El correo es requerido");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo inválido");
            ok = false;
        }

        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("La contraseña es requerida");
            ok = false;
        }

        return ok;
    }

    private void setLoading(boolean isLoading) {
        progress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        btnRegister.setEnabled(!isLoading);
        tvForgot.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }

    private void shakeCard() {
        if (cardLogin != null) {
            cardLogin.animate()
                    .translationX(-10f)
                    .setDuration(50)
                    .withEndAction(() ->
                            cardLogin.animate()
                                    .translationX(10f)
                                    .setDuration(50)
                                    .withEndAction(() ->
                                            cardLogin.animate()
                                                    .translationX(0f)
                                                    .setDuration(50)
                                                    .start()
                                    ).start()
                    ).start();
        }
    }

    private String parseAuthError(Exception e) {
        if (e == null) return "Error";

        if (e instanceof FirebaseAuthInvalidUserException) return "Usuario no existe";
        if (e instanceof FirebaseAuthInvalidCredentialsException) return "Credenciales incorrectas";
        if (e instanceof FirebaseAuthUserCollisionException) return "Correo ya registrado";
        if (e instanceof FirebaseAuthWeakPasswordException) return "Contraseña débil";
        if (e.getMessage() != null) return e.getMessage();

        return "Error desconocido";
    }

    private void showSuccessMessage(String message) {
        Snackbar.make(cardLogin, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.success))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private void showErrorMessage(String message) {
        Snackbar.make(cardLogin, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private String safe(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean fromLogout = getIntent().getBooleanExtra("FROM_LOGOUT", false);

        if (!fromLogout && mAuth.getCurrentUser() != null) {
            verificarRolYRedirigir(mAuth.getCurrentUser().getUid());
        }
    }
}
