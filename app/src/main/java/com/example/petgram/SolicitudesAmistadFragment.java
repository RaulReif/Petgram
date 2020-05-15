package com.example.petgram;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.SolicitudesAmistadAdapter;
import com.example.petgram.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SolicitudesAmistadFragment extends Fragment {

    private SolicitudesAmistadAdapter adapter;
    private RecyclerView recycler;
    private ArrayList<Usuario> lista;

    public SolicitudesAmistadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solicitudes_amistad, container, false);

        findViews(view);

        gestionarRecycler();

        return view;
    }

    private void findViews(View view) {
        recycler = view.findViewById(R.id.recyclerSolicitudesSocial);
    }

    private void gestionarRecycler() {
        // Asociamos al recycler un nuevo layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);

        // Creamos y asociamos el adapterUsuarios al recycler
        lista = new ArrayList<>();
        adapter = new SolicitudesAmistadAdapter(getActivity(), lista);
        recycler.setAdapter(adapter);

        // Obtenemos los uids de los usuarios que nos han enviado una solicitud
        Utils.getMyReference().child("solicitudesRecibidas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String, String>>() {
                                };

                        HashMap<String, String> tempHashMap = dataSnapshot.getValue(genericTypeIndicator);

                        ArrayList<String> uids = new ArrayList<>();
                        if (tempHashMap != null)
                            uids = new ArrayList<>(tempHashMap.values());

                        for (String uid : uids) {
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
