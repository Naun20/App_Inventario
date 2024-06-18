package com.ejercicio2.appinventario;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText mtxtEmail;
    private EditText mtxtContra;
    private Button mbuttonLogin;
    private TextView lblRegistrar;
    private TextView lblRestablecer;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView logoImage;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        mtxtEmail = findViewById(R.id.txtEmail);
        mtxtContra = findViewById(R.id.txtContrasena);
        mbuttonLogin = findViewById(R.id.btn_login);
        lblRegistrar = findViewById(R.id.lblRegistrar);
        lblRestablecer = findViewById(R.id.lblRestablecerCont);
        logoImage = findViewById(R.id.logoImage);

        cargarImagenNegocio();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> signInWithGoogle());

        lblRegistrar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistroActivity.class)));

        lblRestablecer.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RestablecerContrasenaActivity.class)));

        mbuttonLogin.setOnClickListener(v -> {
            String email = mtxtEmail.getText().toString().trim();
            String password = mtxtContra.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginUser(email, password);
            } else {
                LlenarCampos();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void cargarImagenNegocio() {
        String negocioId = "IN050659";
        progressBar.setVisibility(View.VISIBLE);

        db.collection("negocio").document(negocioId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imagenUrl = documentSnapshot.getString("imagenlogo");

                        if (imagenUrl != null && !imagenUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imagenUrl)
                                    .placeholder(R.drawable.agregar)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(logoImage);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("LoginActivity", "Error al obtener información del negocio", e);
                });
    }

    private void loginUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ToasCorrecto();
                        startActivity(new Intent(LoginActivity.this, MenuPrincipal.class));
                        finish();
                    } else {
                        ToasError();
                    }
                });
    }

    public void ToasCorrecto() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_ok, this.findViewById(R.id.CustomToast1));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public void ToasError() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_error, this.findViewById(R.id.CustomToast2));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public void LlenarCampos() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_toast_llenar, this.findViewById(R.id.CustomToast3));

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginActivity", "Google sign in failed", e);
                ToasError();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Log.d("LoginActivity", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        ToasCorrecto();
                        startActivity(new Intent(LoginActivity.this, MenuPrincipal.class));
                        finish();
                    } else {
                        Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                        ToasError();
                    }
                });
    }
}
