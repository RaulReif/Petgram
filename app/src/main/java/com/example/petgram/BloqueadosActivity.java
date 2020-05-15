package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.BloqueadosAdapter;
import com.example.petgram.models.Conversacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BloqueadosActivity extends AppCompatActivity {

    // Lista de conversaciones bloqueadas
    private ArrayList<Conversacion> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueados);

        prepareToolbar();

        Utils.getMyReference().child("conversaciones").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Obtenemos todas las conversaciones que esten bloqueadas
                        lista = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Conversacion conversacion = ds.getValue(Conversacion.class);
                            if (conversacion.isBloqueado())
                                lista.add(conversacion);
                        }
                        // Si el size de la lista es 0 significa que no hay usuarios bloqueados
                        if (lista.size() != 0)
                            prepareRecycler();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void prepareToolbar() {
        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.bloqueadosToolbar);
        toolbar.setTitle("Usuarios bloqueados");
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
        RecyclerView recycler = findViewById(R.id.recyclerBloqueados);

        BloqueadosAdapter adapter = new BloqueadosAdapter(this, lista);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
    }


}
