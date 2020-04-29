package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.PublicacionesPerfilAdapter;
import com.example.petgram.models.Publicacion;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioActivity extends AppCompatActivity {

    private String miUid;

    private ArrayList<Publicacion> listaPublicaciones;

    // Usuario correspondiente del activity
    private Usuario usuario;

    // Views
    private TextView tvNombre, tvLocalidad, tvEstado, tvPublicaciones, tvAmigos;
    private Button btnEnviarSolicitud, btnCancelarSolicitud, btnCancelarAmistad;
    private CircleImageView ivPerfil;
    private Toolbar toolbar;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        this.miUid = FirebaseAuth.getInstance().getUid();
        this.findViews();
        this.bindData(); // Se llama a comprobarAmistad dentro de está función
    }


    private void findViews() {
        this.tvNombre = findViewById(R.id.nombreTvUsuario);
        this.tvLocalidad = findViewById(R.id.localidadTvUsuario);
        this.tvEstado = findViewById(R.id.estadoTvUsuario);
        this.tvPublicaciones = findViewById(R.id.publicacionesTvUsuario);
        this.tvAmigos = findViewById(R.id.amigosTvUsuario);
        this.btnEnviarSolicitud = findViewById(R.id.enviarSolicitudBtnUsuario);
        this.btnCancelarSolicitud = findViewById(R.id.cancelarSolicitudBtnUsuario);
        this.btnCancelarAmistad = findViewById(R.id.cancelarAmistadBtnUsuario);
        this.ivPerfil = findViewById(R.id.imagenIvUsuario);
        this.recycler = findViewById(R.id.publicacionesRecyclerUsuario);
        this.toolbar = findViewById(R.id.toolbarUsuario);
        setSupportActionBar(toolbar);
    }

    private void bindData() { // Se comprueba la amistad al final del metodo
        // Creamos la referencia de los datos del usuario
        DatabaseReference reference = Utils.getUserReference(getIntent().getExtras()
                .getString("uid"));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtenemos la información
                usuario = dataSnapshot.getValue(Usuario.class);

                // Asociamos valores
                tvNombre.setText(usuario.getNombre());
                tvLocalidad.setText(usuario.getLocalidad());
                tvEstado.setText(usuario.getEstado());
                toolbar.setTitle(usuario.getNombre());
                tvAmigos.setText(String.valueOf(obtenerNumeroAmigos()));
                try {
                    Picasso.get().load(usuario.getImagen()).into(ivPerfil);
                } catch (IllegalArgumentException e) {
                } catch (Exception e) {
                }

                obtenerListaPublicaciones();
                tvPublicaciones.setText(String.valueOf(listaPublicaciones.size()));

                // Montamos nuestro recycler
                PublicacionesPerfilAdapter adapter = new PublicacionesPerfilAdapter(UsuarioActivity.this, listaPublicaciones);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(UsuarioActivity.this, 3);
                recycler.setAdapter(adapter);
                recycler.setLayoutManager(layoutManager);
                recycler.setHasFixedSize(true);

                comprobarAmistad();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void obtenerListaPublicaciones() {
        if (usuario.getPublicaciones() != null) {
            listaPublicaciones = new ArrayList<>(usuario.getPublicaciones().values());
        } else {
            listaPublicaciones = new ArrayList<>();
        }
    }

    private int obtenerNumeroAmigos() {
        if (usuario.getAmigos() != null) {
            return usuario.getAmigos().size();
        } else {
            return 0;
        }
    }

    private void comprobarAmistad() {
        Utils.getMyReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("amigos/" + usuario.getUid()))
                    btnCancelarAmistad.setVisibility(View.VISIBLE);
                else if (dataSnapshot.hasChild("solicitudesEnviadas/" + usuario.getUid()))
                    btnCancelarSolicitud.setVisibility(View.VISIBLE);
                else
                    btnEnviarSolicitud.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void enviarSolicitudAmistad(View view) {
        // Agregamos a nuestra lista de solicitudes enviadas
        Utils.getMyReference().child("solicitudesEnviadas").child(usuario.getUid()).setValue(usuario.getUid());

        // Agregamos a la lista del otro usuario de solicitudes recibidas
        Utils.getUserReference(usuario.getUid()).child("solicitudesRecibidas").child(miUid).setValue(miUid);

        btnEnviarSolicitud.setVisibility(View.GONE);
        btnCancelarSolicitud.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Has enviado una solicitud de amistas a " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
    }

    public void cancelarSolicitudAmistad(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Estas seguro que quieres cancelar la solicitud de amistad?");
        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.getMyReference().child("solicitudesEnviadas").child(usuario.getUid()).removeValue();
                Utils.getUserReference(usuario.getUid()).child("solicitudesRecibidas").child(miUid).removeValue();

                btnCancelarSolicitud.setVisibility(View.GONE);
                btnEnviarSolicitud.setVisibility(View.VISIBLE);
            }
        });
        builder.create().show();
    }

    public void cancelarAmistad(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Estas seguro que quieres dejar de ser amigo de " + usuario.getNombre() + "?");
        builder.setNegativeButton("Cancelar", null);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.getMyReference().child("amigos").child(usuario.getUid()).removeValue();
                Utils.getUserReference(usuario.getUid()).child("amigos").child(miUid).removeValue();

                btnCancelarAmistad.setVisibility(View.GONE);
                btnEnviarSolicitud.setVisibility(View.VISIBLE);
            }
        });
        builder.create().show();
    }

    public  void verAmigos(View view) {
        Intent intent = new Intent(this, AmigosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", usuario.getUid());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enviarMensajeUsuarioMenu: {
                // Vamos a chat Activity pasandole el UID del usuario
                Intent intent = new Intent(this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", usuario.getUid());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
