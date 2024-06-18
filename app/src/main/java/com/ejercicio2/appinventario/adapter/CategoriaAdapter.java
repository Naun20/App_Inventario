package com.ejercicio2.appinventario.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.CreateCategoriaActivity;
import com.ejercicio2.appinventario.CreateUsuarioActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.Categoria;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CategoriaAdapter extends FirestoreRecyclerAdapter<Categoria, CategoriaAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;


    public CategoriaAdapter(@NonNull FirestoreRecyclerOptions<Categoria> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Categoria Categoria) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.nombre.setText(Categoria.getNombre());
        viewHolder.descripcion.setText(Categoria.getDescripcion());
        viewHolder.fechaCreacion.setText(Categoria.getFecha_creacion());
        viewHolder.estado.setText(Categoria.getEstado());

        viewHolder.btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar la lógica para abrir CreateClienteaActivity en modo de edición
                abrirActivityEditarCategoria(id);
            }
        });


        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoria(id, Categoria); // Eliminar Categoria
            }
        });

        // Cargar imagen utilizando Picasso (o Glide)
        if (Categoria.getImagen_url() != null && !Categoria.getImagen_url().isEmpty()) {
            Picasso.get().load(Categoria.getImagen_url()).into(viewHolder.imagen);
        } else {
            // Si la URL de la imagen está vacía, dejar usuarioPhoto vacío
            viewHolder.imagen.setImageDrawable(null);
        }
    }

    private void abrirActivityEditarCategoria(String categoriaId) {
        Intent intent = new Intent(activity, CreateCategoriaActivity.class);
        intent.putExtra("CATEGORIAS_ID", categoriaId);
        activity.startActivity(intent);
    }

    private void deleteCategoria(String id, Categoria categoria) {
        mFirestore.collection("categorias").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Categoría eliminada correctamente
                        Toast.makeText(activity, "Categoría eliminada correctamente", Toast.LENGTH_SHORT).show();

                        // Eliminar imagen almacenada (si existe)
                        deleteStoredImage(categoria.getImagen_url());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar la categoría
                        Toast.makeText(activity, "Error al eliminar la categoría", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteStoredImage(String imagenUrl) {
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imagenUrl);
            storageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Imagen almacenada eliminada correctamente
                            Toast.makeText(activity, "Imagen almacenada eliminada", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al eliminar imagen almacenada
                            Toast.makeText(activity, "Error al eliminar imagen almacenada", Toast.LENGTH_SHORT).show();
                        }
                    });
    }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_categoria1_single, parent, false);
        return  new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, descripcion, fechaCreacion, estado;
        ImageView imagen, btn_delete, btn_editar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombrecateg);
            descripcion = itemView.findViewById(R.id.descripcioncateg);
            fechaCreacion = itemView.findViewById(R.id.fechacateg);
            estado = itemView.findViewById(R.id.estadocateg);
            imagen = itemView.findViewById(R.id.imagenCateg);
            btn_delete = itemView.findViewById(R.id.btn_eliminar_categoria);
            btn_editar = itemView.findViewById(R.id.btn_editar_categoria);

        }
    }
}
