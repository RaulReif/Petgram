package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petgram.Configuracion.CamposBD;
import com.example.petgram.Configuracion.Utils;
import com.example.petgram.models.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registro2Activity extends AppCompatActivity {

    // RequestCodes
    private static final int REQUEST_FOTO_GALERIA = 100;
    private static final int REQUEST_FOTO_CAMARA = 200;
    private static final int REQUEST_PERMISOS = 300;

    // Parametro que nos ayudará a saber si venímos de RegistroActivity o no
    private boolean registro;

    // Views
    private CircleImageView imagen;
    private ImageView ivAtras;
    private EditText etLocalidad, etNombre, etEstado;
    private Spinner spTipoAnimal;

    // URI de la foto de perfil
    private Uri fotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);

        findViews();

        /* Comprobamos si estamos dentro de la fase de registro para cambiar la interacción del activity.
         *  Ya que este activity lo reutilizaremos para modificiar los datos del perfil.
         *  Básicamente si nuestro Intent  si contiene un Bundle significará que venimos de Registro
         *  y obligaremos a rellenar todos los campos y ocultaremos el botón de atras. */

        // Venimos de registro
        if (getIntent().getExtras() != null) {
            registro = true;
            ivAtras.setVisibility(View.GONE);
        }
        // No venimos de registro
        else {
            registro = false;
            rellenarCampos();
        }

    }

    private void findViews() {
        imagen = findViewById(R.id.imagenIvRegistro2);
        ivAtras = findViewById(R.id.atrasIvRegistro2);
        spTipoAnimal = findViewById(R.id.tipoAnimalpinnerRegistro2);
        etLocalidad = findViewById(R.id.localidadEtRegistro2);
        etNombre = findViewById(R.id.nombreEtRegistro2);
        etEstado = findViewById(R.id.estadoEtRegistro2);
    }

    public void clickAtras(View view) {
        finish();
    }

    public void clickCambiar(View view) {

        // Creamos nuestro AlertDialog para escoger si queremos obtener la foto a traves de
        // la cámara o la galería
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] opciones = {"Cámara", "Galería"};
        builder.setTitle("Cambiar foto de perfil");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        // CÁMARA
                        // Comprobamos que teneos el permiso de escritura y la cámara en el dispositivo
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                                    || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                                String[] permisos = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA};
                                requestPermissions(permisos, REQUEST_PERMISOS);
                            } else {
                                tomarFoto();
                            }
                        }
                    }
                    break;

                    case 1: {
                        // GALERÍA
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_FOTO_GALERIA);
                    }
                    break;
                }
            }
        });

        builder.create().show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISOS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(this, "Se necesita permisos para realizar esta acción", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_FOTO_GALERIA && data != null && data.getData() != null) {

                imagen.setImageURI(data.getData());
                fotoUri = data.getData();
            } else if (requestCode == REQUEST_FOTO_CAMARA) {
                imagen.setImageURI(fotoUri);
            }

        }

    }

    private void tomarFoto() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        fotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
        startActivityForResult(intent, REQUEST_FOTO_CAMARA);
    }

    public void clickGuardar(View view) {

        // Recogemos los valores
        final String tipo = (String) spTipoAnimal.getSelectedItem();
        final String localidad = etLocalidad.getText().toString().trim();
        final String nombre = etNombre.getText().toString().trim();
        final String estado = etEstado.getText().toString().trim();

        // Comprobamos que haya un tipo de animal seleccionado
        if (!tipo.equals("Escoge un tipo de mascota")) {

            // Comprobamos que los campos de texto no esten vacíos
            if (!localidad.isEmpty() && !nombre.isEmpty() && !estado.isEmpty()) {

                // Comprobamos que nombre del usuario tenga el formato deseado
                if (comprobarNombreUsuario(nombre)) {

                    // Ejecutamos un ProgressDialog
                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setMessage("Actualizando perfil");
                    pd.show();

                    // Almacenamos en un HashMap todos los datos excepto la foto de perfil

                    final HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(CamposBD.NOMBRE, nombre);
                    hashMap.put(CamposBD.ESTADO, estado);
                    hashMap.put(CamposBD.TIPO, tipo);
                    hashMap.put(CamposBD.LOCALIDAD, localidad);


                    // Obtenemos la referencia de donde queremos almacenar la foto de perfil
                    StorageReference storageReference = FirebaseStorage.getInstance().
                            getReference("fotos_perfil/" + FirebaseAuth.getInstance().getCurrentUser()
                                    .getUid());

                    // Guardamos la foto dentro de nuestra referencia si se ha escogido una
                    if (fotoUri != null) {
                        storageReference.putFile(fotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                Uri downloadUri = uriTask.getResult();

                                // Añadimos al HashMap la url donde se ha guardado la imagen
                                hashMap.put(CamposBD.IMAGEN, downloadUri.toString());

                                // Actualizamos los datos de nuestra referencia
                                Utils.getMyReference().updateChildren(hashMap);

                                pd.dismiss();

                                //Redirigimos
                                redirigir();

                            }
                        });
                    } else {
                        // Actualizamos los datos de nuestra referencia
                        Utils.getMyReference().updateChildren(hashMap);
                        pd.dismiss();
                        redirigir();
                    }

                } else {
                    etNombre.setError("Solo se permite utilizar minúsculas, números y barra baja en " +
                            "el nombre de usuario");
                }

            } else
                Toast.makeText(this, "Es necesario rellenear todos los campos", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Selecciona un tipo de animal", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean comprobarNombreUsuario(String nombre) {
        /* Iteramos los caracteres del nombre de usuario y si alguno no esta dentro del rango de
         *  los números correspondientes a las minsculas o la barra baja dentro del código ASCII
         *  devolvemo false. Si se completa la iteración se devolvera true*/
        for (int i = 0; i < nombre.length(); i++)
            if ((nombre.charAt(i) < 97 || nombre.charAt(i) > 122)
                    && (nombre.charAt(i) < 48 || nombre.charAt(i) > 57)
                    && nombre.charAt(i) != 95)
                return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        // No permitiremos ir hacia atras al usuario si venimos de registro
        if (registro) {
            Toast.makeText(this, "Debes guardar todos los datos de perfil", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private void rellenarCampos() {
        Utils.getMyReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtenemos el usuario
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                // Asociamos los datos
                etNombre.setText(usuario.getNombre());
                etEstado.setText(usuario.getEstado());
                etLocalidad.setText(usuario.getLocalidad());
                try {
                    Picasso.get().load(usuario.getImagen()).into(imagen);
                } catch (Exception e) {
                }

                /* Recorremos el spinner para saber en que posición se encuentra el tipo de animal
                 *  que hemos obtenido de la base de datos y cambiarle la selección es ese valor */
                int indice = 0;
                for (int i = 0; i < spTipoAnimal.getCount(); i++) {
                    if (spTipoAnimal.getItemAtPosition(i).equals(usuario.getTipo()))
                        indice = i;
                }
                spTipoAnimal.setSelection(indice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void redirigir() {
        // Refirigiremos a sitios deferentes dependiendo si venimos de registro o no
        if (registro) {
            startActivity(new Intent(Registro2Activity.this, TableroActivity.class));
            finish();
        } else
            finish();
    }

}
