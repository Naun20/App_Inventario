package com.ejercicio2.appinventario.ui.slideshow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ejercicio2.appinventario.CreateVentaActivity;
import com.ejercicio2.appinventario.R;
import com.ejercicio2.appinventario.databinding.FragmentSlideshowBinding;
import com.ejercicio2.appinventario.model.ProductoVenta;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "SlideshowFragment";
    private LinearLayout btn_ventas;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btn_ventas = root.findViewById(R.id.btn_ventas);
        btn_ventas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateVentaActivity.class);
                startActivity(intent);
            }
        });


        obtenerProductosVendidos();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerProductosVendidos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void obtenerProductosVendidos() {
        db.collection("ventas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ProductoVenta> productosVendidos = new ArrayList<>();
                            Map<String, Integer> ventasPorDia = inicializarVentasPorDia();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("es", "ES"));

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String fecha = document.getString("fecha");
                                if (fecha != null) {
                                    try {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(sdf.parse(fecha));
                                        String dia = dayFormat.format(cal.getTime());

                                        int cantidadVentas = ventasPorDia.getOrDefault(dia, 0) + 1;
                                        ventasPorDia.put(dia, cantidadVentas);
                                    } catch (ParseException e) {
                                        Log.e(TAG, "Error parsing date: " + fecha, e);
                                    }
                                }

                                List<Map<String, Object>> productos = (List<Map<String, Object>>) document.get("productosVendidos");
                                for (Map<String, Object> prod : productos) {
                                    String nombre = (String) prod.get("nombreProducto");
                                    int cantidad = ((Long) prod.get("cantidadProducto")).intValue();
                                    double precio = (Double) prod.get("precioProducto");
                                    double total = (Double) prod.get("totalProducto");
                                    productosVendidos.add(new ProductoVenta(nombre, cantidad, precio, total, 0));
                                }
                            }
                            procesarDatosYMostrarGrafico(productosVendidos);
                            mostrarGraficoBarras(ventasPorDia);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void procesarDatosYMostrarGrafico(List<ProductoVenta> productosVendidos) {
        Collections.sort(productosVendidos, new Comparator<ProductoVenta>() {
            @Override
            public int compare(ProductoVenta p1, ProductoVenta p2) {
                return Integer.compare(p2.getCantidad(), p1.getCantidad());
            }
        });

        List<ProductoVenta> top5Productos = productosVendidos.subList(0, Math.min(productosVendidos.size(), 5));

        PieChart pieChart = binding.pieChart;

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (ProductoVenta producto : top5Productos) {
            entries.add(new PieEntry(producto.getCantidad(), producto.getNombre()));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Top 5 Productos Más Vendidos");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(20f);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);

        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void mostrarGraficoBarras(Map<String, Integer> ventasPorDia) {
        BarChart barChart = binding.barChart;

        // Definir colores para cada día de la semana
        int[] colors = {
                Color.rgb(255, 0, 0),        // Lunes - Rojo
                Color.rgb(255, 165, 0),      // Martes - Naranja
                Color.rgb(255, 255, 0),      // Miércoles - Amarillo
                Color.rgb(0, 128, 0),        // Jueves - Verde
                Color.rgb(0, 0, 255),        // Viernes - Azul
                Color.rgb(75, 0, 130),       // Sábado - Índigo
                Color.rgb(128, 0, 128)       // Domingo - Violeta
        };

        // Ordenar el mapa por día de la semana
        Map<String, Integer> ventasPorDiaOrdenadas = new LinkedHashMap<>();
        for (String dia : inicializarVentasPorDia().keySet()) {
            ventasPorDiaOrdenadas.put(dia, ventasPorDia.getOrDefault(dia, 0));
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> barColors = new ArrayList<>(); // Lista de colores para las barras
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : ventasPorDiaOrdenadas.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            barColors.add(colors[index % colors.length]); // Asignar color según el índice del día
            index++;
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Ventas por Día");
        barDataSet.setColors(barColors);
        barDataSet.setValueTextSize(15f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setLabelRotationAngle(45);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private Map<String, Integer> inicializarVentasPorDia() {
        Map<String, Integer> ventasPorDia = new LinkedHashMap<>();
        ventasPorDia.put("lunes", 0);
        ventasPorDia.put("martes", 0);
        ventasPorDia.put("miércoles", 0);
        ventasPorDia.put("jueves", 0);
        ventasPorDia.put("viernes", 0);
        ventasPorDia.put("sábado", 0);
        ventasPorDia.put("domingo", 0);
        return ventasPorDia;
    }
}
