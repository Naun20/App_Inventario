package com.ejercicio2.appinventario.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.ProductoVenta;
import java.util.List;

public class ProductoVentaAdapter extends RecyclerView.Adapter<ProductoVentaAdapter.ViewHolder> {
    private List<ProductoVenta> productoVentaList;
    private OnEditClickListener onEditClickListener;

    private OnDeleteProductListener deleteProductListener;

    public interface OnEditClickListener {
        void onEditClick(int position);
    }
    public interface OnDeleteProductListener {
        void onDeleteProduct(int position, double total);
    }

    public ProductoVentaAdapter(List<ProductoVenta> productoVentaList, OnEditClickListener onEditClickListener, OnDeleteProductListener onDeleteProductListener) {
        this.productoVentaList = productoVentaList;
        this.onEditClickListener = onEditClickListener;
        this.deleteProductListener = onDeleteProductListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_productoventas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductoVenta productoVenta = productoVentaList.get(position);
        holder.nombre.setText(productoVenta.getNombre());
        holder.cantidad.setText(String.valueOf(productoVenta.getCantidad()));
        holder.precio.setText(String.valueOf(productoVenta.getPrecio()));
        holder.total.setText(String.valueOf(productoVenta.getTotal()));

        holder.btnEditar.setOnClickListener(v -> onEditClickListener.onEditClick(position));

        holder.btnEliminar.setOnClickListener(v -> {
            ProductoVenta producto = productoVentaList.get(position);
            producto.setStock(producto.getStock() + producto.getCantidad());
            double totalEliminado = producto.getPrecio() * producto.getCantidad();
            productoVentaList.remove(position);
            notifyItemRemoved(position);
            // Ajustar el rango de índices notificados
            notifyItemRangeChanged(position, productoVentaList.size());
            deleteProductListener.onDeleteProduct(position, totalEliminado);
        });

    }

    @Override
    public int getItemCount() {
        return productoVentaList.size();
    }

    public void updateProductoVenta(int position, ProductoVenta productoVenta) {
        productoVentaList.set(position, productoVenta);
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, cantidad, precio, total;
        ImageView btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tv_nombreproducto);
            cantidad = itemView.findViewById(R.id.tv_cantidadproducto);
            precio = itemView.findViewById(R.id.tv_precioproducto);
            total = itemView.findViewById(R.id.tv_totalproducto);
            btnEditar = itemView.findViewById(R.id.btn_editarventa);
            btnEliminar = itemView.findViewById(R.id.btn_eliminarventa);
        }
    }

}
