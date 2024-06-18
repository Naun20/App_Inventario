package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.CompraAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReporteComprasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCompras;
    private CompraAdapter compraAdapter;
    private List<DocumentSnapshot> compraList;
    private FirebaseFirestore db;
    private TextView filterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_compras);

        recyclerViewCompras = findViewById(R.id.recycleViewreportecompras);
        recyclerViewCompras.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        compraList = new ArrayList<>();
        compraAdapter = new CompraAdapter(this, compraList, this::showCompraDetails);
        recyclerViewCompras.setAdapter(compraAdapter);

        filterTextView = findViewById(R.id.text_viewreportecompras);
        filterTextView.setOnClickListener(this::showFilterDialog);

        loadPurchaseData("Hoy"); // Filtro predeterminado
    }

    private void showFilterDialog(View view) {
        final String[] filterOptions = {"Hoy", "Ayer", "Últimos 7 días", "Últimos 30 días"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el periodo")
                .setItems(filterOptions, (dialog, which) -> {
                    String selectedFilter = filterOptions[which];
                    filterTextView.setText(selectedFilter);
                    loadPurchaseData(selectedFilter);
                });
        builder.create().show();
    }

    private void loadPurchaseData(String filter) {
        Query query = db.collection("compra");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String today = sdf.format(new Date());
        String startDate = today;

        switch (filter) {
            case "Hoy":
                query = query.whereEqualTo("fecha", today);
                break;
            case "Ayer":
                calendar.add(Calendar.DATE, -1);
                startDate = sdf.format(calendar.getTime());
                query = query.whereEqualTo("fecha", startDate);
                break;
            case "Últimos 7 días":
                calendar.add(Calendar.DATE, -7);
                startDate = sdf.format(calendar.getTime());
                query = query.whereGreaterThanOrEqualTo("fecha", startDate);
                break;
            case "Últimos 30 días":
                calendar.add(Calendar.DATE, -30);
                startDate = sdf.format(calendar.getTime());
                query = query.whereGreaterThanOrEqualTo("fecha", startDate);
                break;
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                compraList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    compraList.add(document);
                }
                compraAdapter.notifyDataSetChanged();
            } else {
                // Manejo de errores
                Toast.makeText(ReporteComprasActivity.this, "Error al obtener datos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCompraDetails(DocumentSnapshot compra) {
        CompraDetailDialogFragment dialog = CompraDetailDialogFragment.newInstance(compra.getData());
        dialog.show(getSupportFragmentManager(), "CompraDetailDialogFragment");
    }
}
