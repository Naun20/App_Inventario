package com.ejercicio2.appinventario;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.Negocio;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateNegocioActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nombreNegocio, numeroDoc, cai, impuesto, telefono, descripcion, email, direccion, mensaje;
    private ImageView fotoNegocio;
    private Button btnActualizar, btnEliminarFoto, btnEditar;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private Negocio negocio;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_negocio);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Inicializar los campos de la interfaz
        fotoNegocio = findViewById(R.id.fotonegocio);
        nombreNegocio = findViewById(R.id.nombrenegocio);
        numeroDoc = findViewById(R.id.numerodoc);
        cai = findViewById(R.id.cai);
        impuesto = findViewById(R.id.impuesto);
        telefono = findViewById(R.id.telefono);
        descripcion = findViewById(R.id.descripcion);
        email = findViewById(R.id.email);
        direccion = findViewById(R.id.direccion);
        mensaje = findViewById(R.id.mensaje);
        btnActualizar = findViewById(R.id.btn_actualizar);
        btnEliminarFoto = findViewById(R.id.btneliminarfoto);
        btnEditar = findViewById(R.id.btneditar);

        // Recibir los datos del intent
        negocio = (Negocio) getIntent().getSerializableExtra("negocio");
        if (negocio != null) {
            rellenarCampos();
        }

        // Abrir galería al hacer clic en la imagen
        fotoNegocio.setOnClickListener(v -> seleccionarImagen());

        // Abrir galería al hacer clic en el botón "Foto"
        btnEditar.setOnClickListener(v -> seleccionarImagen());

        // Actualizar negocio al hacer clic en el botón
        btnActualizar.setOnClickListener(v -> actualizarNegocio());

        // Eliminar foto al hacer clic en el botón
        btnEliminarFoto.setOnClickListener(v -> eliminarFoto());
    }

    private void rellenarCampos() {
        nombreNegocio.setText(negocio.getNom_empresa());
        numeroDoc.setText(negocio.getNumerodoc());
        cai.setText(negocio.getCai());
        impuesto.setText(negocio.getImpuesto());
        telefono.setText(negocio.getTelefono());
        descripcion.setText(negocio.getDescripcion());
        email.setText(negocio.getEmail());
        direccion.setText(negocio.getDire_empresa());
        mensaje.setText(negocio.getMensaje());
        // Cargar imagen en fotoNegocio
        Glide.with(this).load(negocio.getImagenlogo()).into(fotoNegocio);
    }

    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                fotoNegocio.setImageBitmap(bitmap);
                uploadImageToFirebase(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(Uri uri) {
        // Verificar si hay una imagen anterior cargada
        if (negocio.getImagenlogo() != null && !negocio.getImagenlogo().isEmpty()) {
            // Obtener referencia a la imagen anterior
            StorageReference oldRef = storage.getReferenceFromUrl(negocio.getImagenlogo());
            // Eliminar la imagen anterior de Firebase Storage
            oldRef.delete().addOnSuccessListener(aVoid -> uploadNewImage(uri))
                    .addOnFailureListener(e -> Toast.makeText(CreateNegocioActivity.this, "Error al eliminar la foto antigua", Toast.LENGTH_SHORT).show());
        } else {
            // Si no hay una imagen anterior, cargar la nueva imagen directamente
            uploadNewImage(uri);
        }
    }

    private void uploadNewImage(Uri uri) {
        // Crear una referencia a la nueva imagen en Firebase Storage
        StorageReference ref = storageReference.child("logonegocio/" + negocio.getNumerodoc() + ".jpg");
        // Subir la nueva imagen al almacenamiento
        ref.putFile(uri).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri1 -> {
            // Obtener la URL de descarga de la nueva imagen
            imageUrl = uri1.toString();
            // Actualizar la URL de la imagen en el objeto negocio
            negocio.setImagenlogo(imageUrl);
            // Mostrar mensaje de éxito
            Toast.makeText(CreateNegocioActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
        })).addOnFailureListener(e -> Toast.makeText(CreateNegocioActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
    }

    private void actualizarNegocio() {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("nom_empresa", nombreNegocio.getText().toString());
        updatedData.put("numerodoc", numeroDoc.getText().toString());
        updatedData.put("cai", cai.getText().toString());
        updatedData.put("impuesto", impuesto.getText().toString());
        updatedData.put("telefono", telefono.getText().toString());
        updatedData.put("descripcion", descripcion.getText().toString());
        updatedData.put("email", email.getText().toString());
        updatedData.put("dire_empresa", direccion.getText().toString());
        updatedData.put("mensaje", mensaje.getText().toString());

        if (imageUrl != null) {
            updatedData.put("imagenlogo", imageUrl); // Aquí se agrega la URL de la imagen al mapa
        }

        db.collection("negocio").document(negocio.getNumerodoc())
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateNegocioActivity.this, "Negocio actualizado con éxito", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK); // Indicar que la actividad se realizó con éxito
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(CreateNegocioActivity.this, "Error al actualizar negocio", Toast.LENGTH_SHORT).show());
    }


    private void eliminarFoto() {
        if (negocio.getImagenlogo() != null && !negocio.getImagenlogo().isEmpty()) {
            StorageReference ref = storage.getReferenceFromUrl(negocio.getImagenlogo());
            ref.delete().addOnSuccessListener(aVoid -> {
                negocio.setImagenlogo(null);
                fotoNegocio.setImageResource(R.drawable.producto); // Restablecer imagen por defecto
                Toast.makeText(CreateNegocioActivity.this, "Foto eliminada con éxito", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(CreateNegocioActivity.this, "Error al eliminar la foto", Toast.LENGTH_SHORT).show());
        }
    }
}
