package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    Button btn_register;
    EditText nombre, email, telefono, usuario, password;
    Spinner spinnerTipoUsuario;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    TextView mTextViewRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de Firebase
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre = findViewById(R.id.txtNombreUsu);
        email = findViewById(R.id.txtEmailUsu);
        telefono = findViewById(R.id.txtTelefonoUsu);
        usuario = findViewById(R.id.txtUsuario);
        password = findViewById(R.id.txtContrasena);
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUser);

        btn_register = findViewById(R.id.btn_Registrar);
        mTextViewRegresar = (TextView)findViewById(R.id.lblVolver);

        mTextViewRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar el ProgressBar
               // pro.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        String[] tipoUsuario = {"Seleccione el Tipo de Usuario:", "Administrador", "Usuario Regular"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipoUsuario);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoUsuario.setAdapter(adapter);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreUser = nombre.getText().toString().trim();
                String emailUser = email.getText().toString().trim();
                String telefonoUser = telefono.getText().toString().trim();
                String usuarioUser = usuario.getText().toString().trim();
                String passUser = password.getText().toString().trim();
                String tipoUser = spinnerTipoUsuario.getSelectedItem().toString();

                // Validar campos obligatorios
                if (nombreUser.isEmpty() || emailUser.isEmpty() || telefonoUser.isEmpty()
                        || usuarioUser.isEmpty() || passUser.isEmpty() || tipoUser.equals("Seleccione el Tipo de Usuario:")) {
                    Toast.makeText(RegistroActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(nombreUser, emailUser, telefonoUser, usuarioUser, passUser, tipoUser);
                }
            }
        });
    }

    private void registerUser(String nombreUser, String emailUser, String telefonoUser, String usuarioUser, String passUser, String tipoUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("id", userId);
                            userData.put("nombre", nombreUser);
                            userData.put("email", emailUser);
                            userData.put("telefono", telefonoUser);
                            userData.put("usuario", usuarioUser);
                            userData.put("password", passUser);
                            userData.put("tipo_usuario", tipoUser);

                            // Save user data to Firestore
                            mFirestore.collection("usuario").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

                                            // After saving user data, navigate to another activity if needed
                                            // startActivity(new Intent(RegistroActivity.this, SomeOtherActivity.class));
                                            finish(); // Finish RegistroActivity after successful registration
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistroActivity.this, "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error al registrar usuario: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
