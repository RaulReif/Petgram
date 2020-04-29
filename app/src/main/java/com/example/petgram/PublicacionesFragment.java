package com.example.petgram;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.PublicacionesAdapter;
import com.example.petgram.models.Publicacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class PublicacionesFragment extends Fragment {

    private RecyclerView recycler;
    private PublicacionesAdapter adapter;
    private ArrayList<Publicacion> publicaciones;

    public PublicacionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicaciones, container, false);

        recycler = view.findViewById(R.id.publicacionesRecycler);

        prepareRecycler();

        // Obtenemos los uids de nuestros amigos
        Utils.getMyReference().child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator =
                        new GenericTypeIndicator<HashMap<String, String>>() {
                        };

                ArrayList<String> uids =
                        new ArrayList<String>(dataSnapshot.getValue(genericTypeIndicator).values());

                // Obtenemos las públicaciones de nuestros amigos y las añadimos a la lista
                obtenerPublicaciones(uids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void obtenerPublicaciones(ArrayList<String> uids) {
        for (String uid : uids) {
            Utils.getUserReference(uid).child("publicaciones")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<HashMap<String, Publicacion>> genericTypeIndicator =
                                    new GenericTypeIndicator<HashMap<String, Publicacion>>() {
                                    };
                            HashMap<String, Publicacion> hashMap = dataSnapshot.getValue(genericTypeIndicator);


                            ArrayList<Publicacion> publicacionesAux = new ArrayList<>();
                            if (hashMap != null)
                                publicacionesAux = new ArrayList<>(hashMap.values());

                            for (Publicacion p : publicacionesAux)
                                publicaciones.add(p);


                            Collections.sort(publicaciones);
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void prepareRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, false);
        publicaciones = new ArrayList<>();
        adapter = new PublicacionesAdapter(getContext(), publicaciones);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

}
