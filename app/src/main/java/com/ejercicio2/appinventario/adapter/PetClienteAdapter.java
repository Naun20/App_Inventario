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

import com.ejercicio2.appinventario.CreateClienteaActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.PetCliente;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Locale;

public class PetClienteAdapter  extends FirestoreRecyclerAdapter<PetCliente, PetClienteAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    Activity activity;
    Query query;
    public PetClienteAdapter(@NonNull FirestoreRecyclerOptions<PetCliente> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull PetCliente PetCliente) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final  String id = documentSnapshot.getId();

        viewHolder.nombre.setText(PetCliente.getNombre());
        viewHolder.dni.setText(PetCliente.getDni());
        viewHolder.direccion.setText(PetCliente.getDireccion());
        viewHolder.genero.setText(PetCliente.getGenero());
        viewHolder.telefono.setText(PetCliente.getTelefono());
        viewHolder.email.setText(PetCliente.getEmail());
        viewHolder.tipo.setText(PetCliente.getTipo());
        viewHolder.fechaCreacion.setText(PetCliente.getFechaCreacion());
        viewHolder.edad.setText(PetCliente.getEdad());

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            deleteCliente(id);
            }
        });
        viewHolder.btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar la lógica para abrir CreateClienteaActivity en modo de edición
                abrirActivityEditarCliente(id);
            }
        });
    }

    private void abrirActivityEditarCliente(String clienteId) {
        // Crear un intent para abrir CreateClienteaActivity en modo de edición
        Intent intent = new Intent(activity, CreateClienteaActivity.class);

        // Pasar el ID del cliente como extra al intent
        intent.putExtra("CLIENTE_ID", clienteId);

        // Iniciar la actividad de edición
        activity.startActivity(intent);
    }


    private void deleteCliente(String id) {
        mFirestore.collection("cliente").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ToasEliminadoCorrecto();
              //  Toast.makeText(activity, "Eliminar Correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cliente_single, parent, false);
        return  new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, dni, direccion, genero, telefono, email, tipo, fechaCreacion, edad;

        ImageView btn_delete, btn_editar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombrecli);
            dni = itemView.findViewById(R.id.dnicli);
            direccion = itemView.findViewById(R.id.direccioncli);
            genero = itemView.findViewById(R.id.generocli);
            telefono = itemView.findViewById(R.id.telefonocli);
            email = itemView.findViewById(R.id.emailcli);
            tipo = itemView.findViewById(R.id.tipocli);
            fechaCreacion = itemView.findViewById(R.id.fechacreacioncli);
            edad = itemView.findViewById(R.id.edadcli);
            btn_delete = itemView.findViewById(R.id.btn_eliminarcliente);
            btn_editar = itemView.findViewById(R.id.btn_editarcliente);

        }

    }
    public void ToasEliminadoCorrecto() {
        // Inflar el layout del Toast personalizado
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_clienteeliminado, (ViewGroup) activity.findViewById(R.id.CustomToast4));

        // Crear y mostrar el Toast personalizado
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }



}
