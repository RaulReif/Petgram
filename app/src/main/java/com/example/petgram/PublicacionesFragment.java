package com.example.petgram;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.PublicacionesAdapter;
import com.example.petgram.models.Publicacion;
import com.example.petgram.models.WrapContentLinearLayoutManager;
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

    private SwipeRefreshLayout refreshLayuot;

    public PublicacionesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publicaciones, container, false);

        findViews(view);
        prepareRefreshLayout();

        return view;
    }



    private void findViews(View view) {
        recycler = view.findViewById(R.id.publicacionesRecycler);
        refreshLayuot = view.findViewById(R.id.refreshLayuotPublicaciones);
    }

    private void prepareRefreshLayout() {
        refreshLayuot.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                refreshLayuot.setRefreshing(false);
            }
        });
        refreshLayuot.setColorSchemeResources(R.color.colorPrimary);
    }

    private void obtenerPublicaciones() {

        publicaciones.clear();

        // Obtenemos los uids de nuestros
        Utils.getMyReference().child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator =
                        new GenericTypeIndicator<HashMap<String, String>>() {};

                HashMap<String, String> hashMap = dataSnapshot.getValue(genericTypeIndicator);

                if(hashMap != null) { // Si el hashMap es nulo significará que el usuario aún no tiene amigos
                    final ArrayList<String> uids = new ArrayList<>(hashMap.values());

                    final int[] contadorIteraciones = {0};
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

                                        contadorIteraciones[0]++;

                                        // Solo notificamos los datos en la última iteración
                                        if(uids.size() == contadorIteraciones[0]) {
                                            Collections.sort(publicaciones);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void prepareRecycler() {
        publicaciones = new ArrayList<>();
        adapter = new PublicacionesAdapter(getContext(), publicaciones);
        recycler.setLayoutManager(new WrapContentLinearLayoutManager
                (getContext(), LinearLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareRecycler();
        obtenerPublicaciones();
    }
}
