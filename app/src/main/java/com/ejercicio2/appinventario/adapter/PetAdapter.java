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

import com.ejercicio2.appinventario.CreateClienteActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.Pet;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class PetAdapter  extends FirestoreRecyclerAdapter<Pet, PetAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;
    public PetAdapter(@NonNull FirestoreRecyclerOptions<Pet> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Pet pet) {
        final String id = getSnapshots().getSnapshot(i).getId();

        viewHolder.name.setText(pet.getName());
        viewHolder.age.setText(pet.getAge());
        viewHolder.color.setText(pet.getColor());

        viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, CreateClienteActivity.class);
                i.putExtra("id_pet", id);
                activity.startActivity(i);
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePet(id);
            }
        });
    }


    private void deletePet(String id) {
        mFirestore.collection("pet").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Eliminado Correctamente", Toast.LENGTH_SHORT).show();
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

       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pet_single, parent, false);
       return  new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, color;
        ImageView btn_delete, btn_edit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nombrepet);
            age = itemView.findViewById(R.id.edadpet);
            color = itemView.findViewById(R.id.colorpet);

            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editarcliente);

        }
    }
}
