package com.ejercicio2.appinventario;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ejercicio2.appinventario.adapter.ProductoVentaAdapter;
import com.ejercicio2.appinventario.adapter.TipoClienteAdapter;
import com.ejercicio2.appinventario.model.PetCliente;
import com.ejercicio2.appinventario.model.Producto;
import com.ejercicio2.appinventario.model.ProductoCompra;
import com.ejercicio2.appinventario.model.ProductoVenta;
import com.ejercicio2.appinventario.model.TipoCliente;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateVentaActivity extends AppCompatActivity implements ProductoVentaAdapter.OnDeleteProductListener, ProductoVentaAdapter.OnEditClickListener {
    private EditText usuarioEditText, fechaEditText, serieEditText, numeroEditText, editTextCantidad,
            editTextPrecio, editTextStock, editTextimpuesto, editTextdescuento;
    private TextView textViewcomprobante, textViewcliente, textViewproducto,
            textViewimpuestoventas,textViewdescuento, tvdescuento, tvsubtotal, tvtotal;
    private Button btnAgregar;
    private Dialog dialog;
    private ArrayList<String> comprobanteList;
    private List<PetCliente> clienteList;
    private ArrayList<Producto> productoList;
    private RecyclerView recyclerView;
    private ProductoVentaAdapter adapter;
    private List<ProductoVenta> productoVentaList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private int editPosition = -1;
    private List<TipoCliente> tipoClienteList;
    private double subtotalVenta = 0;
    private Button btnAgregarVentas;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_venta);


        // Initialize FirebaseAuth and FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Get references to UI elements
        usuarioEditText = findViewById(R.id.Usuario);
        fechaEditText = findViewById(R.id.fecha);
        serieEditText = findViewById(R.id.serie);
        numeroEditText = findViewById(R.id.numero);
        textViewcomprobante = findViewById(R.id.text_viewcomprobante);
        textViewcliente = findViewById(R.id.text_viewcliente);
        textViewproducto = findViewById(R.id.text_view_productoventas);
        editTextStock = findViewById(R.id.stockproducto);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        editTextPrecio = findViewById(R.id.editTextPrecio);
        btnAgregar = findViewById(R.id.btnAgregar);
        recyclerView = findViewById(R.id.recycler_view_productoavender);
       editTextimpuesto = findViewById(R.id.impuestoventa);
        textViewdescuento = findViewById(R.id.text_viewtipocliente);
        editTextdescuento= findViewById(R.id.descuentoventa);
        textViewimpuestoventas= findViewById(R.id.tv_impuestoventas);
        tvdescuento= findViewById(R.id.tv_descuentoventa);
        tvsubtotal= findViewById(R.id.tv_subtotalventaas);
      tvtotal= findViewById(R.id.tv_totalpagarventa);
        btnAgregarVentas= findViewById(R.id.btn_agrearventas);


        btnAgregarVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarVenta();
            }
        });



        // Hacer que el EditText no sea editable
        usuarioEditText.setFocusable(false);
        usuarioEditText.setClickable(false);
        fechaEditText.setFocusable(false);
        fechaEditText.setClickable(false);
        editTextStock.setFocusable(false);
        editTextStock.setClickable(false);
        editTextPrecio.setFocusable(false);
        editTextPrecio.setClickable(false);
       editTextimpuesto.setFocusable(false);
       editTextimpuesto.setClickable(false);

        // Configurar eventos de clic
        textViewcomprobante.setOnClickListener(v -> mostrarDialogoComponente());
        textViewdescuento.setOnClickListener(v -> mostrarDialogoTipoCliente());

        textViewcliente.setOnClickListener(v -> mostrarDialogocliente());
        textViewproducto.setOnClickListener(v -> mostrarDialogoproducto());
        btnAgregar.setOnClickListener(v -> agregarEditarProducto());

        // Inicialización de la lista y el adaptador
        productoVentaList = new ArrayList<>();
        adapter = new ProductoVentaAdapter(productoVentaList, this, this);



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Cargar datos
        getUserInfo();
        cargarimpuesto();
        mostrarFechaActual();
        cargarComprobantes();
        cargarCliente();
        cargarProductos();
        cargarTipoCliente();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    private void guardarVenta() {
        String usuario = usuarioEditText.getText().toString().trim();
        String fecha = fechaEditText.getText().toString().trim();  // Fecha en formato "dd/MM/yyyy"
        String comprobante = textViewcomprobante.getText().toString().trim();
        String serie = serieEditText.getText().toString().trim();
        String numero = numeroEditText.getText().toString().trim();
        String cliente = textViewcliente.getText().toString().trim();
        String totalPagar = tvtotal.getText().toString().trim();
        String impuesto = editTextimpuesto.getText().toString().trim();
        String descuento = editTextdescuento.getText().toString().trim();

        List<Map<String, Object>> productosVendidos = new ArrayList<>();
        for (ProductoVenta producto : productoVentaList) {
            Map<String, Object> productoMap = new HashMap<>();
            productoMap.put("nombreProducto", producto.getNombre());
            productoMap.put("cantidadProducto", producto.getCantidad());
            productoMap.put("precioProducto", producto.getPrecio());
            productoMap.put("totalProducto", producto.getTotal());
            productosVendidos.add(productoMap);
            actualizarStockProductoVenta(producto);
        }

        Map<String, Object> ventaMap = new HashMap<>();
        ventaMap.put("usuario", usuario);
        ventaMap.put("fecha", fecha);
        ventaMap.put("comprobante", comprobante);
        ventaMap.put("serie", serie);
        ventaMap.put("numero", numero);
        ventaMap.put("cliente", cliente);
        ventaMap.put("productosVendidos", productosVendidos);
        ventaMap.put("totalPagar", totalPagar);
        ventaMap.put("subtotal", subtotalVenta);
        ventaMap.put("impuesto", impuesto);
        ventaMap.put("descuento", descuento);

        postVentaFirestore(ventaMap);

        productoVentaList.clear();
        adapter.notifyDataSetChanged();

        textViewcomprobante.setText("Seleccione el Comprobante!");
        textViewcliente.setText("Seleccione el Cliente!");
        textViewdescuento.setText("Seleccione el tipo de descuento!");
        editTextdescuento.setText("");
        tvdescuento.setText("L.00.00");
        serieEditText.setText("");
        numeroEditText.setText("");
        textViewcliente.setText("Seleccione el Cliente!");
        tvtotal.setText("L.00.00");
        editTextdescuento.setText("");
        tvsubtotal.setText("L.00.00");
        TextView tv_descuentoventa = findViewById(R.id.tv_descuentoventa);
        tv_descuentoventa.setText("L 0.00");

        Toast.makeText(getApplicationContext(), "Venta Guardada", Toast.LENGTH_SHORT).show();
    }

    private void actualizarStockProductoVenta(ProductoVenta producto) {
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
                            int nuevoStock = stockActual - producto.getCantidad();

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

    private void postVentaFirestore(Map<String, Object> ventaMap) {
        // Verificar que mFirestore esté inicializado
        if (mFirestore == null) {
            Log.e("postVentaFirestore", "Firestore no está inicializado.");
            return;
        }

        // Agregar la venta a la colección "ventas" en Firestore
        mFirestore.collection("ventas")
                .add(ventaMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("postVentaFirestore", "Venta añadida con ID: " + documentReference.getId());
                    // Opcional: Realizar acciones adicionales después de agregar la venta
                })
                .addOnFailureListener(e -> {
                    Log.e("postVentaFirestore", "Error al añadir la venta", e);
                    // Opcional: Mostrar un mensaje de error al usuario
                    Toast.makeText(getApplicationContext(), "Error al guardar la venta", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarTipoCliente() {
        tipoClienteList = new ArrayList<>();

        mFirestore.collection("tipocliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            TipoCliente tipoCliente = document.toObject(TipoCliente.class);
                            if (tipoCliente.getTipo() != null && tipoCliente.getDescuento() != 0) { // Verificar si el descuento es diferente de 0
                                tipoClienteList.add(tipoCliente);
                                Log.d("CreateVentaActivity", "TipoCliente añadido: " + tipoCliente.getTipo());
                            } else {
                                Log.d("CreateVentaActivity", "Datos de TipoCliente nulos o inválidos encontrados.");
                            }
                        }
                    } else {
                        Log.d("CreateVentaActivity", "No se encontraron documentos.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateVentaActivity", "Error al cargar tipos de cliente", e));
    }

    private void mostrarDialogoTipoCliente() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenertipocliente);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_tipocliente);
        ListView listView = dialog.findViewById(R.id.lista_tipocliente);

        // Convertir la lista de TipoCliente a una lista de cadenas para el adaptador
        List<String> tipoClienteDisplayList = new ArrayList<>();
        for (TipoCliente tipoCliente : tipoClienteList) {
            tipoClienteDisplayList.add(tipoCliente.getTipo() + " - " + tipoCliente.getDescuento() + "L");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tipoClienteDisplayList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTipoCliente = adapter.getItem(position);
            // Parse the selected item to get the tipo and descuento
            String[] parts = selectedTipoCliente.split(" - ");
            String tipo = parts[0];
            String descuento = parts[1].replace("L", "");

            // Set the selected values in the respective views
            textViewdescuento.setText(tipo);
            editTextdescuento.setText(descuento);
            tvdescuento.setText("L " + descuento);

            dialog.dismiss();
        });
    }

    private void cargarimpuesto() {
            String negocioId = "IN050659"; // ID único del negocio

            mFirestore.collection("negocio").document(negocioId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreEmpresa = documentSnapshot.getString("impuesto"); // Obtener el nombre del negocio

                            if (nombreEmpresa != null && !nombreEmpresa.isEmpty()) {
                                editTextimpuesto.setText(nombreEmpresa);
                                textViewimpuestoventas.setText(nombreEmpresa + "%");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LoginActivity", "Error al obtener información del negocio", e);
                    });
        }



    private void agregarEditarProducto() {
        String nombre = textViewproducto.getText().toString().trim();
        String cantidadStr = editTextCantidad.getText().toString().trim();
        String precioStr = editTextPrecio.getText().toString().trim();
        String stockStr = editTextStock.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad, stock;
        double precio;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad, precio o stock inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = cantidad * precio;

        if (editPosition == -1) {
            // Agregar nuevo producto
            if (stock < cantidad) {
                Toast.makeText(this, "Stock insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }
            ProductoVenta nuevoProductoVenta = new ProductoVenta(nombre, cantidad, precio, total, stock - cantidad);
            productoVentaList.add(nuevoProductoVenta);
            adapter.notifyItemInserted(productoVentaList.size() - 1); // Notificar que se insertó un nuevo elemento
        } else {
            // Editar producto existente
            ProductoVenta productoVentaExistente = productoVentaList.get(editPosition);
            int cantidadOriginal = productoVentaExistente.getCantidad();
            int stockAjustado = stock + cantidadOriginal - cantidad;
            if (stockAjustado < 0) {
                Toast.makeText(this, "Stock insuficiente", Toast.LENGTH_SHORT).show();
                return;
            }
            productoVentaExistente.setNombre(nombre);
            productoVentaExistente.setCantidad(cantidad);
            productoVentaExistente.setPrecio(precio);
            productoVentaExistente.setTotal(total);
            productoVentaExistente.setStock(stockAjustado);
            adapter.notifyItemChanged(editPosition); // Notificar que un elemento específico ha cambiado
            editPosition = -1;
        }

        // Limpiar los campos
        textViewproducto.setText("Seleccione el Producto!");
        editTextCantidad.setText("");
        editTextPrecio.setText("");
        editTextStock.setText("");

        // Recalcular el subtotal de la venta
        recalcularSubtotalVenta();

    }

    private void recalcularSubtotalVenta() {
        subtotalVenta = 0;
        for (ProductoVenta productoVenta : productoVentaList) {
            subtotalVenta += productoVenta.getTotal();
        }
        // Actualizar el subtotal en la vista
        tvsubtotal.setText(String.format(Locale.getDefault(), "L %.2f", subtotalVenta));
        // Recalcular el total a pagar cuando se actualiza el subtotal
        recalcularTotalPagarVenta();

    }
    private void recalcularTotalPagarVenta() {
        // Calcular el subtotal
        double subtotal = 0;
        for (ProductoVenta productoVenta : productoVentaList) {
            subtotal += productoVenta.getTotal();
        }

        // Obtener el valor del impuesto
        EditText impuestoEditText = findViewById(R.id.impuestoventa);
        double impuesto = 0;
        if (!impuestoEditText.getText().toString().isEmpty()) {
            impuesto = Double.parseDouble(impuestoEditText.getText().toString());
        }

        // Obtener el valor del descuento (en cantidad absoluta)
        EditText descuentoEditText = findViewById(R.id.descuentoventa);
        double descuento = 0;
        if (!descuentoEditText.getText().toString().isEmpty()) {
            descuento = Double.parseDouble(descuentoEditText.getText().toString());
        }

        // Aplicar el descuento al subtotal
        double subtotalConDescuento = subtotal - descuento;

        // Calcular el impuesto sobre el subtotal con descuento
        double impuestoCalculado = subtotalConDescuento * (impuesto / 100);

        // Calcular el total a pagar sumando el impuesto al subtotal con descuento
        double totalPagar = subtotalConDescuento + impuestoCalculado;

        // Mostrar el resultado en la TextView
        TextView totalPagarTextView = findViewById(R.id.tv_totalpagarventa);
        totalPagarTextView.setText(String.format(Locale.getDefault(), "L %.2f", totalPagar));
    }



    private void cargarProductos() {
        productoList = new ArrayList<>();

        mFirestore.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Producto producto = document.toObject(Producto.class);
                            if (producto.getNombre() != null && producto.getPrecio() != null) {
                                productoList.add(producto); // Agregamos el objeto Producto completo
                                Log.d("CreateVentaActivity", "Producto añadido: " + producto.getNombre());
                            } else {
                                Log.d("CreateVentaActivity", "Datos de producto nulos encontrados.");
                            }
                        }
                    } else {
                        Log.d("CreateVentaActivity", "No se encontraron documentos.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateVentaActivity", "Error al cargar productos", e));
    }

    private void mostrarDialogoproducto() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenercompraproducto);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_producto);
        ListView listView = dialog.findViewById(R.id.lista_producto);

        // Convertir la lista de Producto a una lista de cadenas para el adaptador
        List<String> productoDisplayList = new ArrayList<>();
        for (Producto producto : productoList) {
            if ("Activo".equals(producto.getEstado())) {
                // Agregar nombre, stock y precio del producto en una sola cadena
                productoDisplayList.add(producto.getNombre() + " - Stock: " + producto.getStock() + " - Precio: " + producto.getPrecio());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productoDisplayList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Obtenemos la información del producto seleccionado
            String selectedProductInfo = productoDisplayList.get(position);
            // Extraer el nombre, stock y precio del producto seleccionado
            String[] productInfoParts = selectedProductInfo.split(" - ");
            String selectedProductName = productInfoParts[0];
            String selectedProductStock = productInfoParts[1].split(": ")[1];
            String selectedProductPrice = productInfoParts[2].split(": ")[1];

            // Asignar los valores a los EditText correspondientes
            textViewproducto.setText(selectedProductName);
            EditText stockEditText = findViewById(R.id.stockproducto);
            EditText precioEditText = findViewById(R.id.editTextPrecio);
            stockEditText.setText(selectedProductStock);
            precioEditText.setText(selectedProductPrice);

            dialog.dismiss();
        });
    }

    private void cargarCliente() {
        clienteList = new ArrayList<>();

        mFirestore.collection("cliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            PetCliente cliente = document.toObject(PetCliente.class);
                            if (cliente.getNombre() != null && cliente.getTipo() != null) {
                                clienteList.add(cliente);
                                Log.d("CreateVentaActivity", "Cliente añadido: " + cliente.getNombre());
                            } else {
                                Log.d("CreateVentaActivity", "Datos de cliente nulos encontrados.");
                            }
                        }
                    } else {
                        Log.d("CreateVentaActivity", "No se encontraron documentos.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateVentaActivity", "Error al cargar clientes", e));
    }
    private void mostrarDialogocliente() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenercompracliente);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_cliente);
        ListView listView = dialog.findViewById(R.id.lista_cliente);

        // Convertir la lista de PetCliente a una lista de cadenas para el adaptador
        List<String> clienteDisplayList = new ArrayList<>();
        for (PetCliente cliente : clienteList) {
            clienteDisplayList.add(cliente.getNombre() + " - " + cliente.getTipo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, clienteDisplayList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCliente = adapter.getItem(position);
            textViewcliente.setText(selectedCliente);
            dialog.dismiss();
        });
    }



    private void cargarComprobantes() {
        comprobanteList = new ArrayList<>();

        mFirestore.collection("comprobante")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String comprobante = document.getString("documento");
                            if (comprobante != null) {
                                comprobanteList.add(comprobante);
                                Log.d("CreateVentaActivity", "Comprobante añadido: " + comprobante);
                            } else {
                                Log.d("CreateVentaActivity", "Comprobante nulo encontrado.");
                            }
                        }
                    } else {
                        Log.d("CreateVentaActivity", "No se encontraron documentos.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateVentaActivity", "Error al cargar comprobantes", e));
    }

    private void mostrarDialogoComponente() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenercomprobante);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_comprobante);
        ListView listView = dialog.findViewById(R.id.lista_comprobante);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comprobanteList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedComprobante = adapter.getItem(position);
            textViewcomprobante.setText(selectedComprobante);

            // Aquí obtendremos la serie y el número del comprobante seleccionado
            obtenerSerieYNumero(selectedComprobante);

            dialog.dismiss();
        });
    }

    private void obtenerSerieYNumero(String comprobanteSeleccionado) {
        // Consultamos la base de datos para obtener la serie y el número del comprobante seleccionado
        mFirestore.collection("comprobante")
                .whereEqualTo("documento", comprobanteSeleccionado)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Suponiendo que solo haya un documento que coincida con el comprobante seleccionado
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String serie = document.getString("serie");
                        String numero = document.getString("numero");

                        // Mostramos la serie y el número en los EditText correspondientes
                        serieEditText.setText(serie);
                        numeroEditText.setText(numero);
                    }
                })
                .addOnFailureListener(e -> Log.e("CreateVentaActivity", "Error al obtener serie y número", e));
    }

    private void mostrarFechaActual() {
        // Obtener la fecha actual en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date()); // Obtener la fecha actual

        // Establecer la fecha actual en el EditText de fecha
        fechaEditText.setText(fechaActual);
    }

    private void getUserInfo() {
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


    @Override
    public void onDeleteProduct(int position, double total) {
        // Restar el total eliminado del subtotal
        subtotalVenta -= total;
        // Actualizar el TextView del subtotal
        tvsubtotal.setText(String.format(Locale.getDefault(), "L %.2f", subtotalVenta));
        // Recalcular el total a pagar cuando se actualiza el subtotal
        recalcularTotalPagarVenta();
    }



    @Override
    public void onEditClick(int position) {
        editPosition = position;
        ProductoVenta productoVenta = productoVentaList.get(position);
        textViewproducto.setText(productoVenta.getNombre());
        editTextCantidad.setText(String.valueOf(productoVenta.getCantidad()));
        editTextPrecio.setText(String.valueOf(productoVenta.getPrecio()));
        editTextStock.setText(String.valueOf(productoVenta.getStock()));
    }
}

