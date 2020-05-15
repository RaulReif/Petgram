package com.example.petgram;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


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
    private EditText etBuscarUsuario;

    // Adapter
    private ConversacionesAdapter adapter;

    // Cada objeto de estas listas tiene un usuario con su conversacion asociada
    private ArrayList<Conversacion> lista;
    private ArrayList<Conversacion> listaMostrada;

    private View view;

    public MensajesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mensajes, container, false);
        findViews(view);
        prepareRecycler();
        etBuscarUsuarioListener();
        return view;
    }

    private void findViews(View view) {
        this.recycler = view.findViewById(R.id.conversacionesRecyclerView);
        this.progressBar = view.findViewById(R.id.progressBarMensajes);
        this.etBuscarUsuario = view.findViewById(R.id.buscarConversacionEtMensajes);
    }

    private void prepareRecycler() {
        lista = new ArrayList<>();
        listaMostrada = new ArrayList<>();
        // Montamos nuestro Adapter y LayoutManager y lo asociamos al RecyclerView
        adapter = new ConversacionesAdapter(getContext(), listaMostrada);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

    }

    private void obtenerDatos() {


        // Obtenemos las conversaciones y las añadimos a nuestra lista
        Utils.getMyReference().child("conversaciones").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recycler.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                lista.clear();
                listaMostrada.clear();
                adapter.notifyDataSetChanged();

                final ArrayList<Conversacion> conversaciones = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild("mensajes"))  // Solo añadimos conversaciones con mensajes
                        lista.add(ds.getValue(Conversacion.class));
                        listaMostrada.add(ds.getValue(Conversacion.class));
                }
                Collections.sort(lista);
                Collections.sort(listaMostrada);
                adapter.notifyDataSetChanged();

                recycler.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void etBuscarUsuarioListener() {
        etBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String cadena = s.toString();
                if (!cadena.isEmpty()) {
                    listaMostrada.clear();
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i).getNombreUsuario().contains(cadena)) {
                            listaMostrada.add(lista.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    listaMostrada.clear();
                    listaMostrada.addAll(lista);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerDatos();
    }
}
