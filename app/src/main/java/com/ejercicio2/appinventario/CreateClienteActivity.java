package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class CreateClienteActivity extends AppCompatActivity {

    Button btn_guardar;
    EditText name, age, color;
    private FirebaseFirestore mfirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_cliente);

        String id =getIntent().getStringExtra("id_pet");
        mfirestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.nombrepet);
        age = findViewById(R.id.edadpet);
        color = findViewById(R.id.colorpet);
        btn_guardar = findViewById(R.id.btn_guardar);

        if (id ==null || id ==""){
            btn_guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String namepet = name.getText().toString().trim();
                    String agepet = age.getText().toString().trim();
                    String colorpet = color.getText().toString().trim();

                    if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Ingresar Datos", Toast.LENGTH_SHORT).show();
                    }else {
                        postPet(namepet, agepet, colorpet);
                    }
                }
            });
        }else {
            btn_guardar.setText("Update");
            getPet(id);
           btn_guardar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String namepet = name.getText().toString().trim();
                   String agepet = age.getText().toString().trim();
                   String colorpet = color.getText().toString().trim();

                   if (namepet.isEmpty() && agepet.isEmpty() && colorpet.isEmpty()){
                       Toast.makeText(getApplicationContext(), "Ingresar Datos", Toast.LENGTH_SHORT).show();
                   }else {
                       updatePet(namepet, agepet, colorpet, id);
                   }
               }
           });
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updatePet(String namepet, String agepet, String colorpet, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", namepet);
        map.put("age", agepet);
        map.put("color", colorpet);

        mfirestore.collection("pet").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Actualizado Exitosamente", Toast.LENGTH_SHORT).show();
           finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al Actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postPet(String namepet, String agepet, String colorpet) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", namepet);
        map.put("age", agepet);
        map.put("color", colorpet);

        mfirestore.collection("pet").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Crear Exitosamente", Toast.LENGTH_SHORT).show();
            finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getPet (String id){
        mfirestore.collection("pet").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String namePet = documentSnapshot.getString("name");
                String agePet = documentSnapshot.getString("age");
                String colorPet = documentSnapshot.getString("color");
                name.setText(namePet);
                age.setText(agePet);
                color.setText(colorPet);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}