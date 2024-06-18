package com.ejercicio2.appinventario.ui.compras;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ejercicio2.appinventario.CreateCompraActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.model.Producto;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComprasFragment extends Fragment {

    private static final String TAG = "ComprasFragment";
    private LineChart lineChart;
    private HorizontalBarChart barChart;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout btn_compras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compras, container, false);
        lineChart = rootView.findViewById(R.id.lineChart);
        barChart = rootView.findViewById(R.id.horizontalBarChart);
        obtenerCompras();
        obtenerProductos();

        btn_compras = rootView.findViewById(R.id.btn_compras);
        btn_compras.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CreateCompraActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarDatos();
    }

    private void actualizarDatos() {
        obtenerCompras();
        obtenerProductos();
    }

    private void obtenerCompras() {
        db.collection("compra")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> comprasPorMes = new HashMap<>();
                        SimpleDateFormat sdfInput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fecha = document.getString("fecha");
                            try {
                                String mes = sdfOutput.format(sdfInput.parse(fecha));
                                comprasPorMes.put(mes, comprasPorMes.getOrDefault(mes, 0) + 1);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing date: " + e.getMessage());
                            }
                        }
                        mostrarGraficoDeCompras(comprasPorMes);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void obtenerProductos() {
        db.collection("productos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Producto> productos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Producto producto = document.toObject(Producto.class);
                            productos.add(producto);
                        }
                        mostrarProductosConMenosStock(productos);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void mostrarGraficoDeCompras(Map<String, Integer> comprasPorMes) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        List<String> sortedKeys = new ArrayList<>(comprasPorMes.keySet());
        Collections.sort(sortedKeys);

        int index = 0;
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        SimpleDateFormat sdfOutput = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        for (String key : sortedKeys) {
            try {
                String mesLetras = sdfOutput.format(sdfInput.parse(key));
                entries.add(new Entry(index, comprasPorMes.get(key)));
                labels.add(mesLetras);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
            }
            index++;
        }

        // Crear un conjunto de datos para el gráfico de áreas
        LineDataSet lineDataSet = new LineDataSet(entries, "Compras por Mes");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColor(Color.parseColor("#FF5722")); // Establecer color de línea
        lineDataSet.setFillColor(Color.parseColor("#FF673AB7")); // Establecer color de relleno
        lineDataSet.setValueTextSize(20f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setGranularityEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

    private void mostrarProductosConMenosStock(List<Producto> productos) {
        // Ordenar los productos por stock en orden ascendente
        Collections.sort(productos, Comparator.comparingInt(Producto::getStock));

        // Asegurarse de obtener siempre los 5 productos con menos stock
        List<Producto> top5ProductosConMenosStock = new ArrayList<>();
        for (int i = 0; i < Math.min(5, productos.size()); i++) {
            top5ProductosConMenosStock.add(productos.get(i));
        }

        // Configurar el gráfico de barras horizontal
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>(); // Lista para almacenar colores

        // Agregar colores a la lista
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW) ;
        colors.add(Color.MAGENTA);

        int index = 0;
        for (Producto producto : top5ProductosConMenosStock) {
            entries.add(new BarEntry(index, producto.getStock()));
            labels.add(producto.getNombre());
            index++;
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Productos con Menos Stock");

        // Asignar colores a cada barra
        for (int i = 0; i < colors.size(); i++) {
            barDataSet.addColor(colors.get(i));
        }

        barDataSet.setValueTextSize(20f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelRotationAngle(0);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

}
