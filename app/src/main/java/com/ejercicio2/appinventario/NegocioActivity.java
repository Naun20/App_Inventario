package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ejercicio2.appinventario.adapter.NegocioAdapter;
import com.ejercicio2.appinventario.model.Negocio;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class NegocioActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NegocioAdapter negocioAdapter;
    private List<Negocio> negocioList;
    private FirebaseFirestore db;

    private static final String TAG = "NegocioActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negocio);

        recyclerView = findViewById(R.id.recycleViewNegocio);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        negocioList = new ArrayList<>();
        negocioAdapter = new NegocioAdapter(this, negocioList);
        recyclerView.setAdapter(negocioAdapter);

        db = FirebaseFirestore.getInstance();

        fetchNegocios();

    }

    private void fetchNegocios() {
        CollectionReference negociosRef = db.collection("negocio");
        negociosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Firestore Error: " + error.getMessage());
                    return;
                }

                if (value != null) {
                    Log.d(TAG, "Firestore Data: " + value.getDocuments());
                    negocioList.clear();
                    negocioList.addAll(value.toObjects(Negocio.class));
                    negocioAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "No data found");
                }
            }
        });
    }
}
