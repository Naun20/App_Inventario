package com.ejercicio2.appinventario;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.ejercicio2.appinventario.databinding.ActivityMenu2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuPrincipal extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenu2Binding binding;
    private ImageView imagenUsuario;

    private TextView lblUsuarRegis;
    private TextView lblEmailRegis;
    private FirebaseAuth mAuth; //Almacena Informacion en la Base de datos
    private FirebaseFirestore mFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenu2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMenu.toolbar);
        binding.appBarMenu.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Obtener una referencia a los TextViews en el encabezado del NavigationView
        //NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        lblUsuarRegis = headerView.findViewById(R.id.lblUsuarRegis);
        lblEmailRegis = headerView.findViewById(R.id.lblEmailRegis);
        imagenUsuario = headerView.findViewById(R.id.imageViewUser);



        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener información del usuario y mostrarla en los TextViews
        getUserInfo();
    }

    private void getUserInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mFirestore.collection("usuario").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombre = documentSnapshot.getString("nombre");
                            String email = documentSnapshot.getString("email");
                            String imagenUrl = documentSnapshot.getString("imagen_url"); // Suponiendo que tienes una URL de imagen guardada

                            if (nombre != null) {
                                lblUsuarRegis.setText(nombre);
                            }
                            if (email != null) {
                                lblEmailRegis.setText(email);
                            }
                            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                                // Aquí cargamos la imagen en el ImageView usando una biblioteca como Glide o Picasso
                                Glide.with(this)
                                        .load(imagenUrl)
                                        .placeholder(R.drawable.agregar)
                                        .into(imagenUsuario);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MenuPrincipal", "Error al obtener información del usuario", e);
                    });
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}