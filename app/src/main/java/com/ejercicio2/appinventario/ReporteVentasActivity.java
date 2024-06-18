package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.VentaAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReporteVentasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VentaAdapter ventaAdapter;
    private List<DocumentSnapshot> ventas;
    private FirebaseFirestore db;
    private TextView filterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_ventas);

        recyclerView = findViewById(R.id.recycleViewreporteventas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        ventas = new ArrayList<>();
        ventaAdapter = new VentaAdapter(this, ventas, this::showVentaDetails);
        recyclerView.setAdapter(ventaAdapter);

        filterTextView = findViewById(R.id.text_viewreporteventas);
        filterTextView.setOnClickListener(this::showFilterDialog);

        loadSalesData("Hoy"); // Filtro predeterminado
    }

    private void showFilterDialog(View view) {
        final String[] filterOptions = {"Hoy", "Ayer", "Últimos 7 días", "Últimos 30 días"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el periodo")
                .setItems(filterOptions, (dialog, which) -> {
                    String selectedFilter = filterOptions[which];
                    filterTextView.setText(selectedFilter);
                    loadSalesData(selectedFilter);
                });
        builder.create().show();
    }

    private void loadSalesData(String filter) {
        Query query = db.collection("ventas");

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
                ventas.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ventas.add(document);
                }
                ventaAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showVentaDetails(DocumentSnapshot venta) {
        VentaDetailDialogFragment dialog = VentaDetailDialogFragment.newInstance(venta.getData());
        dialog.show(getSupportFragmentManager(), "VentaDetailDialogFragment");
    }
}
