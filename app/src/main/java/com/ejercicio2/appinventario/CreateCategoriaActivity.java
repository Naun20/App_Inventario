package com.ejercicio2.appinventario;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateCategoriaActivity extends AppCompatActivity {
    private Button btnAgreFoto,btnEliminarFoto,btnAddCategoria;
    private EditText nombreCatego, descripCateg, fechaEditText;
    private RadioButton radioButtonActivo, radioButtonNoActivo;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri selectedImageUri;
    private ImageView categoriaPhoto;

    private static final int PICK_IMAGE_REQUEST = 1;
    private SimpleDateFormat dateFormatter;
    private boolean isEditMode = false;
    private TextView nomCategoria; //Actualiza el nombre de registrar y actualizar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_categoria);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        categoriaPhoto = findViewById(R.id.usuario_photoCateg);

        nombreCatego = findViewById(R.id.NomCateg);
        descripCateg = findViewById(R.id.DescripCateg);
        radioButtonActivo = findViewById(R.id.radioButtonActivo);
        radioButtonNoActivo = findViewById(R.id.radioButtonNoActivo);
        fechaEditText = findViewById(R.id.FechaCreaCateg);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        mostrarFechaActual();
        fechaEditText.setFocusable(false);
        fechaEditText.setClickable(false);

        //Actualizar
        nomCategoria = findViewById(R.id.tvcategoria);

         btnAgreFoto = findViewById(R.id.btn_photoCateg);
         btnEliminarFoto = findViewById(R.id.btn_eliminarphotoCateg);
         btnAddCategoria = findViewById(R.id.btn_addCategoria);
        // Obtener el ID del cliente si se proporcionó
        String categoriaId = getIntent().getStringExtra("CATEGORIAS_ID");
        if (categoriaId != null) {
            // Modo de edición: cargar los datos del cliente existente
            cargarDatosCategoria(categoriaId);
            // Establecer el modo de edición
            isEditMode = true;
            // Cambiar el texto del botón a "Actualizar"
            nomCategoria.setText("Actualizar Categoria");
            btnAddCategoria.setText("Actualizar");
        } else {
            // Modo de creación: texto predeterminado del botón es "Guardar"
            btnAddCategoria.setText("Guardar");
            nomCategoria.setText("Registrar Categoria");
        }

        btnAgreFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        btnEliminarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Borrar la foto seleccionada
                categoriaPhoto.setImageResource(R.drawable.caja); // Mostrar la imagen por defecto o una imagen de "Agregar" en el ImageView

                // Reiniciar la URI de la imagen seleccionada
                selectedImageUri = null;

                Toast.makeText(CreateCategoriaActivity.this, "Foto eliminada. Selecciona otra imagen.", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomcateg = nombreCatego.getText().toString().trim();
                String descripcateg = descripCateg.getText().toString().trim();
                String fechacreacioncateg = fechaEditText.getText().toString().trim();

                boolean isActivo = radioButtonActivo.isChecked();

                if (nomcateg.isEmpty() || descripcateg.isEmpty() || fechacreacioncateg.isEmpty()) {
                    Toast.makeText(CreateCategoriaActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String estado = isActivo ? "Activo" : "No Activo";

                // Verificar si estamos en modo de edición
                String categoriaId = getIntent().getStringExtra("CATEGORIAS_ID");
                if (categoriaId != null) {
                    // Estamos editando una categoría existente
                    actualizarCategoria(categoriaId, nomcateg, descripcateg, fechacreacioncateg, estado, selectedImageUri);
                } else {
                    // Estamos agregando una nueva categoría
                    guardarCategoriaEnFirestore(nomcateg, descripcateg, fechacreacioncateg, estado, selectedImageUri);
                }
            }
        });


        fechaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void mostrarFechaActual() {
        // Obtener la fecha actual en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date()); // Obtener la fecha actual

        // Establecer la fecha actual en el EditText de fecha
        fechaEditText.setText(fechaActual);
    }

    private void actualizarCategoria(String categoriaId, String nombre, String descripcion, String fechaCreacion, String estado, Uri selectedImageUri) {
        if (TextUtils.isEmpty(categoriaId)) {
            Toast.makeText(CreateCategoriaActivity.this, "ID de categoría no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference categoriaRef = db.collection("categorias").document(categoriaId);

        // Crear un mapa con los datos actualizados de la categoría
        Map<String, Object> categoriaMap = new HashMap<>();
        categoriaMap.put("nombre", nombre);
        categoriaMap.put("descripcion", descripcion);
        categoriaMap.put("fecha_creacion", fechaCreacion);
        categoriaMap.put("estado", estado);

        // Verificar si se ha seleccionado una nueva imagen
        if (selectedImageUri != null) {
            // Obtener referencia a la ubicación de almacenamiento de la nueva imagen
            String imageFileName = nombre + "_imagen.jpg";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("imagenes_categorias")
                    .child(imageFileName);

            // Subir la nueva imagen a Firebase Storage y obtener la URL de descarga
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de descarga de la imagen subida
                        storageRef.getDownloadUrl().addOnSuccessListener(imageUrl -> {
                            // Agregar la URL de la nueva imagen al mapa de datos de la categoría
                            categoriaMap.put("imagen_url", imageUrl.toString());

                            // Actualizar la categoría en Firestore con los datos actualizados incluyendo la nueva imagen
                            categoriaRef.update(categoriaMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CreateCategoriaActivity.this, "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show();
                                        finish(); // Cerrar la actividad después de actualizar la categoría
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CreateCategoriaActivity.this, "Error al actualizar la categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateCategoriaActivity.this, "Error al subir nueva imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // No se seleccionó una nueva imagen, actualizar solo los datos de la categoría sin cambiar la imagen
            categoriaRef.update(categoriaMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CreateCategoriaActivity.this, "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad después de actualizar la categoría
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateCategoriaActivity.this, "Error al actualizar la categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    ///Cargar los datos alos editext
    private void cargarDatosCategoria(String categoriaId) {
        // Obtener los datos de la categoría desde Firestore usando categoriaId
        mFirestore.collection("categorias").document(categoriaId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos de la categoría
                            Map<String, Object> categoriaData = documentSnapshot.getData();

                            if (categoriaData != null) {
                                // Establecer los valores en los EditText
                                nombreCatego.setText((String) categoriaData.get("nombre"));
                                descripCateg.setText((String) categoriaData.get("descripcion"));
                                fechaEditText.setText((String) categoriaData.get("fecha_creacion"));

                                // Obtener y establecer el estado de la categoría
                                String estado = (String) categoriaData.get("estado");
                                if (estado != null && estado.equals("Activo")) {
                                    radioButtonActivo.setChecked(true);
                                } else {
                                    radioButtonNoActivo.setChecked(true); // Marcar como inactivo si no está activo
                                }

                                // Obtener la URL de la imagen de la categoría
                                String imageUrl = (String) categoriaData.get("imagen_url");

                                // Cargar la imagen utilizando Glide si imageUrl no está vacío
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(CreateCategoriaActivity.this)
                                            .load(imageUrl)
                                            .into(categoriaPhoto);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error en caso de fallo al cargar los datos de la categoría
                        Toast.makeText(CreateCategoriaActivity.this, "Error al cargar datos de la categoría", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarCategoriaEnFirestore(String nombre, String descripcion, String fechaCreacion, String estado, Uri selectedImageUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference categoriasRef = db.collection("categorias");

        // Crear un nuevo documento para la nueva categoría
        Map<String, Object> categoriaMap = new HashMap<>();
        categoriaMap.put("nombre", nombre);
        categoriaMap.put("descripcion", descripcion);
        categoriaMap.put("fecha_creacion", fechaCreacion);
        categoriaMap.put("estado", estado);

        // Verificar si se ha seleccionado una imagen
        if (selectedImageUri != null) {
            // Subir la imagen a Firebase Storage y obtener la URL de descarga
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("imagenes_categorias")
                    .child(nombre + "_imagen.jpg");

            storageRef.putFile(selectedImageUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageRef.getDownloadUrl();
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Obtener la URL de descarga de la imagen subida
                            String imageUrl = task.getResult().toString();
                            categoriaMap.put("imagen_url", imageUrl);

                            // Agregar la nueva categoría a Firestore
                            categoriasRef.add(categoriaMap)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(CreateCategoriaActivity.this, "Categoría agregada correctamente", Toast.LENGTH_SHORT).show();
                                        finish(); // Cerrar la actividad después de agregar la categoría
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CreateCategoriaActivity.this, "Error al agregar la categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(CreateCategoriaActivity.this, "Error al subir imagen: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // No se seleccionó una imagen, agregar la categoría sin imagen
            categoriasRef.add(categoriaMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CreateCategoriaActivity.this, "Categoría agregada correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad después de agregar la categoría
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateCategoriaActivity.this, "Error al agregar la categoría: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ImageView usuario_photoCateg = findViewById(R.id.usuario_photoCateg);
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(categoriaPhoto);
        }
    }
}
