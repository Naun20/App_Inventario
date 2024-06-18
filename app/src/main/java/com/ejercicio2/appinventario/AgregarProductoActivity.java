package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.CategoriaAdapter;
import com.ejercicio2.appinventario.adapter.ProductoAdapter;
import com.ejercicio2.appinventario.model.Categoria;
import com.ejercicio2.appinventario.model.Producto;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AgregarProductoActivity extends AppCompatActivity {
     Button btn_addProducto;
    SearchView txtBuscar; //BUSCADOR
    Query query;

    RecyclerView mRecycler;
    ProductoAdapter mAdapter; // Corrección del tipo de adaptador
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_producto);

        txtBuscar = findViewById(R.id.searchViewProducto);//BUSCADOR

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recycleViewProducto);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("productos");


        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query, Producto.class)
                        .build();

        mAdapter = new ProductoAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);

        txtBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                filterData(queryText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {
                filterData(queryText);
                return true;
            }
        });//ASTA AQUI

        btn_addProducto = findViewById(R.id.btn_addProducto);


        btn_addProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgregarProductoActivity.this, CreateProductoActivity.class));
            }

        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterData(String queryText) {
        if (queryText != null && !queryText.isEmpty()) {
            query = mFirestore.collection("productos").orderBy("nombre").startAt(queryText).endAt(queryText + "\uf8ff");
        } else {
            query = mFirestore.collection("productos");
        }


        FirestoreRecyclerOptions<Producto> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Producto>()
                        .setQuery(query, Producto.class)
                        .build();

        mAdapter = new ProductoAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening(); // Iniciar la escucha del adaptador
    }

    }

    @Override
    protected void onStop() {
        super.onStop();
            if (mAdapter != null) {
                // mAdapter.stopListening(); // Detener la escucha del adaptador
            }
    }
}