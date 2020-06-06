package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.models.Comentario;
import com.example.petgram.models.Publicacion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActualizarPublicacionActivity extends AppCompatActivity {

    // RequestCodes
    private static final int REQUEST_FOTO_GALERIA = 100;
    private static final int REQUEST_FOTO_CAMARA = 200;
    private static final int REQUEST_PERMISOS = 300;

    // Views
    private ImageView ivFoto;
    private EditText etPieFoto, etLugar;
    private Button btnSubir;

    // Uri de la foto
    private Uri fotoUri;

    // ProgressDialog
    private ProgressDialog pd;

    // Publicación a actualizar y sus comentarios
    private Publicacion publicacion;
    private ArrayList<Comentario> comentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_publicacion);

        findViews();
        prepareToolbar();
        pd = new ProgressDialog(this);
        pd.setMessage("Modificando la publicación...");

        prepareOnClickListeners();

        publicacion = getIntent().getExtras().getParcelable("publicacion");
        comentarios = getIntent().getExtras().getParcelableArrayList("comentarios");

        cargarPublicacion(publicacion);

    }

    private void cargarPublicacion(Publicacion p) {
        etLugar.setText(p.getLugar());
        etPieFoto.setText(p.getPieFoto());
        Picasso.get().load(p.getFoto()).into(ivFoto);
    }

    private void findViews() {
        this.ivFoto = findViewById(R.id.fotoIvActualizarPublicacion);
        this.etPieFoto = findViewById(R.id.pieFotoEtActualizarPublicacion);
        this.etLugar = findViewById(R.id.lugarEtActualizarPublicacion);
        this.btnSubir = findViewById(R.id.subirBtnActualizarPublicacion);
    }

    private void prepareToolbar() {
        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarActualizarPublicacion);
        toolbar.setTitle("Modificar publicación");
        setSupportActionBar(toolbar);

        // Habilitamos el botón de atras en la toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void prepareOnClickListeners() {
        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCambiarFoto();
            }
        });
        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSubir();
            }
        });
    }

    public void clickCambiarFoto() {

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

    public void clickSubir() {
        final String pieFoto = etPieFoto.getText().toString().trim();
        final String lugar = etLugar.getText().toString().trim();
        if (!pieFoto.isEmpty()) {
            if (!lugar.isEmpty()) {
                if (pieFoto.length() > 5) {
                    pd.show();
                    if (fotoUri != null) {
                        // Guardamos la foto en el Firebase Storage
                        StorageReference storageReference = FirebaseStorage.getInstance().
                                getReference("publicaciones/" + FirebaseAuth.getInstance().getCurrentUser()
                                        .getUid() + "/" + System.currentTimeMillis());
                        storageReference.putFile(fotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                Uri downloadUri = uriTask.getResult();

                                final String foto = downloadUri.toString();

                                publicacion.setFoto(foto);
                                publicacion.setPieFoto(pieFoto);
                                publicacion.setLugar(lugar);

                                Utils.getMyReference().child("publicaciones").child(publicacion.getId())
                                        .child("imagen").setValue(foto);

                                Utils.getMyReference().child("publicaciones").child(publicacion.getId())
                                        .child("localidad").setValue(lugar);

                                Utils.getMyReference().child("publicaciones").child(publicacion.getId())
                                        .child("pieFoto").setValue(pieFoto);
                                onBackPressed();

                            }
                        });
                    } else {
                        publicacion.setPieFoto(pieFoto);
                        publicacion.setLugar(lugar);

                        Utils.getMyReference().child("publicaciones").child(publicacion.getId())
                                .child("pieFoto").setValue(pieFoto);

                        Utils.getMyReference().child("publicaciones").child(publicacion.getId())
                                .child("lugar").setValue(lugar);

                        onBackPressed();
                    }
                } else {
                    this.etPieFoto.setError("Tu pie de foto debe ser mayor a 5 caracteres");
                }
            } else {
                etLugar.setError("Escribe el lugar de la publicación");
            }
        } else {
            this.etPieFoto.setError("Debes escribir un pie de foto a tu publicación");
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) { // -1 és el equivalente a la variable RESULT_OK (al estar en un fragment debemos hacerlo así)

            if (requestCode == REQUEST_FOTO_GALERIA && data != null && data.getData() != null) {

                ivFoto.setImageURI(data.getData());
                fotoUri = data.getData();
            } else if (requestCode == REQUEST_FOTO_CAMARA) {
                ivFoto.setImageURI(fotoUri);
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle bundle = new Bundle();
        bundle.putParcelable("publicacion", publicacion);
        bundle.putParcelableArrayList("comentarios", comentarios);
        Intent intent = new Intent(this, PublicacionActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
