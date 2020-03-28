package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarEmailActivity extends AppCompatActivity {

    // Views
    private EditText etContrasena, etEmail;

    // Fireabase
    private FirebaseUser usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_email);

        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.cambiarEmailToolbar);
        toolbar.setTitle("Cambiar email");
        setSupportActionBar(toolbar);

        // Habilitamos el botón de atras en la toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Asociación de las vistas
        etContrasena = findViewById(R.id.contrasenaEtCambiarEmail);
        etEmail = findViewById(R.id.emailEtCambiarEmail);

        // Obtenemos el usuario de Firebase
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        // Rellenamos el campo email con el email del usuario
        etEmail.setText(usuario.getEmail());
    }

    public void clickCambiar(View view) {

        // Recogemos los valores de los campos
        final String email = etEmail.getText().toString();
        String contrasena = etContrasena.getText().toString();

        // Comprobamos que los campos estén rellenados
        boolean camposRellenados = true;

        if(email.isEmpty()){
            camposRellenados = false;
            etEmail.setError("Debes rellenar este campo");
        }

        if(contrasena.isEmpty()){
            camposRellenados = false;
            etContrasena.setError("Debes rellenar este campo");
        }

        // Comprobamos que sea un formato de email correcto
        boolean emailCorrecto = true;

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailCorrecto = false;
            etEmail.setError("El formato del emaul no és correcto");
        }

        if(camposRellenados && emailCorrecto) {
            // Entraremos si los campos han sido rellenados y tiene un formato de email correcto

            // Verificamos que la contraseña sea correcta
            FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.getEmail(), contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Contraseña actual correcta

                                usuario.updateEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Notificamos al usuario que se ha actualizado la contraseña satisfactoriamente
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CambiarEmailActivity.this);
                                                    builder.setTitle("Email cambiado");
                                                    builder.setMessage("Se ha cambiado el email correctamente.");
                                                    builder.setPositiveButton("Aceptar", null);
                                                    builder.create().show(); // Mostramos el AlertDialog
                                                    etContrasena.setText("");
                                                    etEmail.setText("");
                                                }
                                            }
                                        });

                            } else {
                                // Contraseña actual incorrecta

                                etContrasena.setError("La contraseña no es correcta");

                            }

                        }
                    });
        }
    }
}
