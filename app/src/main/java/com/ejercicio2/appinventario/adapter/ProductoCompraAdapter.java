package com.ejercicio2.appinventario.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.ProductoCompra;

import java.util.List;

public class ProductoCompraAdapter extends RecyclerView.Adapter<ProductoCompraAdapter.ViewHolder> {

    private Context context;
    private List<ProductoCompra> listaProductos;
    private OnProductoEliminarListener eliminarListener;
    private OnProductoEditarListener editarListener;

    public interface OnProductoEliminarListener {
        void onProductoEliminado(ProductoCompra producto);
    }

    public interface OnProductoEditarListener {
        void onProductoEditado(ProductoCompra producto);
    }

    public ProductoCompraAdapter(Context context, List<ProductoCompra> listaProductos, OnProductoEliminarListener eliminarListener, OnProductoEditarListener editarListener) {
        this.context = context;
        this.listaProductos = listaProductos;
        this.eliminarListener = eliminarListener;
        this.editarListener = editarListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductoCompra producto = listaProductos.get(position);
        holder.nombreTextView.setText(producto.getNombre());
        holder.categoriaTextView.setText(producto.getCategoria());
        holder.cantidadTextView.setText(String.valueOf(producto.getCantidad()));
        holder.precioTextView.setText(String.valueOf(producto.getPrecio()));

        holder.eliminarButton.setOnClickListener(v -> {
            listaProductos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listaProductos.size());
            eliminarListener.onProductoEliminado(producto);
        });

        holder.editarButton.setOnClickListener(v -> editarListener.onProductoEditado(producto));
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView, categoriaTextView, cantidadTextView, precioTextView;
        ImageView eliminarButton, editarButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.tv_producto);
            categoriaTextView = itemView.findViewById(R.id.tv_categoria);
            cantidadTextView = itemView.findViewById(R.id.tv_cantidad);
            precioTextView = itemView.findViewById(R.id.tv_precio);
            eliminarButton = itemView.findViewById(R.id.btn_eliminarcomprado);
            editarButton = itemView.findViewById(R.id.btn_editarcomprado);
        }
    }
}
