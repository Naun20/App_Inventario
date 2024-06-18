package com.ejercicio2.appinventario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateProductoActivity extends AppCompatActivity {
    private Uri selectedImageUri;

    private Spinner spinnerCategoria;
    private EditText stockEditText, nombreEditText, codigoBarraEditText,
            descripcionEditText, marcaEditText, precioEditText, fechaCreacionEditText, fechaVencimientoEditText;
    private CheckBox checkBoxActivo, checkBoxNoActivo;
    private ImageButton masButton, menosButton;
    private int stock = 1; // Valor inicial del stock
    private List<String> categoriasList;
    private StorageReference mStorageRef;
    private List<Boolean> activoList;
    private FirebaseFirestore mFirestore;
    private ImageView productoPhoto;
    private SimpleDateFormat dateFormatter;
    private Button btnAgreFoto,btnEliminarFoto,btnAddproductos;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isEditMode = false;
    private TextView nomproducto; //Actualiza el nombre de registrar y actualizar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_producto);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        // Referencias a los elementos de la interfaz
        nombreEditText = findViewById(R.id.NomProduc);
        codigoBarraEditText = findViewById(R.id.CodigoBarra);
        descripcionEditText = findViewById(R.id.Descripcionproducto);
        marcaEditText = findViewById(R.id.Marca);
        precioEditText = findViewById(R.id.precio);
        fechaCreacionEditText = findViewById(R.id.FechaCreaproducto);
        fechaVencimientoEditText = findViewById(R.id.FechaVencimiento);
        checkBoxActivo = findViewById(R.id.checkBoxActivo);
        checkBoxNoActivo = findViewById(R.id.checkBoxNoActivo);
        stockEditText = findViewById(R.id.Stock);
        masButton = findViewById(R.id.buttonPlus);
        menosButton = findViewById(R.id.buttonMinus);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        //Actualizar
        nomproducto = findViewById(R.id.tvproducto);

        btnAgreFoto = findViewById(R.id.btn_photoproduc);
        btnEliminarFoto = findViewById(R.id.btn_eliminarphotoproduc);
        btnAddproductos = findViewById(R.id.btn_addProducto);
        productoPhoto = findViewById(R.id.usuario_photoproduc);

        mostrarFechaActual();
        fechaCreacionEditText.setFocusable(false);
        fechaCreacionEditText.setClickable(false);

        String productoId = getIntent().getStringExtra("PRODUCTOS_ID");
        if (productoId != null) {
            // Modo de edición: cargar los datos del cliente existente
            cargarDatosProducto(productoId);
            // Establecer el modo de edición
            isEditMode = true;
            // Cambiar el texto del botón a "Actualizar"
            nomproducto.setText("Actualizar Producto");
            btnAddproductos.setText("Actualizar");
        } else {
            // Modo de creación: texto predeterminado del botón es "Guardar"
            btnAddproductos.setText("Guardar");
            nomproducto.setText("Registrar Categoria");
        }

        fechaVencimientoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        checkBoxActivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Si se marca la opción "Activo", desmarcar la opción "No Activo"
                    checkBoxNoActivo.setChecked(false);
                }
            }
        });

        checkBoxNoActivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Si se marca la opción "No Activo", desmarcar la opción "Activo"
                    checkBoxActivo.setChecked(false);
                }
            }
        });

        // Cargar categorías desde Firestore
        cargarCategorias();

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
                productoPhoto.setImageResource(R.drawable.producto); // Mostrar la imagen por defecto o una imagen de "Agregar" en el ImageView

                // Reiniciar la URI de la imagen seleccionada
                selectedImageUri = null;

                Toast.makeText(CreateProductoActivity.this, "Foto eliminada. Selecciona otra imagen.", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el listener para el Spinner
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Mostrar el texto predeterminado
                    ((TextView) view).setText("Seleccione la Categoría");
                } else {
                    // Obtener el estado (activo o no activo) de la categoría seleccionada
                    boolean esActivo = activoList.get(position);

                    // Obtener el nombre de la categoría seleccionada
                    String nombreCategoria = categoriasList.get(position);

                    // Modificar el texto mostrado en el Spinner según el estado de la categoría
                    if (!esActivo) {
                        // La categoría no está activa, mostrar "(No activo)" junto al nombre de la categoría
                        ((TextView) view).setText(nombreCategoria + " (No activo)");
                    } else {
                        // La categoría está activa, mostrar solo el nombre de la categoría
                        ((TextView) view).setText(nombreCategoria);
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Método requerido pero no necesitamos implementar nada aquí
            }
        });


        // Configurar OnClickListener para el botón de incremento (masButton)
        masButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el valor actual del EditText y convertirlo a entero
                String stockText = stockEditText.getText().toString();
                if (!stockText.isEmpty()) {
                    stock = Integer.parseInt(stockText);
                }

                // Incrementar el stock y actualizar el EditText
                stock++;
                stockEditText.setText(String.valueOf(stock));
            }
        });

        // Configurar OnClickListener para el botón de decremento (menosButton)
        menosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el valor actual del EditText y convertirlo a entero
                String stockText = stockEditText.getText().toString();
                if (!stockText.isEmpty()) {
                    stock = Integer.parseInt(stockText);
                }

                // Verificar que el stock no sea menor que 1 antes de decrementar
                if (stock > 1) {
                    stock--;
                    stockEditText.setText(String.valueOf(stock));
                }
            }
        });

        btnAddproductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de texto
                String nombre = nombreEditText.getText().toString().trim();
                String codigoBarra = codigoBarraEditText.getText().toString().trim();
                String descripcion = descripcionEditText.getText().toString().trim();
                String marca = marcaEditText.getText().toString().trim();
                String precio = precioEditText.getText().toString().trim();
                String fechaCreacion = fechaCreacionEditText.getText().toString().trim();
                String fechaVencimiento = fechaVencimientoEditText.getText().toString().trim();
                String stockStr = stockEditText.getText().toString().trim(); // Obtener el texto del campo de stock

                int stock = 0;
                if (!stockStr.isEmpty()) {
                    try {
                        stock = Integer.parseInt(stockStr); // Intentar convertir el texto a un entero
                    } catch (NumberFormatException e) {
                        Toast.makeText(CreateProductoActivity.this, "El valor de stock no es válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Verificar cuál CheckBox está seleccionada
                String estado = null;
                if (checkBoxActivo.isChecked()) {
                    estado = "Activo";
                } else if (checkBoxNoActivo.isChecked()) {
                    estado = "No Activo";
                }

                // Obtener la categoría seleccionada del Spinner
                String categoria = spinnerCategoria.getSelectedItem().toString();

                // Verificar si estamos en modo de edición
                String productoId = getIntent().getStringExtra("PRODUCTOS_ID");
                if (isEditMode) {
                    actualizarProducto(productoId, nombre, codigoBarra, descripcion, marca, stock, precio, fechaCreacion, fechaVencimiento, estado, categoria, selectedImageUri);
                } else {
                    // Validar campos obligatorios para crear un nuevo producto
                    if (nombre.isEmpty() || codigoBarra.isEmpty() || descripcion.isEmpty() || marca.isEmpty() ||
                            stockStr.isEmpty() || precio.isEmpty() || fechaCreacion.isEmpty() || fechaVencimiento.isEmpty() || selectedImageUri == null) {
                        Toast.makeText(CreateProductoActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Llamada al método guardarImagenEnStorage con todos los parámetros, incluyendo selectedImageUri
                    guardarImagenEnStorage(nombre, codigoBarra, descripcion, marca, stock, precio, fechaCreacion, fechaVencimiento, estado, categoria, selectedImageUri);
                }
            }
        });



        // Aplicar el ajuste de padding para los system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void mostrarFechaActual() {
        // Obtener la fecha actual en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date()); // Obtener la fecha actual

        // Establecer la fecha actual en el EditText de fecha
        fechaCreacionEditText.setText(fechaActual);
    }

    private void cargarCategorias() {
        mFirestore.collection("categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoriasList = new ArrayList<>();
                            activoList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombreCategoria = document.getString("nombre");
                                String estadoCategoria = document.getString("estado");

                                if (estadoCategoria != null && estadoCategoria.equals("Activo")) {
                                    // La categoría está activa, agregar a la lista
                                    categoriasList.add(nombreCategoria);
                                    activoList.add(true);
                                }
                            }

                            // Verificar si se encontraron categorías activas
                            if (!categoriasList.isEmpty()) {
                                // Agregar el texto predeterminado al principio de la lista
                                categoriasList.add(0, "Seleccione la Categoría");

                                // Configurar el adapter para el Spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateProductoActivity.this,
                                        android.R.layout.simple_spinner_item, categoriasList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCategoria.setAdapter(adapter);

                                // Manejar la selección del Spinner
                                spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        // Asegurarse de ignorar la primera posición (texto predeterminado)
                                        if (position > 0) {
                                            // Obtener el estado (activo o no activo) de la categoría seleccionada
                                            boolean esActivo = activoList.get(position - 1); // Restar 1 para ajustar la posición

                                            // Obtener el nombre de la categoría seleccionada
                                            String nombreCategoria = categoriasList.get(position);

                                            // Modificar el texto mostrado en el Spinner según el estado de la categoría
                                            if (!esActivo) {
                                                // La categoría no está activa, mostrar "(No activo)" junto al nombre de la categoría
                                                ((TextView) view).setText(nombreCategoria + " (No activo)");
                                            } else {
                                                // La categoría está activa, mostrar solo el nombre de la categoría
                                                ((TextView) view).setText(nombreCategoria);
                                            }
                                        } else {
                                            // Si se selecciona el texto predeterminado, mostrarlo como está
                                            ((TextView) view).setText("Seleccione la Categoría");
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                        // Método requerido pero no necesitamos implementar nada aquí
                                    }
                                });
                            } else {
                                // No se encontraron categorías activas
                                Toast.makeText(CreateProductoActivity.this, "No hay categorías activas disponibles", Toast.LENGTH_SHORT).show();
                                // Puedes manejar esta situación de acuerdo a tus necesidades
                            }
                        } else {
                            // Manejar el error al cargar las categorías
                            Toast.makeText(CreateProductoActivity.this, "Error al cargar las categorías", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void actualizarProducto(String productoId, String nombre, String codigoBarra, String descripcion, String marca,
                                    int stock, String precio, String fechaCreacion, String fechaVencimiento,
                                    String estado, String categoria, Uri selectedImageUri) {
        if (TextUtils.isEmpty(productoId)) {
            Toast.makeText(CreateProductoActivity.this, "ID de producto no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference productoRef = db.collection("productos").document(productoId);

        // Crear un mapa con los datos actualizados del producto
        Map<String, Object> productoMap = new HashMap<>();
        if (!nombre.isEmpty()) productoMap.put("nombre", nombre);
        if (!codigoBarra.isEmpty()) productoMap.put("codigoBarra", codigoBarra);
        if (!descripcion.isEmpty()) productoMap.put("descripcion", descripcion);
        if (!marca.isEmpty()) productoMap.put("marca", marca);
        if (stock > 0) productoMap.put("stock", stock);
        if (!precio.isEmpty()) productoMap.put("precio", precio);
        if (!fechaCreacion.isEmpty()) productoMap.put("fechaCreacion", fechaCreacion);
        if (!fechaVencimiento.isEmpty()) productoMap.put("fechaVencimiento", fechaVencimiento);
        if (estado != null) productoMap.put("estado", estado);
        if (!categoria.isEmpty()) productoMap.put("categoria", categoria);

        // Verificar si se ha seleccionado una nueva imagen
        if (selectedImageUri != null) {
            // Obtener referencia a la ubicación de almacenamiento de la nueva imagen
            String imageFileName = nombre + "_imagen.jpg";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("imagenes_productos")
                    .child(imageFileName);

            // Eliminar la imagen existente (si hay una) antes de subir la nueva imagen
            productoRef.get().addOnSuccessListener(documentSnapshot -> {
                String imageUrl = (String) documentSnapshot.get("imagen_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Obtener referencia a la imagen existente en Firebase Storage y eliminarla
                    StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                    existingImageRef.delete().addOnSuccessListener(aVoid -> {
                        // Subir la nueva imagen a Firebase Storage y obtener la URL de descarga
                        subirNuevaImagen(storageRef, selectedImageUri, productoMap, productoRef);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(CreateProductoActivity.this, "Error al eliminar la imagen existente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // No hay imagen existente, subir directamente la nueva imagen
                    subirNuevaImagen(storageRef, selectedImageUri, productoMap, productoRef);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(CreateProductoActivity.this, "Error al obtener datos del producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // No se seleccionó una nueva imagen, actualizar solo los datos del producto sin cambiar la imagen
            actualizarProductoEnFirestore(productoMap, productoRef);
        }
    }

    private void subirNuevaImagen(StorageReference storageRef, Uri selectedImageUri, Map<String, Object> productoMap, DocumentReference productoRef) {
        storageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                productoMap.put("imagen_url", uri.toString());
                actualizarProductoEnFirestore(productoMap, productoRef);
            }).addOnFailureListener(e -> {
                Toast.makeText(CreateProductoActivity.this, "Error al obtener la URL de la nueva imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateProductoActivity.this, "Error al subir la nueva imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarProductoEnFirestore(Map<String, Object> productoMap, DocumentReference productoRef) {
        productoRef.update(productoMap).addOnSuccessListener(aVoid -> {
            Toast.makeText(CreateProductoActivity.this, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
            finish(); // Finalizar la actividad después de actualizar exitosamente
        }).addOnFailureListener(e -> {
            Toast.makeText(CreateProductoActivity.this, "Error al actualizar el producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void guardarImagenEnStorage(String nombre, String codigoBarra, String descripcion, String marca, int stock,
                                        String precio, String fechaCreacion, String fechaVencimiento, String estado, String categoria,
                                        Uri selectedImageUri) {
        // Generar un nombre único para la imagen utilizando UUID
        String imageFileName = UUID.randomUUID().toString();
        final StorageReference imageRef = mStorageRef.child("imagenes_productos/" + imageFileName);

        // Subir la imagen al Storage
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Si la subida de la imagen fue exitosa, obtener la URL de descarga
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Guardar la URL de la imagen y los datos del producto en Firestore
                        String imageUrl = uri.toString();
                        guardarProductoEnFirestore(nombre, codigoBarra, descripcion, marca, stock, precio, fechaCreacion,
                                fechaVencimiento, estado, categoria, imageUrl);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(CreateProductoActivity.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateProductoActivity.this, "Error al subir la imagen al Storage", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarProductoEnFirestore(String nombre, String codigoBarra, String descripcion, String marca, int stock,
                                            String precio, String fechaCreacion, String fechaVencimiento, String estado, String categoria,
                                            String imageUrl) {
        // Crear un mapa con los datos del producto
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("codigoBarra", codigoBarra);
        producto.put("descripcion", descripcion);
        producto.put("marca", marca);
        producto.put("stock", stock);
        producto.put("precio", precio);
        producto.put("fechaCreacion", fechaCreacion);
        producto.put("fechaVencimiento", fechaVencimiento);
        producto.put("estado", estado);
        producto.put("categoria", categoria);
        producto.put("imagen_url", imageUrl);

        // Guardar el producto en la colección "productos" en Firestore
        FirebaseFirestore.getInstance().collection("productos")
                .add(producto)
                .addOnSuccessListener(documentReference -> {
                    // Éxito al guardar el producto en Firestore
                    Toast.makeText(CreateProductoActivity.this, "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                    finish(); // Finalizar la actividad después de guardar exitosamente
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el producto en Firestore
                    Toast.makeText(CreateProductoActivity.this, "Error al crear el producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void showDatePickerDialog() {
        final Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateProductoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Crear un objeto Calendar con la fecha seleccionada
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);

                        // Formatear la fecha y establecerla en el EditText
                        fechaVencimientoEditText.setText(dateFormatter.format(selectedDate.getTime()));
                    }
                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        // Mostrar el DatePickerDialog
        datePickerDialog.show();
    }

    private void cargarDatosProducto(String productoId) {
        FirebaseFirestore.getInstance().collection("productos").document(productoId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> productoData = documentSnapshot.getData();

                            if (productoData != null) {
                                // Establecer los valores en los EditText
                                nombreEditText.setText((String) productoData.get("nombre"));
                                codigoBarraEditText.setText((String) productoData.get("codigoBarra"));
                                descripcionEditText.setText((String) productoData.get("descripcion"));
                                marcaEditText.setText((String) productoData.get("marca"));
                                stockEditText.setText(String.valueOf((Long) productoData.get("stock")));
                                precioEditText.setText((String) productoData.get("precio"));
                                fechaCreacionEditText.setText((String) productoData.get("fechaCreacion"));
                                fechaVencimientoEditText.setText((String) productoData.get("fechaVencimiento"));

                                // Obtener el estado del producto
                                String estado = (String) productoData.get("estado");

                                // Establecer el estado en los CheckBox correspondientes
                                if (estado != null) {
                                    if (estado.equals("Activo")) {
                                        checkBoxActivo.setChecked(true);
                                        checkBoxNoActivo.setChecked(false);
                                    } else if (estado.equals("No Activo")) {
                                        checkBoxActivo.setChecked(false);
                                        checkBoxNoActivo.setChecked(true);
                                    }
                                }

                                // Obtener la URL de la imagen del producto
                                String imageUrl = (String) productoData.get("imagen_url");

                                // Cargar la imagen utilizando Glide si imageUrl no está vacío
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(CreateProductoActivity.this)
                                            .load(imageUrl)
                                            .into(productoPhoto);
                                }

                                // Obtener la categoría del producto
                                String categoriaId = (String) productoData.get("categoria");

                                // Cargar las categorías en el Spinner con la categoría seleccionada
                                cargarCategorias(categoriaId);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateProductoActivity.this, "Error al cargar datos del producto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarCategorias(String categoriaSeleccionadaId) {
        mFirestore.collection("categorias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            categoriasList = new ArrayList<>();
                            activoList = new ArrayList<>();
                            int selectedPosition = 0; // Posición de la categoría seleccionada

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String categoriaId = document.getId();
                                String nombreCategoria = document.getString("nombre");
                                String estadoCategoria = document.getString("estado");

                                if (estadoCategoria != null && estadoCategoria.equals("Activo")) {
                                    // La categoría está activa, agregar a la lista
                                    categoriasList.add(nombreCategoria);
                                    activoList.add(true);

                                    // Verificar si esta es la categoría seleccionada
                                    if (categoriaId.equals(categoriaSeleccionadaId)) {
                                        selectedPosition = categoriasList.size() - 1; // Última posición agregada
                                    }
                                }
                            }

                            // Verificar si se encontraron categorías activas
                            if (!categoriasList.isEmpty()) {
                                // Configurar el adapter para el Spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateProductoActivity.this,
                                        android.R.layout.simple_spinner_item, categoriasList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerCategoria.setAdapter(adapter);

                                // Seleccionar la categoría correspondiente
                                spinnerCategoria.setSelection(selectedPosition);

                                // Manejar la selección del Spinner
                                spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        // Implementa lógica según sea necesario
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                        // Método requerido pero no necesitamos implementar nada aquí
                                    }
                                });
                            } else {
                                // No se encontraron categorías activas
                                Toast.makeText(CreateProductoActivity.this, "No hay categorías activas disponibles", Toast.LENGTH_SHORT).show();
                                // Puedes manejar esta situación de acuerdo a tus necesidades
                            }
                        } else {
                            // Manejar el error al cargar las categorías
                            Toast.makeText(CreateProductoActivity.this, "Error al cargar las categorías", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cargarCategoriasEnSpinner(String categoriaIdSeleccionada) {
        FirebaseFirestore.getInstance().collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> categorias = new ArrayList<>();
                    int selectedIndex = -1;

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String categoriaId = documentSnapshot.getId();
                        String nombreCategoria = documentSnapshot.getString("nombre");
                        categorias.add(nombreCategoria);

                        // Verificar si la categoría actual es la seleccionada
                        if (categoriaId.equals(categoriaIdSeleccionada)) {
                            selectedIndex = categorias.size() - 1; // Índice de la categoría seleccionada
                        }
                    }

                    // Crear un ArrayAdapter para el Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateProductoActivity.this, android.R.layout.simple_spinner_item, categorias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Establecer el adaptador en el Spinner
                    spinnerCategoria.setAdapter(adapter);

                    // Seleccionar automáticamente la categoría si se encontró una coincidencia
                    if (selectedIndex != -1) {
                        spinnerCategoria.setSelection(selectedIndex);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateProductoActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ImageView usuario_photoproduc = findViewById(R.id.usuario_photoproduc);
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(productoPhoto);
        }


    }


}
