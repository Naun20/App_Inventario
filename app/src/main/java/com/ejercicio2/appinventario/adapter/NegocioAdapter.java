package com.ejercicio2.appinventario.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ejercicio2.appinventario.CreateNegocioActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.Negocio;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import android.view.View;
import android.widget.ProgressBar;

public class NegocioAdapter extends RecyclerView.Adapter<NegocioAdapter.NegocioViewHolder> {

    private Context context;
    private List<Negocio> negocioList;

    public NegocioAdapter(Context context, List<Negocio> negocioList) {
        this.context = context;
        this.negocioList = negocioList;
    }

    @NonNull
    @Override
    public NegocioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_negocio, parent, false);
        return new NegocioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NegocioViewHolder holder, int position) {
        Negocio negocio = negocioList.get(position);

        // Mostrar nombre de campo y valor correspondiente
        holder.bindData("Nombre de la Empresa: ", negocio.getNom_empresa(), holder.nombrenegocio);
        holder.bindData("Número de Documento: ", negocio.getNumerodoc(), holder.numerodoc);
        holder.bindData("Telefono: ", negocio.getTelefono(), holder.telefononegocio);
        holder.bindData("Impuesto: ", negocio.getImpuesto(), holder.impuesto);
        holder.bindData("Email negocio: ", negocio.getEmail(), holder.emailnegocio);
        holder.bindData("Dirección negocio: ", negocio.getDire_empresa(), holder.direcionnegocio);
        holder.bindData("Cai: ", negocio.getCai(), holder.cai);
        holder.bindData("Descripción: ", negocio.getDescripcion(), holder.descripcionnegocio);
        holder.bindData("Mensaje: ", negocio.getMensaje(), holder.mensaje);

        // Muestra el ProgressBar mientras se carga la imagen
        holder.progressBar.setVisibility(View.VISIBLE);

        // Cargar imagen utilizando Picasso (o Glide)
        if (negocio.getImagenlogo() != null && !negocio.getImagenlogo().isEmpty()) {
            Picasso.get().load(negocio.getImagenlogo())
                    .into(holder.photonegocio, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Oculta el ProgressBar cuando la imagen se carga correctamente
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Oculta el ProgressBar si hay un error al cargar la imagen
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            // Si la URL de la imagen está vacía, dejar photonegocio vacío y ocultar el ProgressBar
            holder.photonegocio.setImageDrawable(null);
            holder.progressBar.setVisibility(View.GONE);
        }

        // Añadir OnClickListener al botón de editar
        holder.btnEditarUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateNegocioActivity.class);
            intent.putExtra("negocio", negocio);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return negocioList.size();
    }

    public void updateNegocioList(List<Negocio> newNegocioList) {
        this.negocioList = newNegocioList;
        notifyDataSetChanged();
    }

    public static class NegocioViewHolder extends RecyclerView.ViewHolder {
        TextView nombrenegocio, numerodoc, telefononegocio, impuesto, emailnegocio, direcionnegocio, cai, descripcionnegocio, mensaje;
        ImageView photonegocio, btnEditarUsuario;
        ProgressBar progressBar;

        public NegocioViewHolder(@NonNull View itemView) {
            super(itemView);

            nombrenegocio = itemView.findViewById(R.id.nombrenegocio);
            numerodoc = itemView.findViewById(R.id.numerodoc);
            telefononegocio = itemView.findViewById(R.id.telefononegocio);
            impuesto = itemView.findViewById(R.id.impuesto);
            emailnegocio = itemView.findViewById(R.id.emailnegocio);
            direcionnegocio = itemView.findViewById(R.id.direcionnegocio);
            cai = itemView.findViewById(R.id.cai);
            descripcionnegocio = itemView.findViewById(R.id.descripcionnegocio);
            mensaje = itemView.findViewById(R.id.mensaje);
            photonegocio = itemView.findViewById(R.id.fotonegocio1);
            btnEditarUsuario = itemView.findViewById(R.id.btn_editar_negocio);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        // Método para establecer el nombre de campo y su valor correspondiente en un TextView
        public void bindData(String fieldName, String value, TextView textView) {
            String fieldText = fieldName + value;
            textView.setText(fieldText);
        }
    }
}