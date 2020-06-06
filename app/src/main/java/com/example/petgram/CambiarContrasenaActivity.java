package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarContrasenaActivity extends AppCompatActivity {

    // Views
    private EditText etContrasenaActual, etContrasena, etRepetirContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);

        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.cambiarContrasenaToolbar);
        toolbar.setTitle("Cambiar contraseña");
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
        etContrasenaActual = findViewById(R.id.contrasenaActualEtCambiarContrasena);
        etContrasena = findViewById(R.id.nuevaContrasenaEtCambiarContrasena);
        etRepetirContrasena = findViewById(R.id.repetirContrasenaEtCambiarContrasena);
    }

    public void clickCambiar(View view) {

        // Recogemos los valores
        String contrasenaAcutal = etContrasenaActual.getText().toString();
        final String contrasena = etContrasena.getText().toString();
        String repetirContrasena = etRepetirContrasena.getText().toString();

        // Comprobamos que todos los campos estén rellenados
        boolean camposRellenados = true;

        if (contrasenaAcutal.isEmpty()) {
            camposRellenados = false;
            etContrasenaActual.setError("Rellena el campo");
        }

        if (contrasena.isEmpty()) {
            camposRellenados = false;
            etContrasena.setError("Rellena el campo");
        }

        if (repetirContrasena.isEmpty()) {
            camposRellenados = false;
            etRepetirContrasena.setError("Rellena el campo");
        }

        // Comprobamos que las contraseñas nuevas coincidan
        boolean coincidenContraseñas = true;

        if(!contrasena.equals(repetirContrasena)){
            coincidenContraseñas = false;
            etRepetirContrasena.setError("No coinciden las contraseñas");
        }

        // Comprobamos que tenga más de 7 digitos
        boolean digitosMinimos = true;

        if(contrasena.length() < 6){
            digitosMinimos = false;
            etContrasena.setError("La contaseña debe contener al menos 6 dígitos");
        }

        // Solo entrará en el siguiente if si todos los campos han sido verificados correctamente
        if (camposRellenados && coincidenContraseñas && digitosMinimos) {
            // Obtenemos una instancia de nuestro usuario en Firebase
            final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

            // Comprobamos que la contraseña introducida sea la correcta
            FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.getEmail(), contrasenaAcutal)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Contraseña actual correcta

                                usuario.updatePassword(contrasena)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Notificamos al usuario que se ha actualizado la contraseña satisfactoriamente
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CambiarContrasenaActivity.this);
                                                    builder.setTitle("Contraseña cambiada");
                                                    builder.setMessage("Se ha cambiado la contraseña correctamente");
                                                    builder.setPositiveButton("Aceptar", null);
                                                    builder.create().show(); // Mostramos el AlertDialog
                                                    etContrasena.setText("");
                                                    etContrasenaActual.setText("");
                                                    etRepetirContrasena.setText("");
                                                }
                                            }
                                        });

                            } else {
                                // Contraseña actual incorrecta

                                etContrasenaActual.setError("La contraseña actual no es correcta");

                            }

                        }
                    });


        }

    }
}
