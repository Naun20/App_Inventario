package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilActivity extends AppCompatActivity {
     Button mButtonSalir;
    private TextView mTextViewNombre, mTextViewEmail, mTextViewTelefono, mTextViewUsuario, mTextViewContrasena, mTextViewTipo;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        mButtonSalir = findViewById(R.id.btn_Salir);
        mTextViewNombre = findViewById(R.id.lblnombre);
        mTextViewEmail = findViewById(R.id.lblemail);
        mTextViewTelefono = findViewById(R.id.lbltelefono);
        mTextViewUsuario = findViewById(R.id.lblusuario);
        mTextViewContrasena = findViewById(R.id.lblContrasena);
        mTextViewTipo = findViewById(R.id.lblAdministrador);

        mButtonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signOut();
                startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Llamar al método para obtener la información del usuario
        getUserInfo();
    }

    private void getUserInfo() {
        String id = mAuth.getCurrentUser().getUid();

        mFirestore.collection("usuario").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            String email = documentSnapshot.getString("email");
                            String telefono = documentSnapshot.getString("telefono");
                            String usuario = documentSnapshot.getString("usuario");
                            String password = documentSnapshot.getString("password");
                            String tipoUsuario = documentSnapshot.getString("tipo_usuario");

                            // Mostrar los datos en los TextView
                            mTextViewNombre.setText(nombre);
                            mTextViewEmail.setText(email);
                            mTextViewTelefono.setText(telefono);
                            mTextViewUsuario.setText(usuario);
                            mTextViewContrasena.setText(password); // Nota: No es recomendable mostrar la contraseña en un TextView
                            mTextViewTipo.setText(tipoUsuario);
                        } else {
                            Toast.makeText(PerfilActivity.this, "El usuario no existe en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PerfilActivity.this, "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}