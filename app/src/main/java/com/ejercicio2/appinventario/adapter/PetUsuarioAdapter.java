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

import com.ejercicio2.appinventario.CreateUsuarioActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.PetUsuario;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PetUsuarioAdapter extends FirestoreRecyclerAdapter<PetUsuario, PetUsuarioAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Activity activity;

    public PetUsuarioAdapter(@NonNull FirestoreRecyclerOptions<PetUsuario> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull PetUsuario petUsuario) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        viewHolder.nombreUser.setText(petUsuario.getNombre());
        viewHolder.emailUser.setText(petUsuario.getEmail());
        viewHolder.telefonoUser.setText(petUsuario.getTelefono());
        viewHolder.usuarioUser.setText(petUsuario.getUsuario());
        viewHolder.passwordUser.setText(petUsuario.getPassword());
        viewHolder.tipouser.setText(petUsuario.getTipo_usuario());

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUsuario(id, petUsuario); // Eliminar usuario
            }
        });

        viewHolder.btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActivityEditarUsuario(id); // Abrir actividad para editar usuario
            }
        });

        // Cargar imagen utilizando Picasso (o Glide)
        if (petUsuario.getImagen_url() != null && !petUsuario.getImagen_url().isEmpty()) {
            Picasso.get().load(petUsuario.getImagen_url()).into(viewHolder.usuarioPhoto);
        } else {
            // Si la URL de la imagen está vacía, dejar usuarioPhoto vacío
            viewHolder.usuarioPhoto.setImageDrawable(null);
        }
    }

    private void abrirActivityEditarUsuario(String usuarioId) {
        Intent intent = new Intent(activity, CreateUsuarioActivity.class);
        intent.putExtra("USUARIO_ID", usuarioId);
        activity.startActivity(intent);
    }

    private void deleteUsuario(String id, PetUsuario petUsuario) {
        mFirestore.collection("usuario").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Eliminar usuario de Firestore correctamente
                        Toast.makeText(activity, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();

                        // Eliminar usuario autenticado
                        deleteAuthenticatedUser();

                        // Eliminar imagen almacenada (si existe)
                        deleteStoredImage(petUsuario.getImagen_url());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al eliminar usuario de Firestore
                        Toast.makeText(activity, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAuthenticatedUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Usuario autenticado eliminado correctamente
                            Toast.makeText(activity, "Usuario autenticado eliminado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al eliminar usuario autenticado
                            Toast.makeText(activity, "Error al eliminar usuario autenticado", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteStoredImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_usuario_single, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUser, emailUser, telefonoUser, usuarioUser, passwordUser, tipouser;
        ImageView usuarioPhoto, btn_delete, btn_editar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreUser = itemView.findViewById(R.id.nombreusuario);
            emailUser = itemView.findViewById(R.id.emailuser);
            telefonoUser = itemView.findViewById(R.id.telefuser);
            usuarioUser = itemView.findViewById(R.id.nomuser);
            passwordUser = itemView.findViewById(R.id.contrasenauser);
            tipouser = itemView.findViewById(R.id.usertipo);
            usuarioPhoto = itemView.findViewById(R.id.photouser);
            btn_delete = itemView.findViewById(R.id.btn_eliminar_usuario);
            btn_editar = itemView.findViewById(R.id.btn_editar_Usuario);
        }
    }
}
