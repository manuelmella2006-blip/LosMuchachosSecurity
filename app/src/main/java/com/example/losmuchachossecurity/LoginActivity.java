package com.example.losmuchachossecurity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvForgot;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgot = findViewById(R.id.tvForgot);
        progress = findViewById(R.id.progress);

        btnLogin.setOnClickListener(v -> doLogin());
        btnRegister.setOnClickListener(v -> doRegister());
        tvForgot.setOnClickListener(v -> doResetPassword());
    }

    private void doLogin() {
        String email = safe(etEmail.getText());
        String pass  = safe(etPassword.getText());

        clearErrors();
        if (!validate(email, pass)) return;

        setLoading(true);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        // SIN verificación de correo: entra directo al Main
                        goToHome();

                        // SI QUIERES EXIGIR VERIFICACIÓN, DESCOMENTA ESTO:
                    /*
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.reload().addOnCompleteListener(r -> {
                            if (user.isEmailVerified()) {
                                goToHome();
                            } else {
                                toast("Verifica tu correo antes de ingresar");
                            }
                        });
                    }
                    */
                    } else {
                        toast(errorMsg(task.getException()));
                    }
                });
    }

    private void doRegister() {
        String email = safe(etEmail.getText());
        String pass  = safe(etPassword.getText());

        clearErrors();
        if (!validate(email, pass)) return;

        setLoading(true);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        toast("Cuenta creada");
                        // Opcional: enviar verificación si la vas a exigir más tarde
                        // FirebaseUser u = mAuth.getCurrentUser();
                        // if (u != null) u.sendEmailVerification();
                    } else {
                        toast(errorMsg(task.getException()));
                    }
                });
    }

    private void doResetPassword() {
        String email = safe(etEmail.getText());
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Ingresa tu correo para enviar el enlace");
            return;
        }
        setLoading(true);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    toast(task.isSuccessful()
                            ? "Enviamos un enlace para restablecer tu contraseña"
                            : errorMsg(task.getException()));
                });
    }

    private boolean validate(String email, String pass) {
        boolean ok = true;
        if (TextUtils.isEmpty(email)) { tilEmail.setError("Correo requerido"); ok = false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Correo inválido"); ok = false; }
        if (TextUtils.isEmpty(pass)) { tilPassword.setError("Contraseña requerida"); ok = false; }
        return ok;
    }

    private void clearErrors() { tilEmail.setError(null); tilPassword.setError(null); }

    private void setLoading(boolean b) {
        progress.setVisibility(b ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!b); btnRegister.setEnabled(!b); tvForgot.setEnabled(!b);
    }

    private String errorMsg(Exception e) {
        return e != null && e.getMessage() != null ? e.getMessage() : "Error";
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }

    private String safe(CharSequence cs) { return cs == null ? "" : cs.toString().trim(); }

    private void goToHome() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}

