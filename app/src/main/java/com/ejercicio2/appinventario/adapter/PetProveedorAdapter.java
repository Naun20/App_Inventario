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

import com.ejercicio2.appinventario.AgregarProveedorActivity;
import com.ejercicio2.appinventario.CreateClienteaActivity;
import com.ejercicio2.appinventario.CreatePeoveedorActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.PetProveedor;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PetProveedorAdapter  extends FirestoreRecyclerAdapter <PetProveedor, PetProveedorAdapter.ViwHolder>{
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    Activity activity;
    public PetProveedorAdapter(@NonNull FirestoreRecyclerOptions<PetProveedor> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViwHolder viwHolder, int i, @NonNull PetProveedor PetProveedor) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viwHolder.getAdapterPosition());
        final  String id = documentSnapshot.getId();

        viwHolder.nombre.setText(PetProveedor.getNombre());
        viwHolder.departamento.setText(PetProveedor.getDepartamento());
        viwHolder.direccion.setText(PetProveedor.getDireccion());
        viwHolder.telefono.setText(PetProveedor.getTelefono());
        viwHolder.email.setText(PetProveedor.getEmail());

        viwHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCliente(id);
            }
        });
        viwHolder.btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implementar la lógica para abrir CreateClienteaActivity en modo de edición
                abrirActivityEditarProveedor(id);
            }
        });
    }

    private void abrirActivityEditarProveedor(String proveedorId) {
        // Crear un intent para abrir CreateClienteaActivity en modo de edición
        Intent intent = new Intent(activity, CreatePeoveedorActivity.class);

        // Pasar el ID del cliente como extra al intent
        intent.putExtra("PROVEEDOR_ID", proveedorId);

        // Iniciar la actividad de edición
        activity.startActivity(intent);
    }


    private void deleteCliente(String id) {
        mFirestore.collection("proveedor").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Proveedor eliminado Correctamente", Toast.LENGTH_SHORT).show();
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
    public ViwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de la vista del proveedor desde el archivo XML
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_proveedore_single, parent, false);
        return new ViwHolder(v); // Devolver una nueva instancia de ViwHolder con la vista inflada
    }

    // Clase interna ViwHolder para contener las vistas de cada elemento en el RecyclerView
    public class ViwHolder extends RecyclerView.ViewHolder {
        TextView nombre, departamento, direccion, telefono, email;
        ImageView btn_delete, btn_editar; // Dos botones con IDs diferentes

        public ViwHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreprov);
            departamento = itemView.findViewById(R.id.departamentov);
            direccion = itemView.findViewById(R.id.direccionprov);
            telefono = itemView.findViewById(R.id.telefonopv);
            email = itemView.findViewById(R.id.emailpv);
            btn_delete = itemView.findViewById(R.id.btn_eliminar_proveedor); // ID correcto para eliminar
            btn_editar = itemView.findViewById(R.id.btn_editar_proveedor); // ID correcto para editar
        }
    }

}