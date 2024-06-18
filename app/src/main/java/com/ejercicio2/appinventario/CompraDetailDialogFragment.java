package com.ejercicio2.appinventario;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompraDetailDialogFragment extends DialogFragment {
    private static final String ARG_COMPRA = "compra";
    private View rootView;
    private Button btnDownloadPdf;

    private TextView tvUsuario, tvFecha, tvProveedor, tvTotalPagar, tvSubtotal, tvImpuesto, tvDescuento;
    private TableLayout tblProductosComprados;
    private ImageView tvImagenlogo;
    private TextView tvNombreNegocio, tvNumeroDoc, tvCai, tvTelefono, tvDescripcion, tvEmail, tvDireccion, tvMensaje;
    private Map<String, Object> compra;

    public static CompraDetailDialogFragment newInstance(Map<String, Object> compra) {
        CompraDetailDialogFragment fragment = new CompraDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COMPRA, (HashMap<String, Object>) compra);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        rootView = inflater.inflate(R.layout.fragment_compra_detail_dialog, null);

        compra = (Map<String, Object>) getArguments().getSerializable(ARG_COMPRA);

        // Inicializar las vistas
        tvUsuario = rootView.findViewById(R.id.tvUsuarioDetail);
        tvFecha = rootView.findViewById(R.id.tvFechaDetail);
        tvProveedor = rootView.findViewById(R.id.tvProveedorDetail);
        tvTotalPagar = rootView.findViewById(R.id.tvTotalPagarDetail);
        tvSubtotal = rootView.findViewById(R.id.tvSubtotalDetail);
        tvImpuesto = rootView.findViewById(R.id.tvImpuestoDetail);
        tvDescuento = rootView.findViewById(R.id.tvDescuentoDetail);
        tblProductosComprados = rootView.findViewById(R.id.tblProductosComprados);

        tvImagenlogo = rootView.findViewById(R.id.imagelogo);
        tvNombreNegocio = rootView.findViewById(R.id.tvNombreNegocioDetail);
        tvNumeroDoc = rootView.findViewById(R.id.tvNumeroDocDetail);
        tvCai = rootView.findViewById(R.id.tvCaiDetail);
        tvTelefono = rootView.findViewById(R.id.tvTelefonoDetail);
        tvDescripcion = rootView.findViewById(R.id.tvDescripcionDetail);
        tvEmail = rootView.findViewById(R.id.tvEmailDetail);
        tvDireccion = rootView.findViewById(R.id.tvDireccionDetail);
        tvMensaje = rootView.findViewById(R.id.tvMensajeDetail);

        // Asignar valores a las vistas
        tvUsuario.setText((String) compra.get("usuario"));
        tvFecha.setText((String) compra.get("fecha"));
        tvProveedor.setText((String) compra.get("proveedor"));
        tvTotalPagar.setText(String.valueOf(compra.get("totalPagar")));
        tvSubtotal.setText(String.valueOf(compra.get("subtotal")));
        tvImpuesto.setText(String.valueOf(compra.get("impuesto")));
        tvDescuento.setText(String.valueOf(compra.get("descuento")));

        // Obtener y mostrar los productos comprados
        List<Map<String, Object>> productosComprados = (List<Map<String, Object>>) compra.get("productosComprados");
        for (Map<String, Object> producto : productosComprados) {
            TableRow row = new TableRow(getContext());

            TextView tvNombreProducto = new TextView(getContext());
            tvNombreProducto.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvNombreProducto.setGravity(Gravity.CENTER);
            tvNombreProducto.setText((String) producto.get("nombreProducto"));
            row.addView(tvNombreProducto);

            TextView tvCategoriaProducto = new TextView(getContext());
            tvCategoriaProducto.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvCategoriaProducto.setGravity(Gravity.CENTER);
            tvCategoriaProducto.setText((String) producto.get("categoriaProducto"));
            row.addView(tvCategoriaProducto);

            TextView tvCantidadProducto = new TextView(getContext());
            tvCantidadProducto.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvCantidadProducto.setGravity(Gravity.CENTER);
            tvCantidadProducto.setText(String.valueOf(producto.get("cantidadProducto")));
            row.addView(tvCantidadProducto);

            TextView tvPrecioProducto = new TextView(getContext());
            tvPrecioProducto.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tvPrecioProducto.setGravity(Gravity.CENTER);
            tvPrecioProducto.setText(String.valueOf(producto.get("precioProducto")));
            row.addView(tvPrecioProducto);

            tblProductosComprados.addView(row);
        }

        // Obtener y mostrar la información del negocio
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("negocio").document("IN050659")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvNombreNegocio.setText(documentSnapshot.getString("nom_empresa"));
                        tvNumeroDoc.setText(documentSnapshot.getString("numerodoc"));
                        tvCai.setText(documentSnapshot.getString("cai"));
                        tvTelefono.setText(documentSnapshot.getString("telefono"));
                        tvDescripcion.setText(documentSnapshot.getString("descripcion"));
                        tvEmail.setText(documentSnapshot.getString("email"));
                        tvDireccion.setText(documentSnapshot.getString("dire_empresa"));
                        tvMensaje.setText(documentSnapshot.getString("mensaje"));

                        String imageUrl = documentSnapshot.getString("imagenlogo");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.caja)
                                    .error(R.drawable.caja)
                                    .into(tvImagenlogo);
                        } else {
                            tvImagenlogo.setImageResource(R.drawable.caja);
                        }
                    }
                });

        // Botón para descargar el PDF
        btnDownloadPdf = rootView.findViewById(R.id.btnDownloadPdf);
        btnDownloadPdf.setOnClickListener(v -> createPdfFromLayout());

        askPermissions();

        builder.setView(rootView)
                .setTitle("Detalle de la Compra")
                .setPositiveButton("Cerrar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void createPdfFromLayout() {
        // Ocultar el botón de descarga antes de crear el PDF
        btnDownloadPdf.setVisibility(View.GONE);

        // Measure and layout the view
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        rootView.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);

        // Create a PDF document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        rootView.draw(canvas);

        document.finishPage(page);

        // Specify the output file
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String proveedor = tvProveedor.getText().toString().replace(" ", "_");
        String fileName = "detalle_compra_" + proveedor + ".pdf";
        File file = new File(downloadsDir, fileName);

        // Write the document content
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(getActivity(), "PDF creado exitosamente!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al crear el PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Mostrar el botón de descarga nuevamente después de crear el PDF
        btnDownloadPdf.setVisibility(View.VISIBLE);
    }


}
