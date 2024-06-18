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

import com.ejercicio2.appinventario.adapter.PetClienteAdapter;
import com.ejercicio2.appinventario.adapter.PetProveedorAdapter;
import com.ejercicio2.appinventario.model.PetCliente;
import com.ejercicio2.appinventario.model.PetProveedor;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AgregarProveedorActivity extends AppCompatActivity {
    SearchView txtBuscar; //BUSCADOR
    Query query;
    Button btn_addProveedor;
    RecyclerView mRecycler;
    PetProveedorAdapter mAdapter; // Correcció n del tipo de adaptador
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_proveedor);

        btn_addProveedor = findViewById(R.id.btn_addProveedor);
        txtBuscar = findViewById(R.id.searchViewProveedor);//BUSCADOR

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recycleViewProveedor);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("proveedor");

        FirestoreRecyclerOptions<PetProveedor> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PetProveedor>()
                        .setQuery(query, PetProveedor.class)
                        .build();

        mAdapter = new PetProveedorAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);

        btn_addProveedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgregarProveedorActivity.this, CreatePeoveedorActivity.class));
            }

        });
        // Set a listener for the SearchView PARTE DEL BUSCADOR
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterData(String queryText) {
        if (queryText != null && !queryText.isEmpty()) {
            query = mFirestore.collection("proveedor").orderBy("nombre").startAt(queryText).endAt(queryText + "\uf8ff");
        } else {
            query = mFirestore.collection("proveedor");
        }

        FirestoreRecyclerOptions<PetProveedor> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PetProveedor>()
                        .setQuery(query, PetProveedor.class)
                        .build();

        mAdapter = new PetProveedorAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            //  mAdapter.stopListening();
        }
    }

}