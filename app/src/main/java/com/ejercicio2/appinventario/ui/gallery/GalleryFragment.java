package com.ejercicio2.appinventario.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ejercicio2.appinventario.AgregarCategoriaActivity;
import com.ejercicio2.appinventario.AgregarClienteActivity;
import com.ejercicio2.appinventario.AgregarProductoActivity;
import com.ejercicio2.appinventario.AgregarProveedorActivity;
import com.ejercicio2.appinventario.AgregarTipoClienteActivity;
import com.ejercicio2.appinventario.AgregarUsuarioActivity;
import com.ejercicio2.appinventario.AjusteActivity;
import com.ejercicio2.appinventario.CreateCompraActivity;
import com.ejercicio2.appinventario.CreateVentaActivity;
import com.ejercicio2.appinventario.NegocioActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.ReporteComprasActivity;
import com.ejercicio2.appinventario.ReporteVentasActivity;
import com.ejercicio2.appinventario.databinding.FragmentGalleryBinding;
import com.ejercicio2.appinventario.model.PermisoUsuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {
    private LinearLayout btn_clientes, btn_producto, btn_compras, btntipo;
    private LinearLayout btn_usuario, btn_categoria, btn_negocio, btn_reportecompras;
    private LinearLayout btn_proveedores,btn_ventas, btn_reporteventa, btn_reportes;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Importante si estás usando Firestore

    private ImageView logoImage;


    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtener referencia al LinearLayout desde la vista inflada
        btn_clientes = root.findViewById(R.id.btn_client);
        btn_usuario = root.findViewById(R.id.btn_user1);
        btn_proveedores = root.findViewById(R.id.btn_proveedor1);
        btn_categoria = root.findViewById(R.id.btn_categoria);
        btn_producto = root.findViewById(R.id.btn_producto);
        btn_compras = root.findViewById(R.id.btn_compras);
        btn_ventas = root.findViewById(R.id.btn_ventas);
        btn_negocio = root.findViewById(R.id.btn_negocio);
        logoImage = root.findViewById(R.id.logoImage);
        btntipo = root.findViewById(R.id.btn_tipocliente);
        btn_reporteventa = root.findViewById(R.id.btnreporteventa);
        btn_reportecompras = root.findViewById(R.id.btnreportecompra);
        btn_reportes = root.findViewById(R.id.btnajuste);


        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        // Asignar un OnClickListener al LinearLayout (botón simulado)
        btn_reportes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para aabrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AjusteActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });


        btn_reportecompras.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), ReporteComprasActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });
        btn_reporteventa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), ReporteVentasActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        btntipo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AgregarTipoClienteActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        btn_negocio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), NegocioActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        btn_ventas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), CreateVentaActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });
        btn_compras.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), CreateCompraActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        btn_producto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AgregarProductoActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });

        btn_categoria.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AgregarCategoriaActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });


        // Asignar un OnClickListener al LinearLayout (botón simulado)

        btn_proveedores.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AgregarProveedorActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });


        // Asignar un OnClickListener al LinearLayout (botón simulado)

        btn_usuario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AgregarUsuarioActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });
        // Asignar un OnClickListener al LinearLayout (botón simulado)
        btn_clientes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir AgregarClienteActivity
                Intent intent = new Intent(getActivity(), AgregarClienteActivity.class);
                startActivity(intent); // Iniciar la actividad
            }
        });



        return root;
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar la referencia al binding cuando se destruye la vista
    }
}
