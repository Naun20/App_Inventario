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
import com.ejercicio2.appinventario.CreateProductoActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.Producto;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProductoAdapter extends FirestoreRecyclerAdapter<Producto, ProductoAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;
    public ProductoAdapter(@NonNull FirestoreRecyclerOptions<Producto> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Producto Producto) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.nombre.setText(Producto.getNombre());
        viewHolder.codigoBarra.setText(Producto.getCodigoBarra());
        viewHolder.descripcion.setText(Producto.getDescripcion());
        viewHolder.marca.setText(Producto.getMarca());
        viewHolder.stock.setText(String.valueOf(Producto.getStock()));
        viewHolder.precio.setText(Producto.getPrecio());
        viewHolder.fechaCreacion.setText(Producto.getFechaCreacion());
        viewHolder.fechaVencimiento.setText(Producto.getFechaVencimiento());
        viewHolder.estado.setText(Producto.getEstado());
        viewHolder.categoria.setText(Producto.getCategoria());

        viewHolder.btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar la lógica para abrir CreateClienteaActivity en modo de edición
                abrirActivityEditarProducto(id);
            }
        });


        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteproducto(id, Producto); // Eliminar Categoria
            }
        });

        // Cargar imagen utilizando Picasso (o Glide)
        if (Producto.getImagen_url() != null && !Producto.getImagen_url().isEmpty()) {
            Picasso.get().load(Producto.getImagen_url()).into(viewHolder.imagen);
        } else {
            // Si la URL de la imagen está vacía, dejar usuarioPhoto vacío
            viewHolder.imagen.setImageDrawable(null);
        }

    }

    private void abrirActivityEditarProducto(String productoId) {
        Intent intent = new Intent(activity, CreateProductoActivity.class);
        intent.putExtra("PRODUCTOS_ID", productoId);
        activity.startActivity(intent);
    }

    private void deleteproducto(String id, Producto producto) {
        mFirestore.collection("productos").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Categoría eliminada correctamente
                        Toast.makeText(activity, "Producto eliminada correctamente", Toast.LENGTH_SHORT).show();

                        // Eliminar imagen almacenada (si existe)
                        deleteStoredImage(producto.getImagen_url());
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_producto_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, codigoBarra, descripcion, marca, stock, precio,
                fechaCreacion, fechaVencimiento, estado, categoria;
        ImageView imagen, btn_delete, btn_editar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asignar vistas a las variables dentro del ViewHolder
            nombre = itemView.findViewById(R.id.nombreproduc);
            codigoBarra = itemView.findViewById(R.id.codigobarraproduc);
            descripcion = itemView.findViewById(R.id.descripcionpro);
            marca = itemView.findViewById(R.id.marcaproduc);
            stock = itemView.findViewById(R.id.stockproduc);
            precio = itemView.findViewById(R.id.precioproduc);
            fechaCreacion = itemView.findViewById(R.id.fechacreacionproduc);
            fechaVencimiento = itemView.findViewById(R.id.fechavencimientoproduc);
            estado = itemView.findViewById(R.id.estadoproduc);
            categoria = itemView.findViewById(R.id.categoriaproduc);
            imagen = itemView.findViewById(R.id.imagenproduc);

            btn_delete = itemView.findViewById(R.id.btn_eliminarproducto);
            btn_editar = itemView.findViewById(R.id.btn_editarproducto);
        }
    }
}
