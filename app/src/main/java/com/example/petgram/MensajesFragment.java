package com.example.petgram;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.ConversacionesAdapter;
import com.example.petgram.models.Conversacion;
import com.example.petgram.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class MensajesFragment extends Fragment {

    // Views
    private RecyclerView recycler;
    private ProgressBar progressBar;

    // Adapter
    private ConversacionesAdapter adapter;

    // Listas de usuarios y conversaciones con estos usuarios
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<Conversacion> listaConversaciones;


    public MensajesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensajes, container, false);

        findViews(view);
        prepareRecycler();

        return view;
    }

    private void prepareRecycler() {
        this.listaUsuarios = new ArrayList<>();
        this.listaConversaciones = new ArrayList<>();

        // Montamos nuestro Adapter y LayoutManager y lo asociamos al RecyclerView
        adapter = new ConversacionesAdapter(listaConversaciones,
                listaUsuarios, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

    }

    private void obtenerDatos() {
        progressBar.setVisibility(View.VISIBLE);
        // Limpiamos las listas por si acaso
        listaConversaciones.clear();
        listaUsuarios.clear();
        adapter.notifyDataSetChanged();


        // Obtenemos las conversaciones y las añadimos a nuestra lista
        Utils.getMyReference().child("conversaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Conversacion conversacion = ds.getValue(Conversacion.class);
                    if (ds.hasChild("mensajes")) // Solo añadimos conversaciones con mensajes
                        listaConversaciones.add(conversacion);
                }

                Collections.sort(listaConversaciones); // Ordenamos la lista

                /* Guardamos en un ArrayList los UIDs de las conversaciones para obtener posteriormente
                   sus usuarios */
                ArrayList<String> uids = new ArrayList<>();
                for (Conversacion c : listaConversaciones)
                    uids.add(c.getUid());

        /* Obtenemos una referencia de cada usuario con su id y obtenemos su valor para guardarla
           en nuestra lista de usuarios*/
                for (String uid : uids) {
                    progressBar.setVisibility(View.VISIBLE);

                   Utils.getUserReference(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaUsuarios.add(dataSnapshot.getValue(Usuario.class));
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
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

    private void findViews(View view) {
        this.recycler = view.findViewById(R.id.conversacionesRecyclerView);
        this.progressBar = view.findViewById(R.id.progressBarMensajes);
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerDatos();
    }
}
