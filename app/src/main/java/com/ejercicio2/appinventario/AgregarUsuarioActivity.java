package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.PetClienteAdapter;
import com.ejercicio2.appinventario.adapter.PetUsuarioAdapter;
import com.ejercicio2.appinventario.model.PetCliente;
import com.ejercicio2.appinventario.model.PetUsuario;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AgregarUsuarioActivity extends AppCompatActivity {
    SearchView txtBuscar; //BUSCADOR

    private Button btn_addUsuario;
    private RecyclerView mRecycler;
    private PetUsuarioAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_usuario);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recycleViewUsuario);
        btn_addUsuario = findViewById(R.id.btn_addCliente);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("usuario");
        txtBuscar = findViewById(R.id.searchUsuario);//BUSCADOR

        FirestoreRecyclerOptions<PetUsuario> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PetUsuario>()
                        .setQuery(query, PetUsuario.class)
                        .build();

        mAdapter = new PetUsuarioAdapter(firestoreRecyclerOptions, this);
        mRecycler.setAdapter(mAdapter);


        btn_addUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgregarUsuarioActivity.this, CreateUsuarioActivity.class));
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

    }

    private void filterData(String queryText) {
        if (queryText != null && !queryText.isEmpty()) {
            query = mFirestore.collection("usuario").orderBy("nombre").startAt(queryText).endAt(queryText + "\uf8ff");
        } else {
            query = mFirestore.collection("usuario");
        }

        FirestoreRecyclerOptions<PetUsuario> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<PetUsuario>()
                        .setQuery(query, PetUsuario.class)
                        .build();

        mAdapter = new PetUsuarioAdapter(firestoreRecyclerOptions, this);
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
