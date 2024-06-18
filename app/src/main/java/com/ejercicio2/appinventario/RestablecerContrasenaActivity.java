package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestablecerContrasenaActivity extends AppCompatActivity {

    private EditText mEdiTextEmail;
    private Button mButonRestablecer;
    private TextView Regresar;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restablecer_contrasena);

        mAuth = FirebaseAuth.getInstance(); // Inicializar Firebase Auth

        mEdiTextEmail = findViewById(R.id.txtEmail);
        mButonRestablecer = findViewById(R.id.btn_Restablecer);
        Regresar = findViewById(R.id.lblRegresar);
        progressBar = findViewById(R.id.progressBar);



        mButonRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEdiTextEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    resetPassword(email);
                } else {
                    Toast.makeText(RestablecerContrasenaActivity.this, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestablecerContrasenaActivity.this, LoginActivity.class));
                finish(); // Terminar la actividad actual
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.setLanguageCode("es"); // Opcional: Establecer el idioma del correo de restablecimiento
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(RestablecerContrasenaActivity.this, "Se ha enviado un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RestablecerContrasenaActivity.this, "No se pudo enviar el correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}