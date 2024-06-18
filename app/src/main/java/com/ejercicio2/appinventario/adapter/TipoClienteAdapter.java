package com.ejercicio2.appinventario.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.CreateTipoClienteActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.TipoCliente;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TipoClienteAdapter extends RecyclerView.Adapter<TipoClienteAdapter.TipoClienteViewHolder> {

    private List<TipoCliente> tipoClienteList;
    private List<TipoCliente> tipoClienteListFull; // Lista de tipoCliente completa (sin filtrar)


    public TipoClienteAdapter(List<TipoCliente> tipoClienteList) {
        this.tipoClienteList = tipoClienteList;
        this.tipoClienteListFull = new ArrayList<>(tipoClienteList); // Inicializar la lista completa
    }

    @NonNull
    @Override
    public TipoClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_clientetipo_single, parent, false);
        return new TipoClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipoClienteViewHolder holder, int position) {
        TipoCliente tipoCliente = tipoClienteList.get(position);
        holder.tipoTextView.setText(tipoCliente.getTipo());
        holder.descuentoTextView.setText(String.valueOf(tipoCliente.getDescuento()));

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CreateTipoClienteActivity.class);
            intent.putExtra("tipoClienteId", tipoCliente.getId());
            intent.putExtra("tipo", tipoCliente.getTipo());
            intent.putExtra("descuento", tipoCliente.getDescuento());
            v.getContext().startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("tipocliente")
                    .document(tipoCliente.getId())  // Usar el ID del documento para eliminar
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        int newPosition = holder.getAdapterPosition(); // Obtener la posición actual del ViewHolder
                        if (newPosition != RecyclerView.NO_POSITION && newPosition < tipoClienteList.size()) {
                            tipoClienteList.remove(newPosition);
                            notifyItemRemoved(newPosition);
                            notifyItemRangeChanged(newPosition, tipoClienteList.size());
                            Toast.makeText(v.getContext(), "Tipo de cliente eliminado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Error al eliminar tipo de cliente", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return tipoClienteList.size();
    }

    public void setFilter(List<TipoCliente> filteredList) {
        tipoClienteList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }


    public static class TipoClienteViewHolder extends RecyclerView.ViewHolder {

        public TextView tipoTextView;
        public TextView descuentoTextView;
        public ImageView editButton;
        public ImageView deleteButton;

        public TipoClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tipoTextView = itemView.findViewById(R.id.tipo);
            descuentoTextView = itemView.findViewById(R.id.descuento);
            editButton = itemView.findViewById(R.id.btn_editarcomprado);
            deleteButton = itemView.findViewById(R.id.btn_eliminarcomprado);
        }
    }
}
