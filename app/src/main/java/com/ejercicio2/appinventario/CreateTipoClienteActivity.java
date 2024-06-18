package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateTipoClienteActivity extends AppCompatActivity {
    private EditText tipocliente, descuento;
    private Button btn_addtipoCliente;
    private FirebaseFirestore mfirestore;
    private String tipoClienteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tipo_cliente);

        // Inicializar Firestore
        mfirestore = FirebaseFirestore.getInstance();

        // Asignar vistas a las variables
        tipocliente = findViewById(R.id.tipoCliente);
        descuento = findViewById(R.id.descuentoclienta);
        btn_addtipoCliente = findViewById(R.id.btn_addtipocliete);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Verificar si estamos en modo edición
        if (getIntent().hasExtra("tipoClienteId")) {
            tipoClienteId = getIntent().getStringExtra("tipoClienteId");
            String tipo = getIntent().getStringExtra("tipo");
            double descuentoValue = getIntent().getDoubleExtra("descuento", 0);

            // Establecer datos en los EditText
            tipocliente.setText(tipo);
            descuento.setText(String.valueOf(descuentoValue));

            // Cambiar el texto del botón a "Actualizar"
            btn_addtipoCliente.setText("Actualizar");
        }

        // Configurar el listener para el botón
        btn_addtipoCliente.setOnClickListener(v -> {
            String tipoClienteStr = tipocliente.getText().toString().trim();
            String descuentoStr = descuento.getText().toString().trim();

            // Validar los campos
            if (TextUtils.isEmpty(tipoClienteStr)) {
                Toast.makeText(this, "Por favor ingrese el tipo de cliente", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(descuentoStr)) {
                Toast.makeText(this, "Por favor ingrese el descuento", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir el descuento a número
            double descuentoValue;
            try {
                descuentoValue = Double.parseDouble(descuentoStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Descuento inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el mapa de datos
            Map<String, Object> tipoClienteData = new HashMap<>();
            tipoClienteData.put("tipo", tipoClienteStr);
            tipoClienteData.put("descuento", descuentoValue);

            if (tipoClienteId == null) {
                // Guardar en Firestore (modo creación)
                mfirestore.collection("tipocliente").add(tipoClienteData)
                        .addOnSuccessListener(documentReference -> {
                            String id = documentReference.getId();
                            tipoClienteData.put("id", id);
                            documentReference.set(tipoClienteData);

                            Toast.makeText(this, "Tipo de cliente registrado correctamente", Toast.LENGTH_SHORT).show();
                            tipocliente.setText("");
                            descuento.setText("");
                            finish(); // Cerrar la actividad
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al registrar el tipo de cliente", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Actualizar en Firestore (modo edición)
                mfirestore.collection("tipocliente").document(tipoClienteId)
                        .set(tipoClienteData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Tipo de cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                            finish(); // Cerrar la actividad
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al actualizar el tipo de cliente", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
