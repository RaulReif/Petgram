package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Views
    private EditText etEmail, etContrasena;
    private ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Asociación de las vistas
        etEmail = findViewById(R.id.emailEtLogin);
        etContrasena = findViewById(R.id.contrasenaEtLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");

        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Comprobamos que haya un usuario logueado para redirigir el Activity
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null)
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        startActivity(new Intent(this, TableroActivity.class));
        finish();
    }

    public void clickEntrar(View view){

        String email = etEmail.getText().toString();
        String contrasena = etContrasena.getText().toString();

        if (!email.isEmpty() && !contrasena.isEmpty()){
            progressDialog.show();
            login(email, contrasena);
        }
        else {
            Toast.makeText(this, "Introduce los datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void clickRegistrarse(View view){
        startActivity(new Intent(this, RegistroActivity.class));
        finish();
    }

    private void login (String email, String contrasena){
        firebaseAuth.signInWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this,    new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            progressDialog.dismiss();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Datos incorrectos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
