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
import com.example.losmuchachossecurity.ui.usuario.MainActivityUsuario;
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

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView btnRegister; // ✅ CORREGIDO (antes Button)
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
        btnRegister = findViewById(R.id.btnRegister); // ✅ Sigue igual
        tvForgot = findViewById(R.id.tvForgot);
        cbRecordar = findViewById(R.id.cbRecordar);
        progress = findViewById(R.id.progress);
        cardLogin = findViewById(R.id.cardLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> doLogin());

        if (btnRegister != null) btnRegister.setOnClickListener(v -> doRegister());

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
                            showErrorMessage("Error al obtener datos del usuario");
                        }

                    } else {
                        setLoading(false);
                        shakeCard();
                        showErrorMessage(parseAuthError(task.getException()));
                    }
                });
    }

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
                            crearUsuarioEnFirestore(user.getUid(), email);
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

    private void verificarRolYRedirigir(String userId) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    setLoading(false);

                    if (documentSnapshot.exists()) {
                        String rol = documentSnapshot.getString("rol");

                        if (rol == null || rol.isEmpty()) {
                            rol = "usuario";
                            db.collection("usuarios").document(userId).update("rol", "usuario");
                        }

                        Intent intent;
                        if ("admin".equalsIgnoreCase(rol)) {
                            intent = new Intent(LoginActivity.this, MainActivityAdmin.class);
                            showSuccessMessage("¡Bienvenido Admin!");
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivityUsuario.class);
                            showSuccessMessage("¡Bienvenido!");
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            crearUsuarioEnFirestore(userId, currentUser.getEmail());
                        }
                    }

                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showErrorMessage("Error al verificar usuario: " + e.getMessage());
                });
    }

    private void crearUsuarioEnFirestore(String userId, String email) {
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("nombre", email.split("@")[0]);
        usuario.put("rol", "usuario");
        usuario.put("fechaRegistro", System.currentTimeMillis());
        usuario.put("activo", true);

        db.collection("usuarios").document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    setLoading(false);
                    showSuccessMessage("Cuenta creada exitosamente");

                    Intent intent = new Intent(LoginActivity.this, MainActivityUsuario.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showErrorMessage("Error al crear usuario: " + e.getMessage());
                });
    }

    private void doResetPassword() {
        String email = safe(etEmail.getText());

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingresa tu correo para enviar el enlace");
            etEmail.requestFocus();
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
                        showSuccessMessage("Revisa tu correo para restablecer tu contraseña");
                    } else {
                        showErrorMessage("No pudimos enviar el correo. Verifica tu email");
                    }
                });
    }

    private boolean validate(String email, String pass) {
        boolean ok = true;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("El correo es requerido");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Formato de correo inválido");
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
        if (btnRegister != null) btnRegister.setEnabled(!isLoading);
        tvForgot.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }

    private void shakeCard() {
        if (cardLogin != null) {
            cardLogin.animate()
                    .translationX(-10f)
                    .setDuration(50)
                    .withEndAction(() -> cardLogin.animate()
                            .translationX(10f)
                            .setDuration(50)
                            .withEndAction(() -> cardLogin.animate()
                                    .translationX(0f)
                                    .setDuration(50)
                                    .start())
                            .start())
                    .start();
        }
    }

    private String parseAuthError(Exception e) {
        if (e == null) return "Error desconocido";

        if (e instanceof FirebaseAuthInvalidUserException) {
            return "No existe una cuenta con este correo";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Credenciales incorrectas";
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            return "Ya existe una cuenta con este correo";
        } else if (e instanceof FirebaseAuthWeakPasswordException) {
            return "La contraseña es muy débil";
        } else if (e.getMessage() != null) {
            return e.getMessage();
        }

        return "Error de autenticación";
    }

    private void showSuccessMessage(String message) {
        if (cardLogin != null) {
            Snackbar.make(cardLogin, message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.success))
                    .setTextColor(getColor(R.color.white))
                    .show();
        }
    }

    private void showErrorMessage(String message) {
        if (cardLogin != null) {
            Snackbar.make(cardLogin, message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getColor(R.color.error))
                    .setTextColor(getColor(R.color.white))
                    .show();
        }
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
