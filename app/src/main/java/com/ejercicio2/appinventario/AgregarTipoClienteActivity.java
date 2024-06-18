package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.TipoClienteAdapter;
import com.ejercicio2.appinventario.model.TipoCliente;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AgregarTipoClienteActivity extends AppCompatActivity {
    private Button btn_addtipoCliente;
    private RecyclerView recyclerView;
    private TipoClienteAdapter adapter;
    private List<TipoCliente> tipoClienteList;
    private FirebaseFirestore mfirestore;
    SearchView txtBuscar; //BUSCADOR
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tipo_cliente);

        btn_addtipoCliente = findViewById(R.id.btn_addtipoCliente);
        recyclerView = findViewById(R.id.recycleViewtipoCliente);

        // Inicializar Firestore
        mfirestore = FirebaseFirestore.getInstance();
        txtBuscar = findViewById(R.id.searchViewtipocliente);//BUSCADOR
        // Set a listener for the SearchView PARTE DEL BUSCADOR
        // Set a listener for the SearchView
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
        });


        // Configurar RecyclerView
        tipoClienteList = new ArrayList<>();
        adapter = new TipoClienteAdapter(tipoClienteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Cargar datos de Firestore
        mfirestore.collection("tipocliente").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                tipoClienteList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    TipoCliente tipoCliente = doc.toObject(TipoCliente.class);
                    tipoCliente.setId(doc.getId()); // Asegúrate de establecer el ID del documento
                    tipoClienteList.add(tipoCliente);
                }
                adapter.notifyDataSetChanged();
            }
        });

        btn_addtipoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AgregarTipoClienteActivity.this, CreateTipoClienteActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void filterData(String queryText) {
        List<TipoCliente> filteredList = new ArrayList<>();
        for (TipoCliente tipoCliente : tipoClienteList) {
            // Buscar por el nombre del tipo de cliente
            String tipoClienteNombre = tipoCliente.getTipo().toLowerCase();
            if (tipoClienteNombre.contains(queryText.toLowerCase())) {
                filteredList.add(tipoCliente);
            }
        }
        adapter.setFilter(filteredList); // Actualizar el adaptador con la lista filtrada
    }


}
