package com.ejercicio2.appinventario;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentaDetailDialogFragment extends DialogFragment {

    private static final String ARG_VENTA = "venta";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView tvUsuario, tvCliente, tvFecha, tvComprobante, tvSerie, tvNumero, tvTotalPagar, tvSubtotal, tvImpuesto, tvDescuento;
    private TableLayout tblProductosVendidos;
    private ImageView tvImagenlogo;
    private TextView tvNombreNegocio, tvNumeroDoc, tvCai, tvTelefono, tvDescripcion, tvEmail, tvDireccion, tvMensaje;
    private Map<String, Object> venta;
    private View rootView;
    private Button btnDownloadPdf;

    public static VentaDetailDialogFragment newInstance(Map<String, Object> venta) {
        VentaDetailDialogFragment fragment = new VentaDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VENTA, (HashMap<String, Object>) venta);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_venta_detail_dialog, null);
        rootView = view;


        venta = (Map<String, Object>) getArguments().getSerializable(ARG_VENTA);

        // Inicializar las vistas
        tvUsuario = rootView.findViewById(R.id.tvUsuarioDetail);
        tvCliente = view.findViewById(R.id.tvClienteDetail);
        tvFecha = view.findViewById(R.id.tvFechaDetail);
        tvComprobante = view.findViewById(R.id.tvComprobanteDetail);
        tvSerie = view.findViewById(R.id.tvSerieDetail);
        tvNumero = view.findViewById(R.id.tvNumeroDetail);
        tvTotalPagar = view.findViewById(R.id.tvTotalPagarDetail);
        tvSubtotal = view.findViewById(R.id.tvSubtotalDetail);
        tvImpuesto = view.findViewById(R.id.tvImpuestoDetail);
        tvDescuento = view.findViewById(R.id.tvDescuentoDetail);
        tblProductosVendidos = view.findViewById(R.id.tblProductosVendidos);

        tvImagenlogo = view.findViewById(R.id.imagelogo);
        tvNombreNegocio = view.findViewById(R.id.tvNombreNegocioDetail);
        tvNumeroDoc = view.findViewById(R.id.tvNumeroDocDetail);
        tvCai = view.findViewById(R.id.tvCaiDetail);
        tvTelefono = view.findViewById(R.id.tvTelefonoDetail);
        tvDescripcion = view.findViewById(R.id.tvDescripcionDetail);
        tvEmail = view.findViewById(R.id.tvEmailDetail);
        tvDireccion = view.findViewById(R.id.tvDireccionDetail);
        tvMensaje = view.findViewById(R.id.tvMensajeDetail);


        // Asignar valores a las vistas
        tvUsuario.setText((String) venta.get("usuario"));
        tvCliente.setText((String) venta.get("cliente"));
        tvFecha.setText((String) venta.get("fecha"));
        tvComprobante.setText((String) venta.get("comprobante"));
        tvSerie.setText((String) venta.get("serie"));
        tvNumero.setText((String) venta.get("numero"));
        tvTotalPagar.setText(String.valueOf(venta.get("totalPagar")));
        tvSubtotal.setText(String.valueOf(venta.get("subtotal")));
        tvImpuesto.setText(String.valueOf(venta.get("impuesto")));
        tvDescuento.setText(String.valueOf(venta.get("descuento")));

        List<Map<String, Object>> productosVendidos = (List<Map<String, Object>>) venta.get("productosVendidos");
        for (Map<String, Object> producto : productosVendidos) {
            TableRow row = new TableRow(getContext());

            TextView tvNombreProducto = new TextView(getContext());
            tvNombreProducto.setText((String) producto.get("nombreProducto"));

            TextView tvCantidadProducto = new TextView(getContext());
            tvCantidadProducto.setText(String.valueOf(producto.get("cantidadProducto")));

            TextView tvPrecioProducto = new TextView(getContext());
            tvPrecioProducto.setText(String.valueOf(producto.get("precioProducto")));

            TextView tvTotalProducto = new TextView(getContext());
            tvTotalProducto.setText(String.valueOf(producto.get("totalProducto")));

            row.addView(tvNombreProducto);
            row.addView(tvCantidadProducto);
            row.addView(tvPrecioProducto);
            row.addView(tvTotalProducto);

            tblProductosVendidos.addView(row);
        }

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

        builder.setView(view)
                .setTitle("Detalle de la Venta")
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
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        rootView.draw(canvas);

        document.finishPage(page);

        // Specify the output file
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String Cliente = tvCliente.getText().toString().replace(" ", "_");
        String fileName = "detalle_compra_" + Cliente + ".pdf";
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