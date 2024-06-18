package com.ejercicio2.appinventario.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.CompraViewHolder> {

    private Context context;
    private List<DocumentSnapshot> compras;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot compra);
    }

    public CompraAdapter(Context context, List<DocumentSnapshot> compras, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.compras = compras;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_compras, parent, false);
        return new CompraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompraViewHolder holder, int position) {
        DocumentSnapshot compra = compras.get(position);
        holder.tvProveedor.setText((String) compra.get("proveedor"));
        holder.tvFechaCompra.setText((String) compra.get("fecha"));
        holder.tvTotalCompra.setText(String.valueOf(compra.get("totalPagar")));

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(compra));
    }

    @Override
    public int getItemCount() {
        return compras.size();
    }

    static class CompraViewHolder extends RecyclerView.ViewHolder {

        TextView tvProveedor, tvFechaCompra, tvTotalCompra;
        ImageView btbMostrar;

        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProveedor = itemView.findViewById(R.id.tvproveedor);
            tvFechaCompra = itemView.findViewById(R.id.tvFechacompra);
            tvTotalCompra = itemView.findViewById(R.id.tvTotalcompra);
            btbMostrar = itemView.findViewById(R.id.btbmostrar);
        }
    }
}
