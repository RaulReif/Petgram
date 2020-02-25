package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.petgram.Configuracion.CamposBD;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    // Views
    private EditText etEmail, etRepetirEmail, etContrasena, etRepetirContrasena;
    private CheckBox cbCondiciones;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Asociación de las vistas
        etEmail = findViewById(R.id.emailEtRegistro);
        etRepetirEmail = findViewById(R.id.repetirEmailEtRegistro);
        etContrasena = findViewById(R.id.contrasenaEtRegistro);
        etRepetirContrasena = findViewById(R.id.repetirContrasenaEtRegistro);
        cbCondiciones = findViewById(R.id.condicionesCbRegistro);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario...");

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void clickCancelar(View view) {
        finish();
    }

    public void clickRegistrarse(View view) {

        limpiarErrores();

        String email = etEmail.getText().toString().trim();
        String repetirEmail = etRepetirEmail.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String repetirContrasena = etRepetirContrasena.getText().toString().trim();

        // Comprobamos que el CheckBox de las condiciones este seleccionado
        if (cbCondiciones.isChecked()) {
            
            // Comprobamos que esten todos los campos rellenados
            if (!email.isEmpty() && !repetirEmail.isEmpty() && !contrasena.isEmpty()
                    && !repetirContrasena.isEmpty()) {

                // Comprobamos que los emails coinciden
                if (email.equals(repetirEmail)) {

                    // Comprobamos que las contraseñas coinciden
                    if (contrasena.equals(repetirContrasena)) {

                        // Comprobamos que el email tiene un formato correcto
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                            // Comprobamos que la contraseña tiene por lo menos 6 caracteres
                            if (contrasena.length() >= 6) {
                                comprobarCorreo(email, contrasena);
                            } else {
                                etContrasena.setError("La contraseña debe contener al menos 6 caracteres");
                            }
                        } else {
                            etEmail.setError("El formato del email no es correcto");
                        }
                    } else {
                        etRepetirContrasena.setError("La contraseña no coincide");
                    }

                } else {
                    etRepetirEmail.setError("El email no coincide");
                }
            } else {
                if (email.isEmpty())
                    etEmail.setError("Ingresa el email");

                if (repetirEmail.isEmpty())
                    etRepetirEmail.setError("Ingresa el email");

                if (contrasena.isEmpty())
                    etContrasena.setError("Ingresa la contraseña");

                if (repetirContrasena.isEmpty())
                    etRepetirContrasena.setError("Ingresa la contraseña");
            }
        }
        else {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
        }
    }

    private void comprobarCorreo(final String email, final String contrasena) {
        progressDialog.show();
        // Comprueba si el email existe o no
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            crearCuenta(email, contrasena);
                        } else {
                            progressDialog.dismiss();
                            etEmail.setError("El email ya esta en uso");
                        }

                    }
                });


    }

    private void crearCuenta(String email, String contrasena) {
        firebaseAuth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            Toast.makeText(RegistroActivity.this, "Registrado", Toast.LENGTH_SHORT).show();

                            // Esto nos creara unos datos por defecto para el usuario en la base de datos
                            crearUsuarioFireBase();

                            // Enviamos a Registro2Activity un Bundle para que sepa que que hemos llegado desde aquí
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(RegistroActivity.this, Registro2Activity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegistroActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /* Añadimos unos datos por defecto más el correo obtenido
    *  La necesidad de esto es asegurarnos que aunque en el siguiente Activity el usuario aunque
    *  cierre la aplicación tenga unos datos por defecto asociados*/
    private void crearUsuarioFireBase() {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CamposBD.EMAIL, etEmail.getText().toString().trim());
        hashMap.put(CamposBD.NOMBRE, "guest" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put(CamposBD.ESTADO, "Sin estado");
        hashMap.put(CamposBD.TIPO, "No especificado");
        hashMap.put(CamposBD.LOCALIDAD, "No especificada");
        hashMap.put(CamposBD.IMAGEN, "");
        hashMap.put(CamposBD.UID, FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.setValue(hashMap);
    }

    private void limpiarErrores() {
        etEmail.setError(null);
        etRepetirEmail.setError(null);
        etContrasena.setError(null);
        etRepetirContrasena.setError(null);
    }
}
