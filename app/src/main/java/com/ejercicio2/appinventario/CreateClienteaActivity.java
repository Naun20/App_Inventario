package com.ejercicio2.appinventario;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateClienteaActivity extends AppCompatActivity {
    private EditText nombreCliente, dniCliente, direccionCliente, generoCliente,
            telefonoCliente, emailCliente, tipoCliente, edadCliente;
    private Button btn_addCliente;
    private EditText fechaCreacionCliente;
    private Calendar calendar;
    private Spinner spinnergenero, spinnertipocliente;

    private FirebaseFirestore mfirestore;
    private ArrayAdapter<String> generoAdapter;
    private ArrayAdapter<String> tipoClienteAdapter;
    private boolean isEditMode = false;
    private TextView nomClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_clientea);


        // Inicializar Firestore
        mfirestore = FirebaseFirestore.getInstance();

        // Asignar vistas a las variables
        nomClient = findViewById(R.id.lblClientRegis);
        nombreCliente = findViewById(R.id.nombreCliente);
        dniCliente = findViewById(R.id.dniclienta);
        direccionCliente = findViewById(R.id.direccioncliente);
        spinnergenero = findViewById(R.id.spinergenero);
        telefonoCliente = findViewById(R.id.Telefonocliente);
        emailCliente = findViewById(R.id.emailncliente);
        spinnertipocliente = findViewById(R.id.spinertipocliente);
        fechaCreacionCliente = findViewById(R.id.fechacreacioncliente);
        edadCliente = findViewById(R.id.edadcliente);

        // btn_addCliente = findViewById(R.id.btn_addCliente);
        // Obtener el ID del cliente si se proporcionó
        btn_addCliente = findViewById(R.id.btn_addCliente);
        // Obtener el ID del cliente si se proporcionó
        String clienteId = getIntent().getStringExtra("CLIENTE_ID");
        if (clienteId != null) {
            // Modo de edición: cargar los datos del cliente existente
            cargarDatosCliente(clienteId);
            // Establecer el modo de edición
            isEditMode = true;
            // Cambiar el texto del botón a "Actualizar"
            btn_addCliente.setText("Actualizar");
            nomClient.setText("Actualizar Cliente");
        } else {
            // Modo de creación: texto predeterminado del botón es "Guardar"
            btn_addCliente.setText("Guardar");
            nomClient.setText("Registrar Cliente");
        }

        tipoClienteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        tipoClienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertipocliente.setAdapter(tipoClienteAdapter);

        // Configurar adaptador para Spinner de género
        String[] tipogenero = {"Seleccione el Tipo de Género:", "Masculino", "Femenino"};
        generoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipogenero);
        generoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnergenero.setAdapter(generoAdapter);

        // Obtener la referencia del Spinner desde el layout
        spinnertipocliente = findViewById(R.id.spinertipocliente);

        // Obtener tipos de cliente de Firestore y configurar el adaptador del Spinner
        obtenerTiposCliente();
        obtenerTiposCliente();

        // Inicializar el calendario
        calendar = Calendar.getInstance();
        mostrarFechaActual();
        fechaCreacionCliente.setFocusable(false);
        fechaCreacionCliente.setClickable(false);

        // Manejar clic en el EditText de Fecha de Creación

        // Manejar clic en el botón de agregar cliente
        btn_addCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = nombreCliente.getText().toString().trim();
                String dni = dniCliente.getText().toString().trim();
                String direccion = direccionCliente.getText().toString().trim();
                String telefono = telefonoCliente.getText().toString().trim();
                String email = emailCliente.getText().toString().trim();
                String fechaCreacion = fechaCreacionCliente.getText().toString().trim();
                String edad = edadCliente.getText().toString().trim();
                String genero = spinnergenero.getSelectedItem().toString();
                String tipo = spinnertipocliente.getSelectedItem().toString();

                if (nombre.isEmpty() || dni.isEmpty() || direccion.isEmpty() || genero.isEmpty() ||
                        telefono.isEmpty() || email.isEmpty() || tipo.isEmpty() || fechaCreacion.isEmpty() ||
                        edad.isEmpty()) {
                    // Mostrar mensaje de llenar todos los campos
                    ToasLlenarcampoCorrecto();
                } else {
                    if (isEditMode) {
                        // Estamos en modo de edición: actualizar el cliente existente
                        String clienteId = getIntent().getStringExtra("CLIENTE_ID");
                        if (clienteId != null) {
                            actualizarCliente(clienteId, nombre, dni, direccion, genero, telefono, email, tipo, fechaCreacion, edad);
                        }
                    } else {
                        // Estamos en modo de creación: agregar un nuevo cliente
                        postCliente(nombre, dni, direccion, genero, telefono, email, tipo, fechaCreacion, edad);
                    }
                }
            }
        });
    }

    private void obtenerTiposCliente() {
        mfirestore.collection("tipocliente")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> tiposCliente = new ArrayList<>();
                        tiposCliente.add("Seleccione el tipo de cliente");

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String tipoCliente = document.getString("tipo");
                            tiposCliente.add(tipoCliente);
                        }

                        // Limpiar el adaptador antes de agregar nuevos datos
                        tipoClienteAdapter.clear();
                        // Agregar los tipos de cliente al adaptador
                        tipoClienteAdapter.addAll(tiposCliente);
                        // Notificar al adaptador que los datos han cambiado
                        tipoClienteAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateClienteaActivity.this, "Error al obtener tipos de cliente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarFechaActual() {
        // Obtener la fecha actual en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date()); // Obtener la fecha actual

        // Establecer la fecha actual en el EditText de fecha
        fechaCreacionCliente.setText(fechaActual);
    }

    // En el método actualizarCliente
    private void actualizarCliente(String clienteId, String nombre, String dni, String direccion, String genero, String telefono, String email, String tipo, String fechaCreacion, String edad) {
        // Crear un mapa con los datos actualizados del cliente
        Map<String, Object> clienteMap = new HashMap<>();
        clienteMap.put("nombre", nombre);
        clienteMap.put("dni", dni);
        clienteMap.put("direccion", direccion);
        clienteMap.put("genero", genero);
        clienteMap.put("telefono", telefono);
        clienteMap.put("email", email);
        clienteMap.put("tipo", tipo);
        clienteMap.put("fechaCreacion", fechaCreacion);
        clienteMap.put("edad", edad);

        // Actualizar los datos del cliente en Firestore
        mfirestore.collection("cliente").document(clienteId)
                .set(clienteMap)  // Usar set() en lugar de add() para actualizar
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Mostrar mensaje de cliente actualizado correctamente
                        Toast.makeText(getApplicationContext(), "Cliente Actualizado cliente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad de creación de cliente después de actualizar correctamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al actualizar el cliente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // En el método cargarDatosCliente
    // Dentro del método cargarDatosCliente
    private void cargarDatosCliente(String clienteId) {
        // Aquí debes cargar los datos del cliente desde Firestore usando clienteId
        // y luego establecer los valores en los campos del formulario
        mfirestore.collection("cliente").document(clienteId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del cliente
                            Map<String, Object> clienteData = documentSnapshot.getData();

                            // Establecer los valores en los EditText y Spinners
                            nombreCliente.setText((String) clienteData.get("nombre"));
                            dniCliente.setText((String) clienteData.get("dni"));
                            direccionCliente.setText((String) clienteData.get("direccion"));
                            // Por ejemplo, para el género (spinner)
                            String genero = (String) clienteData.get("genero");
                            int indexGenero = generoAdapter.getPosition(genero);
                            spinnergenero.setSelection(indexGenero);

                            spinnertipocliente.setAdapter(tipoClienteAdapter);
                            String tipo = (String) clienteData.get("tipo");

                            // Imprime los valores para depuración
                            Log.d("DEBUG", "Tipo de cliente desde Firestore: " + tipo);
                            Log.d("DEBUG", "Valores en el adaptador: " + tipoClienteAdapter);

                            // Buscar el índice del tipo de cliente dentro del adaptador
                            int indextipo = tipoClienteAdapter.getPosition(tipo.trim()); // trim() para eliminar espacios en blanco adicionales

                            if (indextipo != -1) {
                                spinnertipocliente.setSelection(indextipo);
                            } else {
                                Toast.makeText(CreateClienteaActivity.this, "Tipo de cliente no encontrado: " + tipo, Toast.LENGTH_SHORT).show();
                            }

                            telefonoCliente.setText((String) clienteData.get("telefono"));
                            emailCliente.setText((String) clienteData.get("email"));
                            fechaCreacionCliente.setText((String) clienteData.get("fechaCreacion"));
                            edadCliente.setText((String) clienteData.get("edad"));

                            // Puedes hacer lo mismo para el resto de campos
                        }
                    }
                });
    }
    // En el método postCliente
    private void postCliente(String nombre, String dni, String direccion, String genero,
                             String telefono, String email, String tipo, String fechaCreacion,
                             String edad) {
        // Crear un mapa con los datos del cliente
        Map<String, Object> clienteMap = new HashMap<>();
        clienteMap.put("nombre", nombre);
        clienteMap.put("dni", dni);
        clienteMap.put("direccion", direccion);
        clienteMap.put("genero", genero);
        clienteMap.put("telefono", telefono);
        clienteMap.put("email", email);
        clienteMap.put("tipo", tipo);
        clienteMap.put("fechaCreacion", fechaCreacion);
        clienteMap.put("edad", edad);

        // Agregar el cliente a la colección "cliente" en Firestore
        mfirestore.collection("cliente")
                .add(clienteMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        ToasguardadCorrecto();
                        finish(); // Cerrar la actividad de creación de cliente después de guardar correctamente
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al crear el cliente", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void ToasguardadCorrecto() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_guardarcliente, this. findViewById(R.id.CustomToast5));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public void ToasLlenarcampoCorrecto() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_llenarcliente, this. findViewById(R.id.CustomToast6));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}
