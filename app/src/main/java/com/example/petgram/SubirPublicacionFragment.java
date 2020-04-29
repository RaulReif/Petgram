package com.example.petgram;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.models.Publicacion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubirPublicacionFragment extends Fragment {


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

    // Referencia del usuario
    private String nombreUsuario;

    // ProgressDialog
    private ProgressDialog pd;

    public SubirPublicacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subir_publicacion, container, false);

        findViews(view);

        // Obtenemos el nombre del usuario
        Utils.getMyReference().child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreUsuario = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pd = new ProgressDialog(getContext());
        pd.setMessage("Subiendo la publicación...");

        prepareOnClickListeners();

        return view;
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

    private void findViews(View view) {
        this.ivFoto = view.findViewById(R.id.fotoIvSubirPublicacion);
        this.etPieFoto = view.findViewById(R.id.pieFotoEtSubirPublicacion);
        this.etLugar = view.findViewById(R.id.lugarEtSubirPublicacion);
        this.btnSubir = view.findViewById(R.id.subirBtnSubirPublicacion);
    }

    public void clickCambiarFoto() {

        // Creamos nuestro AlertDialog para escoger si queremos obtener la foto a traves de
        // la cámara o la galería
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                            if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                                    || getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
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

        fotoUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
                if (pieFoto.length() > 25) {
                    if (this.fotoUri != null) {
                        pd.show();
                        // Obtenemos la referencia de las publicaciones del usuario
                        final DatabaseReference referenciaPublicaciones = Utils.getMyReference().child("publicaciones");

                        // Obtenemos algunos de los datos necesarios para crear la publicación
                        final long timestamp = System.currentTimeMillis();
                        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                                String foto = downloadUri.toString();

                                DatabaseReference referrenciaPublicacion = referenciaPublicaciones.push();

                                Publicacion publicacion = new Publicacion(uid, foto, pieFoto,
                                        nombreUsuario, timestamp, lugar, referrenciaPublicacion.getKey());

                                referrenciaPublicacion.setValue(publicacion);

                                pd.dismiss();
                                startActivity(new Intent(getContext(), TableroActivity.class));
                            }
                        });


                    } else {
                        Toast.makeText(getContext(), "Escoge o realiza una foto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    this.etPieFoto.setError("Tu pie de foto debe ser mayor a 25 caracteres");
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
                Toast.makeText(getContext(), "Se necesita permisos para realizar esta acción", Toast.LENGTH_SHORT).show();
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

}
