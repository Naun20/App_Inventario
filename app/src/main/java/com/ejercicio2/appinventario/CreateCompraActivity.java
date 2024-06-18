package com.ejercicio2.appinventario;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejercicio2.appinventario.adapter.ProductoCompraAdapter;
import com.ejercicio2.appinventario.model.ProductoCompra;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateCompraActivity extends AppCompatActivity implements ProductoCompraAdapter.OnProductoEliminarListener, ProductoCompraAdapter.OnProductoEditarListener {

    private  EditText usuarioEditText, fechaEditText;
    private List<String> productosList;
    private Map<String, String> productosMap; // Para almacenar nombre y categoría
    private EditText productoEditText, categoriaEditText, cantidadEditText;
    private TextView textViewProducto;
    private ImageButton buttonMinus, buttonPlus;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private TextView textViewProveedor;
    private Dialog dialog;
    private ArrayList<String> proveedoresList;
    private ArrayList<ProductoCompra> listaProductos;
    private RecyclerView recyclerView;
    private ProductoCompraAdapter adapter;
    private EditText etProducto, etCategoria, etCantidad, etPrecio;
    private Button btnAgregarProducto;
    private TextView subtotalTextView;
    private double subtotal;
    private ProductoCompra productoActual; // Para manejar la edición
    // Variables para almacenar los valores de subtotal, impuesto y descuento
    private TextView tvSubtotal, tvTotalPagar, tvImpuestoAplicado, tvDescuentoAplicado;

    private Button btnAgregarCompra;
    EditText etImpuesto, etDescuento;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_compra);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener referencia al botón btnAgregarCompra
        btnAgregarCompra = findViewById(R.id.btn_agrearcompra);

        // Obtener referencia al EditText
        usuarioEditText = findViewById(R.id.Usuario);
        fechaEditText = findViewById(R.id.fecha);
        textViewProveedor = findViewById(R.id.text_view);

        // Inicializar las EditTexts
        etImpuesto = findViewById(R.id.impuesto);
        etDescuento = findViewById(R.id.descuento);
        // Producto
        productoEditText = findViewById(R.id.producto);
        categoriaEditText = findViewById(R.id.categoria);
        cantidadEditText = findViewById(R.id.cantidad);
        textViewProducto = findViewById(R.id.text_view_producto);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonPlus = findViewById(R.id.buttonPlus);
        subtotalTextView = findViewById(R.id.tv_subtotal);

        // Hacer que el EditText no sea editable
        usuarioEditText.setFocusable(false);
        usuarioEditText.setClickable(false);
        fechaEditText.setFocusable(false);
        fechaEditText.setClickable(false);
        productoEditText.setFocusable(false);
        productoEditText.setClickable(false);
        categoriaEditText.setFocusable(false);
        categoriaEditText.setClickable(false);
        recyclerView = findViewById(R.id.recycler_view_productoacomprar);

        Button btnAplicarImpuesto = findViewById(R.id.btn_agrearimpuesto);
        EditText etImpuesto = findViewById(R.id.impuesto);
        tvImpuestoAplicado = findViewById(R.id.tv_impuesto);


        Button btnAplicarDescuento = findViewById(R.id.btn_agregardescuento);
        EditText etDescuento = findViewById(R.id.descuento);
        tvDescuentoAplicado = findViewById(R.id.tv_descuento);

        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotalPagar = findViewById(R.id.tv_totalpagar);

        // Configurar listeners
        btnAplicarImpuesto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicarImpuesto(etImpuesto, tvImpuestoAplicado);
            }
        });

        btnAplicarDescuento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicarDescuento(etDescuento, tvDescuentoAplicado);
            }
        });

        btnAgregarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCompra();
            }
        });


        listaProductos = new ArrayList<>();
        adapter = new ProductoCompraAdapter(this, listaProductos, this, this); // Pasar 'this' como listener

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        etProducto = findViewById(R.id.producto);
        etCategoria = findViewById(R.id.categoria);
        etCantidad = findViewById(R.id.cantidad);
        etPrecio = findViewById(R.id.precio);//precio


        btnAgregarProducto = findViewById(R.id.btn_agreagrproducto);//agregar productos

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarOActualizarProducto();
            }
        });


        // Obtener información del usuario y establecerla en el EditText
        getUserInfo();
        // Mostrar la fecha actual en el EditText de fecha
        mostrarFechaActual();

        // Asegurarse de que el ID 'main' exista en el archivo XML
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cargar la lista de proveedores desde Firestore
        cargarProveedores();
        cargarProductos();

        // Configurar el evento de clic en el TextView para mostrar el diálogo
        textViewProveedor.setOnClickListener(v -> mostrarDialogoProveedores());
        textViewProducto.setOnClickListener(v -> mostrarDialogoProducto());
    }

    // Método para guardar la compra completa en la base de datos
    private void guardarCompra() {
        String usuario = usuarioEditText.getText().toString().trim();
        String fecha = fechaEditText.getText().toString().trim();
        String proveedor = textViewProveedor.getText().toString().trim();
        String totalPagar = tvTotalPagar.getText().toString().trim();
        String impuesto = tvImpuestoAplicado.getText().toString().trim();
        String descuento = tvDescuentoAplicado.getText().toString().trim();

        // Verifica que se haya seleccionado un proveedor
        if (proveedor.isEmpty() || proveedor.equals("Seleccione el Proveedor!")) {
            Toast.makeText(getApplicationContext(), "Por favor selecciona un proveedor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear una lista de productos
        List<Map<String, Object>> productosComprados = new ArrayList<>();

        // Iterar sobre cada producto en la lista
        for (ProductoCompra producto : listaProductos) {
            Map<String, Object> productoMap = new HashMap<>();
            productoMap.put("nombreProducto", producto.getNombre());
            productoMap.put("categoriaProducto", producto.getCategoria());
            productoMap.put("cantidadProducto", producto.getCantidad());
            productoMap.put("precioProducto", producto.getPrecio());

            productosComprados.add(productoMap);

            // Actualizar solo el stock del producto en Firestore
            actualizarStockProducto(producto);
        }

        // Crear el mapa de la compra y añadir la lista de productos
        Map<String, Object> compraMap = new HashMap<>();
        compraMap.put("usuario", usuario);
        compraMap.put("fecha", fecha);
        compraMap.put("proveedor", proveedor);
        compraMap.put("productosComprados", productosComprados);
        compraMap.put("subtotal", subtotal);
        compraMap.put("impuesto", impuesto);
        compraMap.put("descuento", descuento);
        compraMap.put("totalPagar", totalPagar);

        // Guardar la compra en la base de datos
        postCompras(compraMap);

        // Limpiar los campos de entrada después de guardar la compra
        limpiarCamposDeEntrada();

        // Mostrar un mensaje de éxito
        Toast.makeText(getApplicationContext(), "Compra Guardada", Toast.LENGTH_SHORT).show();
    }

    private void limpiarCamposDeEntrada() {
        // Limpiar los campos de entrada
        etProducto.setText("");
        etCategoria.setText("");
        etCantidad.setText("");
        etPrecio.setText("");
        etImpuesto.setText("");
        etDescuento.setText("");

        // Limpiar los campos de cálculo
        tvImpuestoAplicado.setText("0.00");
        tvDescuentoAplicado.setText("L.00.00");
        tvSubtotal.setText("L.00.00");
        tvTotalPagar.setText("L.00.00");

        // Restablecer el campo de proveedor
        textViewProveedor.setText("Seleccione el Proveedor!");

        // Limpiar el TextView del subtotal
        TextView subtotalTextView = findViewById(R.id.tv_subtotal);
        subtotalTextView.setText("L.00.00");

        // Limpiar la lista de productos para vaciar el RecyclerView
        listaProductos.clear();
        adapter.notifyDataSetChanged();
    }


    private void actualizarStockProducto(ProductoCompra producto) {
        // Verificar que mFirestore esté inicializado
        if (mFirestore == null) {
            Log.e("UpdateProduct", "Firestore no está inicializado.");
            return;
        }

        // Obtener la referencia al documento del producto en Firestore
        mFirestore.collection("productos")
                .whereEqualTo("nombre", producto.getNombre())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String productoId = document.getId();
                            DocumentReference productoRef = mFirestore.collection("productos").document(productoId);

                            // Obtener el valor actual de stock
                            int stockActual = document.getLong("stock").intValue();

                            // Calcular el nuevo valor de stock
                            int nuevoStock = stockActual + producto.getCantidad();

                            // Actualizar solo el stock en Firestore
                            Map<String, Object> productoActualizado = new HashMap<>();
                            productoActualizado.put("stock", nuevoStock);

                            productoRef.update(productoActualizado)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("UpdateProduct", "Stock del producto actualizado correctamente");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UpdateProduct", "Error al actualizar el stock del producto", e);
                                    });
                        }
                    } else {
                        Log.e("UpdateProduct", "No se encontró el producto: " + producto.getNombre());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateProduct", "Error al obtener el producto", e);
                });
    }
    private void postCompras(Map<String, Object> compraMap) {
        // Agregar la compra a la colección "compra" en Firestore
        mFirestore.collection("compra")
                .add(compraMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Compra creada exitosamente", Toast.LENGTH_SHORT).show();
                        // Si deseas realizar alguna acción adicional después de guardar la compra, puedes hacerlo aquí
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al crear la compra", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarFechaActual() {
        // Obtener la fecha actual en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date()); // Obtener la fecha actual

        // Establecer la fecha actual en el EditText de fecha
        fechaEditText.setText(fechaActual);
    }

    private void getUserInfo() { // Funcion para que aparezca el nombre de usuario
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mFirestore.collection("usuario").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("usuario");

                            if (nombre != null) {
                                usuarioEditText.setText(nombre);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CreateCompraActivity", "Error al obtener información del usuario", e);
                    });
        }
    }

    private void cargarProductos() {
        productosList = new ArrayList<>();
        productosMap = new HashMap<>();

        mFirestore.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String nombreProducto = document.getString("nombre");
                            String categoria = document.getString("categoria");
                            if (nombreProducto != null && categoria != null) {
                                productosList.add(nombreProducto + " - " + categoria);
                                productosMap.put(nombreProducto + " - " + categoria, categoria); // Guardar en el mapa
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateCompraActivity", "Error al cargar productos", e));
    }

    private void mostrarDialogoProducto() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenercompraproducto);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_producto);
        ListView listView = dialog.findViewById(R.id.lista_producto);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productosList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProduct = adapter.getItem(position);
            String[] parts = selectedProduct.split(" - ");
            if (parts.length == 2) {
                String productoNombre = parts[0];
                String categoria = parts[1];

                textViewProducto.setText(productoNombre);
                productoEditText.setText(productoNombre);
                categoriaEditText.setText(categoria);
            }
            dialog.dismiss();
        });
    }

    private void cargarProveedores() { //Logica para cargfar proveedores
        proveedoresList = new ArrayList<>();

        mFirestore.collection("proveedor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String nombreProveedor = document.getString("nombre");
                            if (nombreProveedor != null) {
                                proveedoresList.add(nombreProveedor);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateCompraActivity", "Error al cargar proveedores", e));
    }

    private void mostrarDialogoProveedores() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenerproveedor);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_probeedor);
        ListView listView = dialog.findViewById(R.id.lista_proveedor);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proveedoresList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProveedor = adapter.getItem(position);
            textViewProveedor.setText(selectedProveedor);
            dialog.dismiss();
        });
    }


    private void agregarOActualizarProducto() {
        // Obtener valores de los EditText
        String nombre = etProducto.getText().toString();
        String categoria = etCategoria.getText().toString();
        int cantidad;
        double precio;

        try {
            cantidad = Integer.parseInt(etCantidad.getText().toString());
            precio = Double.parseDouble(etPrecio.getText().toString());
        } catch (NumberFormatException e) {
            // Manejo de errores si el usuario no ha ingresado números válidos
            Toast.makeText(this, "Por favor, ingrese una cantidad y precio válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productoActual == null) {
            // Agregar nuevo producto
            ProductoCompra nuevoProducto = new ProductoCompra(nombre, categoria, cantidad, precio);
            listaProductos.add(nuevoProducto);
        } else {
            // Actualizar producto existente
            productoActual.setNombre(nombre);
            productoActual.setCategoria(categoria);
            productoActual.setCantidad(cantidad);
            productoActual.setPrecio(precio);
        }

        adapter.notifyDataSetChanged();

        // Actualizar el subtotal
        recalcularSubtotal();

        // Limpiar los campos de entrada
        limpiarCampos();

        // Reset producto actual
        productoActual = null;
    }


    private void limpiarCampos() {
        textViewProducto.setText("Seleccione el Producto!");
        etProducto.setText("");
        etCategoria.setText("");
        etCantidad.setText("");
        etPrecio.setText("");
    }



    private void recalcularSubtotal() {
        subtotal = 0;
        for (ProductoCompra producto : listaProductos) {
            subtotal += producto.getPrecio() * producto.getCantidad();
        }
        tvSubtotal.setText(String.format(Locale.getDefault(), "L %.2f", subtotal));
        // Recalcular el total a pagar cuando se actualiza el subtotal
        recalcularTotalPagar();
    }

    private void recalcularTotalPagar() {
        // Obtener el valor del impuesto
        EditText etImpuesto = findViewById(R.id.impuesto);
        String impuestoText = etImpuesto.getText().toString();
        double impuesto = 0;
        if (!impuestoText.isEmpty()) {
            impuesto = Double.parseDouble(impuestoText);
        }

        // Obtener el valor del descuento
        EditText etDescuento = findViewById(R.id.descuento);
        String descuentoText = etDescuento.getText().toString();
        double descuento = 0;
        if (!descuentoText.isEmpty()) {
            descuento = Double.parseDouble(descuentoText);
        }

        // Calcular el total a pagar
        double totalPagar = subtotal + (subtotal * impuesto / 100) - descuento;
        tvTotalPagar.setText(String.format(Locale.getDefault(), "L %.2f", totalPagar));
    }

    private void aplicarImpuesto(EditText etImpuesto, TextView tvImpuestoAplicado) {
        String impuestoText = etImpuesto.getText().toString();
        if (!impuestoText.isEmpty()) {
            tvImpuestoAplicado.setText(impuestoText + "%" );
            recalcularTotalPagar();
        } else {
            Toast.makeText(getApplicationContext(), "Por favor ingresa un valor de impuesto", Toast.LENGTH_SHORT).show();
        }
    }

    private void aplicarDescuento(EditText etDescuento, TextView tvDescuentoAplicado) {
        String descuentoText = etDescuento.getText().toString();
        if (!descuentoText.isEmpty()) {
            tvDescuentoAplicado.setText("L " + descuentoText);
            recalcularTotalPagar();
        } else {
            Toast.makeText(getApplicationContext(), "Por favor ingresa un valor de descuento", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductoEliminado(ProductoCompra producto) {
        subtotal -= producto.getPrecio() * producto.getCantidad();
        tvSubtotal.setText(String.format(Locale.getDefault(), "L %.2f", subtotal));
        recalcularTotalPagar();
    }

    @Override
    public void onProductoEditado(ProductoCompra producto) {
        // Establecer los datos del producto en los campos de entrada para la edición
        etProducto.setText(producto.getNombre());
        etCategoria.setText(producto.getCategoria());
        etCantidad.setText(String.valueOf(producto.getCantidad()));
        etPrecio.setText(String.valueOf(producto.getPrecio()));

        productoActual = producto; // Asignar producto actual para edición
    }
}
