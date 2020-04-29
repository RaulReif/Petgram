package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.UsuariosAdapter;
import com.example.petgram.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AmigosActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private UsuariosAdapter adapter;
    private ArrayList<Usuario> lista;

    // Uid del usuario a mostrar los amigos
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);

        uid = getIntent().getExtras().getString("uid");

        recycler = findViewById(R.id.recyclerAmigos);
        prepareToolbar();
        prepareRecycler();
        cargarUsuariosAmigos();

    }

    private void prepareToolbar() {
        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAmigos);
        toolbar.setTitle("Amigos");
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        lista = new ArrayList<>();
        adapter = new UsuariosAdapter(this, lista);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    private void cargarUsuariosAmigos() {
        Utils.getUserReference(uid).child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator =
                        new GenericTypeIndicator<HashMap<String, String>>() {};

                HashMap<String, String> tempHashMap = dataSnapshot.getValue(genericTypeIndicator);

                ArrayList<String> uids = new ArrayList<>();
                if(tempHashMap != null)
                    uids = new ArrayList<>(tempHashMap.values());

                for(String uid : uids) {
                    Utils.getUserReference(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            lista.add(dataSnapshot.getValue(Usuario.class));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
