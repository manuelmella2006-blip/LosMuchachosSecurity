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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.losmuchachossecurity.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgot;
    private ProgressBar progress;
    private CardView cardLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔐 Forzar STATUS BAR VERDE + iconos blancos
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

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupListeners();
        animateCard();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgot = findViewById(R.id.tvForgot);
        progress = findViewById(R.id.progress);
        cardLogin = findViewById(R.id.cardLogin);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v -> doRegister());
        tvForgot.setOnClickListener(v -> doResetPassword());

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilEmail.setError(null);
        });
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) tilPassword.setError(null);
        });
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

        clearErrors();

        if (!validate(email, pass)) {
            shakeCard();
            return;
        }

        setLoading(true);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        showSuccessMessage("¡Bienvenido!");
                        goToHome();
                    } else {
                        shakeCard();
                        showErrorMessage(parseAuthError(task.getException()));
                    }
                });
    }

    private void doRegister() {
        String email = safe(etEmail.getText());
        String pass = safe(etPassword.getText());

        clearErrors();

        if (!validate(email, pass)) {
            shakeCard();
            return;
        }

        if (pass.length() < 6) {
            tilPassword.setError("La contraseña debe tener al menos 6 caracteres");
            shakeCard();
            return;
        }

        setLoading(true);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        showSuccessMessage("Cuenta creada exitosamente");
                    } else {
                        shakeCard();
                        showErrorMessage(parseAuthError(task.getException()));
                    }
                });
    }

    private void doResetPassword() {
        String email = safe(etEmail.getText());

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Ingresa tu correo para enviar el enlace");
            tilEmail.requestFocus();
            shakeCard();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo inválido");
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
            tilEmail.setError("El correo es requerido");
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Formato de correo inválido");
            ok = false;
        }

        if (TextUtils.isEmpty(pass)) {
            tilPassword.setError("La contraseña es requerida");
            ok = false;
        }

        return ok;
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
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
            Animation shake = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            cardLogin.startAnimation(shake);
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

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean fromLogout = getIntent().getBooleanExtra("FROM_LOGOUT", false);
        if (!fromLogout && mAuth.getCurrentUser() != null) {
            goToHome();
        }
    }
}
