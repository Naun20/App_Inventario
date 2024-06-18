package com.ejercicio2.appinventario;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ejercicio2.appinventario.model.PetUsuario;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjusteActivity extends AppCompatActivity {

    private List<PetUsuario> usuarioList;
    private TextView textViewUsuario;
    private EditText editTextNombre, editTextUsuario, editTextTipo;
    private CheckBox checkBoxClientes, checkBoxTipoCliente, checkBoxUsuarios, checkBoxProveedores,
            checkBoxCategorias, checkBoxProductos, checkBoxCompras, checkBoxVentas,
            checkBoxnegocio, checkBoxreporteventa, checkBoxajuste, checkBoxreportecompra;
    private Button actualizar;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajuste);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewUsuario = findViewById(R.id.text_viewusuario);
        editTextNombre = findViewById(R.id.nombre);
        editTextUsuario = findViewById(R.id.usuario);
        editTextTipo = findViewById(R.id.Tipo);

        checkBoxClientes = findViewById(R.id.checkBoxClientes);
        checkBoxTipoCliente = findViewById(R.id.checkBoxTipoCliente);
        checkBoxUsuarios = findViewById(R.id.checkBoxUsuarios);
        checkBoxProveedores = findViewById(R.id.checkBoxProveedores);
        checkBoxCategorias = findViewById(R.id.checkBoxCategorias);
        checkBoxProductos = findViewById(R.id.checkBoxProductos);
        checkBoxCompras = findViewById(R.id.checkBoxCompras);
        checkBoxVentas = findViewById(R.id.checkBoxVentas);
        checkBoxnegocio = findViewById(R.id.checkBoxNegocio);
        checkBoxreporteventa = findViewById(R.id.checkBoxreporteventa);
        checkBoxajuste = findViewById(R.id.checkBoxajuste);
        checkBoxreportecompra = findViewById(R.id.checkBoxreportecompra);
      //  actualizar = findViewById(R.id.btnGuardarCambios);

        textViewUsuario.setOnClickListener(v -> mostrarDialogoUsuario());

        cargarUsuarios();

    }

    private void cargarUsuarios() {
        usuarioList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("usuario")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            PetUsuario usuario = document.toObject(PetUsuario.class);
                            if (usuario.getNombre() != null && usuario.getTipo_usuario() != null) {
                                usuarioList.add(usuario);
                                Log.d("AjusteActivity", "Usuario añadido: " + usuario.getNombre());
                            } else {
                                Log.d("AjusteActivity", "Datos de usuario nulos encontrados.");
                            }
                        }
                    } else {
                        Log.d("AjusteActivity", "No se encontraron documentos.");
                    }
                })
                .addOnFailureListener(e -> Log.e("AjusteActivity", "Error al cargar usuarios", e));
    }

    private void mostrarDialogoUsuario() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_searchable_spinenerusuario);
        dialog.getWindow().setLayout(1000, 1600);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editex_usuario);
        ListView listView = dialog.findViewById(R.id.lista_usuario);

        List<String> usuarioDisplayList = new ArrayList<>();
        for (PetUsuario usuario : usuarioList) {
            usuarioDisplayList.add(usuario.getNombre() + " - " + usuario.getTipo_usuario());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usuarioDisplayList);
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
            String selectedUsuario = adapter.getItem(position);
            textViewUsuario.setText(selectedUsuario);

            // Mostrar el nombre y el tipo del usuario seleccionado en los campos correspondientes
            String[] parts = selectedUsuario.split(" - ");
            if (parts.length == 2) {
                editTextNombre.setText(parts[0]);
                editTextTipo.setText(parts[1]);

                // Buscar el usuario seleccionado en la lista de usuarios
                for (PetUsuario usuario : usuarioList) {
                    if (usuario.getNombre().equals(parts[0]) && usuario.getTipo_usuario().equals(parts[1])) {
                        // Mostrar el nombre de usuario en el campo correspondiente
                        editTextUsuario.setText(usuario.getUsuario());

                        // Establecer el usuario en la EditText checkBoxUsuarios
                        checkBoxUsuarios.setText(usuario.getUsuario());

                        // Mostrar permisos en los CheckBox
                        Map<String, String> permisos = usuario.getPermisos();
                        if (permisos != null) {
                            checkBoxClientes.setChecked("activo".equals(permisos.get("clientes")));
                            checkBoxTipoCliente.setChecked("activo".equals(permisos.get("tipoCliente")));
                            checkBoxUsuarios.setChecked("activo".equals(permisos.get("usuarios")));
                            checkBoxProveedores.setChecked("activo".equals(permisos.get("proveedores")));
                            checkBoxCategorias.setChecked("activo".equals(permisos.get("categorias")));
                            checkBoxProductos.setChecked("activo".equals(permisos.get("productos")));
                            checkBoxCompras.setChecked("activo".equals(permisos.get("compras")));
                            checkBoxVentas.setChecked("activo".equals(permisos.get("ventas")));
                            checkBoxnegocio.setChecked("activo".equals(permisos.get("negocio")));
                            checkBoxreporteventa.setChecked("activo".equals(permisos.get("reporteventas")));
                            checkBoxajuste.setChecked("activo".equals(permisos.get("ajuste")));
                            checkBoxreportecompra.setChecked("activo".equals(permisos.get("reportecompras")));
                        } else {
                            // Si no hay permisos, desmarcar todos los CheckBox
                            desmarcarTodosLosCheckBox();
                        }
                        break;
                    }
                }
            }

            dialog.dismiss();
        });
    }
    public void guardarCambios(View view) {
        // Obtener el nombre y tipo de usuario del TextView
        String selectedUsuario = textViewUsuario.getText().toString();
        String[] parts = selectedUsuario.split(" - ");
        if (parts.length != 2) {
            // Mostrar un mensaje de error si no se ha seleccionado ningún usuario
            Toast.makeText(this, "Por favor, selecciona un usuario", Toast.LENGTH_SHORT).show();
            return; // Salir del método sin guardar cambios
        }
        String nombreUsuario = parts[0];
        String tipoUsuario = parts[1];

        // Buscar el usuario seleccionado en la lista de usuarios
        for (PetUsuario usuario : usuarioList) {
            if (usuario.getNombre().equals(nombreUsuario) && usuario.getTipo_usuario().equals(tipoUsuario)) {
                // Actualizar los permisos del usuario según las selecciones en los CheckBox
                Map<String, String> permisos = new HashMap<>();
                permisos.put("clientes", checkBoxClientes.isChecked() ? "activo" : "inactivo");
                permisos.put("tipoCliente", checkBoxTipoCliente.isChecked() ? "activo" : "inactivo");
                permisos.put("usuarios", checkBoxUsuarios.isChecked() ? "activo" : "inactivo");
                permisos.put("proveedores", checkBoxProveedores.isChecked() ? "activo" : "inactivo");
                permisos.put("categorias", checkBoxCategorias.isChecked() ? "activo" : "inactivo");
                permisos.put("productos", checkBoxProductos.isChecked() ? "activo" : "inactivo");
                permisos.put("compras", checkBoxCompras.isChecked() ? "activo" : "inactivo");
                permisos.put("ventas", checkBoxVentas.isChecked() ? "activo" : "inactivo");
                permisos.put("negocio", checkBoxnegocio.isChecked() ? "activo" : "inactivo");
                permisos.put("reporteventas", checkBoxreporteventa.isChecked() ? "activo" : "inactivo");
                permisos.put("ajuste", checkBoxajuste.isChecked() ? "activo" : "inactivo");
                permisos.put("reportecompras", checkBoxreportecompra.isChecked() ? "activo" : "inactivo");

                // Actualizar los permisos en el objeto de usuario
                usuario.setPermisos(permisos);

                // Guardar el usuario actualizado en la base de datos
                guardarUsuario(usuario);

                // Mostrar un mensaje de éxito
                Toast.makeText(this, "Cambios guardados para el usuario " + nombreUsuario, Toast.LENGTH_SHORT).show();

                return; // Salir del método después de guardar los cambios
            }
        }

        // Mostrar un mensaje de error si no se encontró el usuario seleccionado
        Toast.makeText(this, "El usuario seleccionado no se encontró en la lista", Toast.LENGTH_SHORT).show();
    }

    private void guardarUsuario(PetUsuario usuario) {
        // Obtén la instancia de FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Accede a la colección "usuario" y actualiza el documento del usuario con su ID
        db.collection("usuario")
                .document(usuario.getId()) // Usar el ID único del usuario
                .set(usuario) // Actualizar el documento con el objeto de usuario modificado
                .addOnSuccessListener(aVoid -> {
                    Log.d("AjusteActivity", "Usuario actualizado correctamente en la base de datos");
                    // Aquí puedes agregar cualquier lógica adicional después de guardar el usuario
                })
                .addOnFailureListener(e -> {
                    Log.e("AjusteActivity", "Error al actualizar el usuario en la base de datos", e);
                    // Maneja el error aquí, si es necesario
                });
    }



    private void desmarcarTodosLosCheckBox() {
        checkBoxClientes.setChecked(false);
        checkBoxTipoCliente.setChecked(false);
        checkBoxUsuarios.setChecked(false);
        checkBoxProveedores.setChecked(false);
        checkBoxCategorias.setChecked(false);
        checkBoxProductos.setChecked(false);
        checkBoxCompras.setChecked(false);
        checkBoxVentas.setChecked(false);
        checkBoxnegocio.setChecked(false);
        checkBoxreporteventa.setChecked(false);
        checkBoxajuste.setChecked(false);
        checkBoxreportecompra.setChecked(false);
    }
    // Método para guardar los cambios en los permisos

    }


