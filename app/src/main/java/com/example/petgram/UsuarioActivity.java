package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petgram.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioActivity extends AppCompatActivity {

    // Usuario correspondiente del activity
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        // Asosiación de la toolbar
        final Toolbar toolbar = findViewById(R.id.toolbarUsuario);
        setSupportActionBar(toolbar);


        // Asociación de las vistas
        final TextView tvNombre = findViewById(R.id.nombreTvUsuario);
        final TextView tvLocalidad = findViewById(R.id.localidadTvUsuario);
        final TextView tvEstado = findViewById(R.id.estadoTvUsuario);
        final CircleImageView ivPerfil = findViewById(R.id.imagenIvUsuario);

        // Creamos la referencia de los datos del usuario
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(getIntent().getExtras().getString("uid"));

        System.out.println((getIntent().getExtras().getString("uid")));


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

                try {
                    Picasso.get().load(usuario.getImagen()).into(ivPerfil);
                } catch(IllegalArgumentException e){}
                catch (Exception e){}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.enviarMensajeUsuarioMenu:{
                // Vamos a chat Activity pasandole el UID del usuario
                Intent intent = new Intent(this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", usuario.getUid());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void enviarSolicitudAmistad(View view){
        Toast.makeText(this, "Se desea enviar una solicitud de amistad al usuario", Toast.LENGTH_SHORT).show();
    }
}
