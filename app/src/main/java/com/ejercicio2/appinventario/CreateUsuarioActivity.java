package com.ejercicio2.appinventario;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CreateUsuarioActivity extends AppCompatActivity {
    private EditText nombreUser, emailUser, telefonoUser, usuarioUser, passwordUser;
    private Spinner spinnerTipoUsuario;
    private Button btn_addUser, btn_photo;
    private ImageView usuarioPhoto;
    private Button btn_eliminar_photo;
    private TextView nomRegis; // Actualiza el nombre de registrar y actualizar

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri selectedImageUri;
    private ArrayAdapter<String> tipoUsuarioAdapter;

    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isEditMode = false;

    private CheckBox checkBoxClientes, checkBoxTipoCliente, checkBoxUsuarios, checkBoxProveedores,
            checkBoxCategorias, checkBoxProductos, checkBoxCompras, checkBoxVentas, checkBoxnegocio,
            checkBoxreporteventa, checkBoxajuste, checkBoxreportecompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_usuario);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        checkBoxClientes = findViewById(R.id.checkBoxClientes);
        checkBoxTipoCliente = findViewById(R.id.checkBoxTipoCliente);
        checkBoxUsuarios = findViewById(R.id.checkBoxUsuarios);
        checkBoxProveedores = findViewById(R.id.checkBoxProveedores);
        checkBoxCategorias = findViewById(R.id.checkBoxCategorias);
        checkBoxProductos = findViewById(R.id.checkBoxProductos);
        checkBoxCompras = findViewById(R.id.checkBoxCompras);
        checkBoxVentas = findViewById(R.id.checkBoxVentas);
        checkBoxnegocio = findViewById(R.id.checkBoxNegocio);
        checkBoxreporteventa = findViewById(R.id.checkBoxreporteventa);
        checkBoxajuste = findViewById(R.id.checkBoxajuste);
        checkBoxreportecompra = findViewById(R.id.checkBoxreportecompra);


        nomRegis = findViewById(R.id.tvRegistroUser);
        nombreUser = findViewById(R.id.txtNombreUser);
        emailUser = findViewById(R.id.txtEmailUser);
        telefonoUser = findViewById(R.id.txtTelefonoUser);
        usuarioUser = findViewById(R.id.txtUsuarioUser);
        passwordUser = findViewById(R.id.txtContrasenauser);
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuariouser);
        btn_photo = findViewById(R.id.btn_photo);
        usuarioPhoto = findViewById(R.id.usuario_photo);
        btn_eliminar_photo = findViewById(R.id.btn_eliminarphoto);

        btn_addUser = findViewById(R.id.btn_addUser);
        String usuarioId = getIntent().getStringExtra("USUARIO_ID");
        if (usuarioId != null) {
            cargarDatosUsuario(usuarioId);
            isEditMode = true;
            nomRegis.setText("Actualizar Usuario");
            btn_addUser.setText("Actualizar");
        } else {
            btn_addUser.setText("Guardar");
            nomRegis.setText("Registrar Usuario");
        }

        String[] tiposUsuario = {"Seleccione el Tipo de Usuario:", "Administrador", "Regular"};
        tipoUsuarioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposUsuario);
        tipoUsuarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoUsuario.setAdapter(tipoUsuarioAdapter);

        btn_photo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btn_eliminar_photo.setOnClickListener(v -> {
            usuarioPhoto.setImageResource(R.drawable.agregar);
            selectedImageUri = null;
            Toast.makeText(CreateUsuarioActivity.this, "Foto eliminada. Selecciona otra imagen.", Toast.LENGTH_SHORT).show();
        });


        btn_addUser.setOnClickListener(v -> {
            String nombre = nombreUser.getText().toString().trim();
            String email = emailUser.getText().toString().trim();
            String telefono = telefonoUser.getText().toString().trim();
            String usuario = usuarioUser.getText().toString().trim();
            String password = passwordUser.getText().toString().trim();
            String tipoUsuarioSeleccionado = spinnerTipoUsuario.getSelectedItem().toString();

            Map<String, String> permisos = new HashMap<>();
            permisos.put("clientes", checkBoxClientes.isChecked() ? "activo" : "inactivo");
            permisos.put("tipoCliente", checkBoxTipoCliente.isChecked() ? "activo" : "inactivo");
            permisos.put("usuarios", checkBoxUsuarios.isChecked() ? "activo" : "inactivo");
            permisos.put("proveedores", checkBoxProveedores.isChecked() ? "activo" : "inactivo");
            permisos.put("categorias", checkBoxCategorias.isChecked() ? "activo" : "inactivo");
            permisos.put("productos", checkBoxProductos.isChecked() ? "activo" : "inactivo");
            permisos.put("compras", checkBoxCompras.isChecked() ? "activo" : "inactivo");
            permisos.put("ventas", checkBoxVentas.isChecked() ? "activo" : "inactivo");
            permisos.put("negocio", checkBoxnegocio.isChecked() ? "activo" : "inactivo");
            permisos.put("reporteventas", checkBoxreporteventa.isChecked() ? "activo" : "inactivo");
            permisos.put("ajuste", checkBoxajuste.isChecked() ? "activo" : "inactivo");
            permisos.put("reportecompras", checkBoxreportecompra.isChecked() ? "activo" : "inactivo");

            if (isEditMode) {
                if (usuarioId != null) {
                    actualizarUsuario(usuarioId, nombre, email, telefono, usuario, password, tipoUsuarioSeleccionado, selectedImageUri, permisos);
                }
            } else {
                if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(telefono)
                        || TextUtils.isEmpty(usuario) || TextUtils.isEmpty(password) || tipoUsuarioSeleccionado.equals("Seleccione el Tipo de Usuario:") || selectedImageUri == null) {
                    Toast.makeText(CreateUsuarioActivity.this, "Complete todos los campos y seleccione una imagen", Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario(nombre, email, telefono, usuario, password, tipoUsuarioSeleccionado, selectedImageUri, permisos);
                }
            }
        });
    }

    private void registrarUsuario(String nombre, String email, String telefono, String usuario, String password, String tipoUsuario, Uri selectedImageUri, Map<String, String> permisos) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    StorageReference imageRef = mStorageRef.child("images/" + userId + "_profile.jpg");
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();

                                    Map<String, Object> usuarioMap = new HashMap<>();
                                    usuarioMap.put("nombre", nombre);
                                    usuarioMap.put("email", email);
                                    usuarioMap.put("telefono", telefono);
                                    usuarioMap.put("usuario", usuario);
                                    usuarioMap.put("password", password);
                                    usuarioMap.put("tipo_usuario", tipoUsuario);
                                    usuarioMap.put("imagen_url", imageUrl);
                                    usuarioMap.put("permisos", permisos);

                                    mFirestore.collection("usuario").document(userId)
                                            .set(usuarioMap)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(CreateUsuarioActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(CreateUsuarioActivity.this, "Error al registrar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(CreateUsuarioActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateUsuarioActivity.this, "Error al registrar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarUsuario(String usuarioId, String nombre, String email, String telefono, String usuario, String password, String tipoUsuario, Uri selectedImageUri, Map<String, String> permisos) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d("ActualizarUsuario", "Usuario autenticado: " + firebaseUser.getUid());
            if (!TextUtils.isEmpty(email)) {
                firebaseUser.updateEmail(email)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("ActualizarUsuario", "Correo electrónico actualizado");
                            if (!TextUtils.isEmpty(password)) {
                                firebaseUser.updatePassword(password)
                                        .addOnSuccessListener(aVoid1 -> {
                                            Log.d("ActualizarUsuario", "Contraseña actualizada");
                                            actualizarDatosFirestore(usuarioId, nombre, email, telefono, usuario, password, tipoUsuario, selectedImageUri, permisos);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("ActualizarUsuario", "Error al actualizar la contraseña: " + e.getMessage());
                                            Toast.makeText(CreateUsuarioActivity.this, "Error al actualizar la contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                actualizarDatosFirestore(usuarioId, nombre, email, telefono, usuario, password, tipoUsuario, selectedImageUri, permisos);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ActualizarUsuario", "Error al actualizar el correo electrónico: " + e.getMessage());
                            Toast.makeText(CreateUsuarioActivity.this, "Error al actualizar el correo electrónico: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                if (!TextUtils.isEmpty(password)) {
                    firebaseUser.updatePassword(password)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ActualizarUsuario", "Contraseña actualizada");
                                actualizarDatosFirestore(usuarioId, nombre, email, telefono, usuario, password, tipoUsuario, selectedImageUri, permisos);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ActualizarUsuario", "Error al actualizar la contraseña: " + e.getMessage());
                                Toast.makeText(CreateUsuarioActivity.this, "Error al actualizar la contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    actualizarDatosFirestore(usuarioId, nombre, email, telefono, usuario, password, tipoUsuario, selectedImageUri, permisos);
                }
            }
        } else {
            Log.e("ActualizarUsuario", "Usuario no autenticado");
            Toast.makeText(CreateUsuarioActivity.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarDatosFirestore(String usuarioId, String nombre, String email, String telefono, String usuario, String password, String tipoUsuario, Uri selectedImageUri, Map<String, String> permisos) {
        if (selectedImageUri != null) {
            // Se seleccionó una nueva imagen, por lo que se debe subir y actualizar la URL de la imagen
            StorageReference imageRef = mStorageRef.child("images/" + usuarioId + "_profile.jpg");
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            Log.d("ActualizarDatosFirestore", "Imagen subida: " + imageUrl);

                            // Crear un mapa de actualización con la nueva URL de imagen
                            Map<String, Object> usuarioMap = new HashMap<>();
                            usuarioMap.put("nombre", nombre);
                            usuarioMap.put("email", email);
                            usuarioMap.put("telefono", telefono);
                            usuarioMap.put("usuario", usuario);
                            usuarioMap.put("password", password);
                            usuarioMap.put("tipo_usuario", tipoUsuario);
                            usuarioMap.put("imagen_url", imageUrl);

                            // Actualizar permisos aquí
                            for (Map.Entry<String, String> entry : permisos.entrySet()) {
                                usuarioMap.put("permisos." + entry.getKey(), entry.getValue());
                            }

                            // Actualizar los datos del usuario en Firestore
                            mFirestore.collection("usuario").document(usuarioId)
                                    .update(usuarioMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("ActualizarDatosFirestore", "Datos actualizados en Firestore");
                                        Toast.makeText(CreateUsuarioActivity.this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ActualizarDatosFirestore", "Error al actualizar el usuario: " + e.getMessage());
                                        Toast.makeText(CreateUsuarioActivity.this, "Error al actualizar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ActualizarDatosFirestore", "Error al subir la imagen: " + e.getMessage());
                        Toast.makeText(CreateUsuarioActivity.this, "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // No se seleccionó una nueva imagen, solo actualizar otros datos
            Map<String, Object> usuarioMap = new HashMap<>();
            usuarioMap.put("nombre", nombre);
            usuarioMap.put("email", email);
            usuarioMap.put("telefono", telefono);
            usuarioMap.put("usuario", usuario);
            usuarioMap.put("password", password);
            usuarioMap.put("tipo_usuario", tipoUsuario);

            // Actualizar permisos aquí
            for (Map.Entry<String, String> entry : permisos.entrySet()) {
                usuarioMap.put("permisos." + entry.getKey(), entry.getValue());
            }

            // Actualizar los datos del usuario en Firestore
            mFirestore.collection("usuario").document(usuarioId)
                    .update(usuarioMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ActualizarDatosFirestore", "Datos actualizados en Firestore");
                        Toast.makeText(CreateUsuarioActivity.this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ActualizarDatosFirestore", "Error al actualizar el usuario: " + e.getMessage());
                        Toast.makeText(CreateUsuarioActivity.this, "Error al actualizar el usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

        private void cargarDatosUsuario(String usuarioId) {
        mFirestore.collection("usuario").document(usuarioId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String email = documentSnapshot.getString("email");
                        String telefono = documentSnapshot.getString("telefono");
                        String usuario = documentSnapshot.getString("usuario");
                        String password = documentSnapshot.getString("password"); // Asegúrate de que la contraseña está almacenada
                        String tipoUsuario = documentSnapshot.getString("tipo_usuario");
                        String imagenUrl = documentSnapshot.getString("imagen_url");
                        Map<String, String> permisos = (Map<String, String>) documentSnapshot.get("permisos");

                        nombreUser.setText(nombre);
                        emailUser.setText(email);
                        telefonoUser.setText(telefono);
                        usuarioUser.setText(usuario);
                        passwordUser.setText(password); // Mostrar la contraseña actual

                        if (tipoUsuarioAdapter != null) {
                            int spinnerPosition = tipoUsuarioAdapter.getPosition(tipoUsuario);
                            if (spinnerPosition >= 0) {
                                spinnerTipoUsuario.setSelection(spinnerPosition);
                            }
                        }

                        if (imagenUrl != null && !imagenUrl.isEmpty()) {
                            Glide.with(this).load(imagenUrl).into(usuarioPhoto);
                        } else {
                            usuarioPhoto.setImageResource(R.drawable.agregar);
                        }

                        if (permisos != null) {
                            checkBoxClientes.setChecked("activo".equals(permisos.get("clientes")));
                            checkBoxTipoCliente.setChecked("activo".equals(permisos.get("tipoCliente")));
                            checkBoxUsuarios.setChecked("activo".equals(permisos.get("usuarios")));
                            checkBoxProveedores.setChecked("activo".equals(permisos.get("proveedores")));
                            checkBoxCategorias.setChecked("activo".equals(permisos.get("categorias")));
                            checkBoxProductos.setChecked("activo".equals(permisos.get("productos")));
                            checkBoxCompras.setChecked("activo".equals(permisos.get("compras")));
                            checkBoxVentas.setChecked("activo".equals(permisos.get("ventas")));
                            checkBoxnegocio.setChecked("activo".equals(permisos.get("negocio")));
                            checkBoxreporteventa.setChecked("activo".equals(permisos.get("reporteventas")));
                            checkBoxajuste.setChecked("activo".equals(permisos.get("ajuste")));
                            checkBoxreportecompra.setChecked("activo".equals(permisos.get("reportecompras")));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateUsuarioActivity.this, "Error al cargar los datos del usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            usuarioPhoto.setImageURI(selectedImageUri);
        }
    }
}
