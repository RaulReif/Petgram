package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.ComentariosAdapter;
import com.example.petgram.models.Comentario;
import com.example.petgram.models.Publicacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ComentariosActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ComentariosAdapter adapter;
    private ArrayList<Comentario> comentarios;

    // Uids
    private String uidPublicacion, uidUsuario;

    private EditText etComentario;

    private Publicacion publicacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        findViews();
        prepareToolbar();

        uidPublicacion = getIntent().getExtras().getString("uidPublicacion");
        uidUsuario = getIntent().getExtras().getString("uidUsuario");

        // Obtenemos los comentarios de la publicacion
        Utils.getUserReference(uidUsuario).child("publicaciones").child(uidPublicacion)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                publicacion = dataSnapshot.getValue(Publicacion.class);
                comentarios = new ArrayList<>(publicacion.getComentarios().values());
                prepareRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findViews() {
        recycler = findViewById(R.id.recyclerComentarios);
        etComentario = findViewById(R.id.comentarioEtComentarios);
    }

    private void prepareToolbar() {
        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarComentarios);
        toolbar.setTitle("Comentarios");
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
    }


    private void prepareRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter = new ComentariosAdapter(this, comentarios);

        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

    }

    public void clickComentar(View view) {
        final String mensaje = etComentario.getText().toString().trim();
        if (!mensaje.isEmpty()) {
            Utils.getMyReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Comentario comentario = new Comentario( mensaje,
                            dataSnapshot.child("nombre").getValue(String.class),
                            dataSnapshot.child("uid").getValue(String.class),
                            dataSnapshot.child("imagen").getValue(String.class),
                            publicacion.getUid(), publicacion.getUidUsuario());

                    Utils.getUserReference(uidUsuario).child("publicaciones")
                            .child(uidPublicacion).child("comentarios").push().setValue(comentario);

                    comentarios.add(comentario);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            etComentario.setText("");
        }
    }
}
