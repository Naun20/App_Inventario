package com.ejercicio2.appinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.PetAdapter;
import com.ejercicio2.appinventario.model.Pet;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PMainActivity extends AppCompatActivity {
    Button btn_add;
    RecyclerView mRecycler;
    PetAdapter mAdapter;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pmain);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recycleViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirestore.collection("pet");

        FirestoreRecyclerOptions<Pet> firestoreRecyclerOptions =
                new  FirestoreRecyclerOptions.Builder<Pet>().setQuery(query, Pet.class).build();

        mAdapter = new PetAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

        btn_add = findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PMainActivity.this, CreateClienteActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
      //  mAdapter.stopListening();
    }
}