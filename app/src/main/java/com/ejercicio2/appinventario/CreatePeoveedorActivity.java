package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatePeoveedorActivity extends AppCompatActivity {

    private EditText nombreProv, direccionProv, telefonoProv, emailProv;
    private Spinner spinnerDepartamento;
    private Button btn_addProveedor;

    private FirebaseFirestore mFirestore;
    private ArrayAdapter<String> departamentoAdapter;
    private boolean isEditMode = false;
    private String proveedorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_peoveedor);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener referencias a las vistas
        btn_addProveedor = findViewById(R.id.btn_addProveedor);
        nombreProv = findViewById(R.id.nombreproveedor);
        spinnerDepartamento = findViewById(R.id.spinnerdepartamentoProveedor);
        direccionProv = findViewById(R.id.direccionproveedor);
        telefonoProv = findViewById(R.id.Telefonoprov);
        emailProv = findViewById(R.id.emailprov);

        // Obtener el ID del proveedor si se proporcionó
        proveedorId = getIntent().getStringExtra("PROVEEDOR_ID");
        if (proveedorId != null) {
            // Modo de edición: cargar los datos del proveedor existente
            cargarDatosProveedor(proveedorId);
            // Establecer el modo de edición
            isEditMode = true;
            // Cambiar el texto del botón a "Actualizar"
            btn_addProveedor.setText("Actualizar");
        } else {
            // Modo de creación: texto predeterminado del botón es "Guardar"
            btn_addProveedor.setText("Guardar");
        }

        // Configurar adaptador para Spinner de departamento
        String[] departamentosHonduras = {"Seleccione el Departamento",
                "Atlántida", "Colón", "Comayagua", "Copán", "Cortés", "Choluteca",
                "El Paraíso", "Francisco Morazán", "Gracias a Dios", "Intibucá",
                "Islas de la Bahía", "La Paz", "Lempira", "Ocotepeque", "Olancho",
                "Santa Bárbara", "Valle", "Yoro"};

        departamentoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departamentosHonduras);
        departamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartamento.setAdapter(departamentoAdapter);

        // Configurar clic del botón para agregar o actualizar proveedor
        btn_addProveedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarProveedor();
            }
        });
    }

    private void cargarDatosProveedor(String proveedorId) {
        mFirestore.collection("proveedor").document(proveedorId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del proveedor
                            Map<String, Object> proveedorData = documentSnapshot.getData();

                            // Establecer los valores en los EditText y Spinner
                            nombreProv.setText((String) proveedorData.get("nombre"));
                            direccionProv.setText((String) proveedorData.get("direccion"));
                            telefonoProv.setText((String) proveedorData.get("telefono"));
                            emailProv.setText((String) proveedorData.get("email"));
                            // Obtener y establecer el departamento del proveedor
                            String departamento = (String) proveedorData.get("departamento");
                            int indexDepartamento = departamentoAdapter.getPosition(departamento);
                            spinnerDepartamento.setSelection(indexDepartamento);
                        }
                    }
                });
    }

    private void guardarProveedor() {
        String nombre = nombreProv.getText().toString().trim();
        String direccion = direccionProv.getText().toString().trim();
        String telefono = telefonoProv.getText().toString().trim();
        String email = emailProv.getText().toString().trim();
        String departamento = spinnerDepartamento.getSelectedItem().toString();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || departamento.equals("Seleccione el Departamento")) {
            Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa con los datos del proveedor
        Map<String, Object> proveedorMap = new HashMap<>();
        proveedorMap.put("nombre", nombre);
        proveedorMap.put("direccion", direccion);
        proveedorMap.put("telefono", telefono);
        proveedorMap.put("email", email);
        proveedorMap.put("departamento", departamento);

        // Guardar o actualizar el proveedor en Firestore
        DocumentReference proveedorRef;
        if (isEditMode) {
            proveedorRef = mFirestore.collection("proveedor").document(proveedorId);
        } else {
            proveedorRef = mFirestore.collection("proveedor").document();
            proveedorId = proveedorRef.getId(); // Asignar el nuevo ID generado
        }

        proveedorRef.set(proveedorMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Proveedor " + (isEditMode ? "actualizado" : "guardado") + " correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad después de guardar o actualizar
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al " + (isEditMode ? "actualizar" : "guardar") + " el proveedor", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
