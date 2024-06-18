package com.ejercicio2.appinventario.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class VentaAdapter extends RecyclerView.Adapter<VentaAdapter.VentaViewHolder> {

    private Context context;
    private List<DocumentSnapshot> ventas;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot venta);
    }

    public VentaAdapter(Context context, List<DocumentSnapshot> ventas, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.ventas = ventas;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_venta, parent, false);
        return new VentaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentaViewHolder holder, int position) {
        DocumentSnapshot venta = ventas.get(position);
        holder.tvCliente.setText((String) venta.get("cliente"));
        holder.tvFecha.setText((String) venta.get("fecha"));
        holder.tvTotal.setText(String.valueOf(venta.get("totalPagar")));

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(venta));
    }

    @Override
    public int getItemCount() {
        return ventas.size();
    }

    static class VentaViewHolder extends RecyclerView.ViewHolder {

        TextView tvCliente, tvFecha, tvTotal;

        public VentaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
