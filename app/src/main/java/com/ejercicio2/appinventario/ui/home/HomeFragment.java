package com.ejercicio2.appinventario.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.databinding.FragmentHomeBinding;
import com.ejercicio2.appinventario.model.ImageSliderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewPager2 viewPager;
    private ImageSliderAdapter adapter;
    private List<String> imageUrls;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private TextView lblcantidad, lblcantidadcliente, lblcantidadusuarios,
            lblcantidadprobeedores, lblcantidadcategorias, lbltipos;
    private FirebaseAuth mAuth; // Almacena Informacion en la Base de datos
    private FirebaseFirestore mFirestore;
    private HorizontalScrollView horizontalScrollView;
    private Handler handler;
    private Runnable runnable;
    private int scrollAmount = 5; // Cambia la velocidad del desplazamiento

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager = binding.viewPager;
        imageUrls = new ArrayList<>();
        adapter = new ImageSliderAdapter(imageUrls);
        viewPager.setAdapter(adapter);

        lblcantidad = root.findViewById(R.id.cantidaproducto);
        lblcantidadcliente = root.findViewById(R.id.cantidacliente);
        lblcantidadusuarios = root.findViewById(R.id.cantidausuarios);
        lblcantidadprobeedores = root.findViewById(R.id.cantidadproveedores);
        lblcantidadcategorias = root.findViewById(R.id.cantidadcategorias);
        lbltipos = root.findViewById(R.id.cantidadtipos);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener información del usuario y mostrarla en los TextViews
        getproductoInfo();
        getclienteInfo();
        getcusuarioInfo();
        getcproveedoresInfo();
        getccategoriaInfo();
        getctiposInfo();

        loadImagesFromFirebase();
        startAutoSlide();

        // Inicializar el HorizontalScrollView
        horizontalScrollView = root.findViewById(R.id.horizontalScrollView);

        // Crear y comenzar la animación
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int scrollX = horizontalScrollView.getScrollX() + scrollAmount;
                horizontalScrollView.scrollTo(scrollX, 0);

                // Si se alcanza el final, reiniciar
                if (scrollX >= horizontalScrollView.getChildAt(0).getWidth() - horizontalScrollView.getWidth()) {
                    horizontalScrollView.scrollTo(0, 0);
                }

                handler.postDelayed(this, 30); // Ajusta el retraso para cambiar la suavidad
            }
        };

        // Iniciar el desplazamiento
        handler.post(runnable);

        return root;
    }

    private void getctiposInfo() {
        // Obtener una referencia a la colección "productos" en Firestore
        mFirestore.collection("tipocliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totaltipo = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la colección
                    String textoCantidad = " " + totaltipo; // Concatenar el número de productos con el texto "Productos"
                    lbltipos.setText(textoCantidad); // Actualizar el TextView con la cantidad de productos
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                });
    }

    private void getccategoriaInfo() {
        // Obtener una referencia a la colección "productos" en Firestore
        mFirestore.collection("categorias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalcategoria = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la colección
                    String textoCantidad = " " + totalcategoria; // Concatenar el número de productos con el texto "Productos"
                    lblcantidadcategorias.setText(textoCantidad); // Actualizar el TextView con la cantidad de productos
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                });
    }

    private void getcproveedoresInfo() {
        // Obtener una referencia a la colección "productos" en Firestore
        mFirestore.collection("proveedor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalproveedores = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la colección
                    String textoCantidad = " " + totalproveedores; // Concatenar el número de productos con el texto "Productos"
                    lblcantidadprobeedores.setText(textoCantidad); // Actualizar el TextView con la cantidad de productos
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                });
    }

    private void getcusuarioInfo() {
        // Obtener una referencia a la colección "productos" en Firestore
        mFirestore.collection("usuario")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalusuario = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la colección
                    String textoCantidad = " " + totalusuario; // Concatenar el número de productos con el texto "Productos"
                    lblcantidadusuarios.setText(textoCantidad); // Actualizar el TextView con la cantidad de productos
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                });
    }

    private void getclienteInfo() {
        // Obtener una referencia a la colección "productos" en Firestore
        mFirestore.collection("cliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalcliente = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la colección
                    String textoCantidad = " " + totalcliente; // Concatenar el número de productos con el texto "Productos"
                    lblcantidadcliente.setText(textoCantidad); // Actualizar el TextView con la cantidad de productos
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                });
    }

    private void getproductoInfo() {
        // Obtener una referencia a la colección "productos" en Firestore
        mFirestore.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalProductos = queryDocumentSnapshots.size(); // Obtener el número total de documentos en la colección
                    String textoCantidad = " " + totalProductos; // Concatenar el número de productos con el texto "Productos"
                    lblcantidad.setText(textoCantidad); // Actualizar el TextView con la cantidad de productos
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                });
    }

    private void loadImagesFromFirebase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("imagenes_productos");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference fileRef : listResult.getItems()) {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrls.add(uri.toString());
                    adapter.notifyDataSetChanged();
                });
            }
        }).addOnFailureListener(exception -> {
            // Manejar errores aquí
        });
    }

    private void startAutoSlide() {
        sliderHandler.postDelayed(slideRunnable, 3000); // Cambia cada 3 segundos
    }

    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager.getAdapter() != null) {
                int itemCount = viewPager.getAdapter().getItemCount();
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % itemCount;
                viewPager.setCurrentItem(nextItem, true);
            }
            sliderHandler.postDelayed(this, 3000); // Cambia cada 3 segundos
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        sliderHandler.removeCallbacks(slideRunnable);
        // Detener el desplazamiento cuando el fragmento se destruya
        handler.removeCallbacks(runnable);
    }
}
