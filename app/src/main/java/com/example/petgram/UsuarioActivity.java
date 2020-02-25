package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);


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
                Usuario u = dataSnapshot.getValue(Usuario.class);

                // Asociamos valores
                tvNombre.setText(u.getNombre());
                tvLocalidad.setText(u.getLocalidad());
                tvEstado.setText(u.getEstado());

                try {
                    Picasso.get().load(u.getImagen()).into(ivPerfil);
                } catch(IllegalArgumentException e){}
                catch (Exception e){}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
