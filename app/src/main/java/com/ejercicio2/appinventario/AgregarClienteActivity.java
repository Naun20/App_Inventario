package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.PetClienteAdapter;
import com.ejercicio2.appinventario.model.PetCliente;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AgregarClienteActivity extends AppCompatActivity {
    SearchView txtBuscar; //BUSCADOR
    Query query;
    Button btn_addCliente;
    RecyclerView mRecycler;
    PetClienteAdapter mAdapter; // Corrección del tipo de adaptador
    FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cliente);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recycleViewCliente);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("cliente");

        FirestoreRecyclerOptions<PetCliente> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PetCliente>()
                        .setQuery(query, PetCliente.class)
                        .build();

        mAdapter = new PetClienteAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);
        txtBuscar = findViewById(R.id.searchViewcliente);//BUSCADOR

        btn_addCliente = findViewById(R.id.btn_addCliente);

        btn_addCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgregarClienteActivity.this, CreateClienteaActivity.class));
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

        // Aplicar los insets de la ventana al contenedor principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

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
            //   mAdapter.stopListening(); // Detener la escucha del adaptador
        }
    }
    //BUSCADOR

    private void filterData(String queryText) {
        if (queryText != null && !queryText.isEmpty()) {
            query = mFirestore.collection("cliente").orderBy("nombre").startAt(queryText).endAt(queryText + "\uf8ff");
        } else {
            query = mFirestore.collection("cliente");
        }

        FirestoreRecyclerOptions<PetCliente> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PetCliente>()
                        .setQuery(query, PetCliente.class)
                        .build();

        mAdapter = new PetClienteAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);
        mAdapter.startListening();
    }

}