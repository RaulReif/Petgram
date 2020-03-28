package com.example.petgram;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petgram.adapters.ConversacionAdapter;
import com.example.petgram.models.Conversacion;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MensajesFragment extends Fragment {

    // RecyclerView
    private RecyclerView recycler;

    // Adapter
    private ConversacionAdapter adapter;

    // Listas de usuarios y conversaciones con estos usuarios
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<Conversacion> listaConversaciones;


    public MensajesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensajes, container, false);

        this.listaUsuarios = new ArrayList<>();
        this.listaConversaciones = new ArrayList<>();

        // Obtenemos las referencias de las vistas
        this.recycler = view.findViewById(R.id.conversacionesRecyclerView);
        
        // Montamos nuestro Adapter y LayoutManager y lo asociamos al RecyclerView
        adapter = new ConversacionAdapter(listaConversaciones,
                listaUsuarios, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        // Obtenemos la referencia del nodo de conversaciones de nuestro usuario
        DatabaseReference conversacionesReference = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("conversaciones");


        // Obtenemos las conversaciones de la referencia y las añadimos a nuestra lista
        conversacionesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Conversacion conversacion = ds.getValue(Conversacion.class);
                    listaConversaciones.add(conversacion);
                }

                /* Guardamos en un ArrayList los UIDs de las conversaciones para obtener posteriormente
                   sus usuarios */
                ArrayList<String> uids = new ArrayList<>();
                for (Conversacion c : listaConversaciones)
                    uids.add(c.getUid());

        /* Obtenemos una referencia de cada usuario con su id y obtenemos su valor para guardarla
           en nuestra lista de usuarios*/
                for (String uid : uids) {

                    DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("usuarios").child(uid);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaUsuarios.add(dataSnapshot.getValue(Usuario.class));
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


        adapter.notifyDataSetChanged();

        return view;
    }

}
