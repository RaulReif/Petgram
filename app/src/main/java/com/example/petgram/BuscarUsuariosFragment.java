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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petgram.adapters.UsuariosAdapter;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuscarUsuariosFragment extends Fragment {

    private RecyclerView recycler;
    private ImageView ivImagen;
    private EditText etBuscar;

    private ArrayList<Usuario> listaUsuarios;

    private UsuariosAdapter adapter;

    public BuscarUsuariosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar_usuarios, container, false);

        findViews(view);

        gestionarRecycler();

        etBuscarListener();

        return view;
    }

    private void findViews(View view) {
        recycler = view.findViewById(R.id.recyclerUsuariosBuscadosSocial);
        ivImagen = view.findViewById(R.id.imagenIvSocial);
        etBuscar = view.findViewById(R.id.buscarUsuariosEtSocial);
    }

    private void gestionarRecycler() {
        // Asociamos al recycler un nuevo layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);

        // Creamos y asociamos el adapterUsuarios al recycler
        listaUsuarios = new ArrayList<>();
        adapter = new UsuariosAdapter(getActivity(), listaUsuarios);
        recycler.setAdapter(adapter);
    }

    private void etBuscarListener() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                final String cadena = s.toString();
                if(cadena.isEmpty()){
                    listaUsuarios.clear();
                    recycler.setVisibility(View.GONE);
                    ivImagen.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String cadena = s.toString();
                if (!cadena.isEmpty()) {
                    // No está vacío
                    recycler.setVisibility(View.VISIBLE);
                    ivImagen.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference("usuarios")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Limpiamos la listaUsuarios de usuarios
                                    listaUsuarios.clear();

                                    /* La función getChildren() nos da un Iterator el cual iteraremos para sacar
                                     *  uno por uno todos los DataSnapshot que devuelve que són cada uno de sus nodos
                                     *  (es decir, los usuarios)*/
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        // Obtenemos el usuario del DataSnapshot
                                        Usuario usuario = ds.getValue(Usuario.class);

                                        /* Comprobamos que el usuario no sea el mismo y que su nombre coincida con
                                         *  el texto introducido en el SearchView */
                                        if (!usuario.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                            if (usuario.getNombre().toLowerCase().contains(cadena.toLowerCase()))
                                                listaUsuarios.add(usuario);

                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                } else {
                    // Está vacío
                    recycler.setVisibility(View.GONE);
                    ivImagen.setVisibility(View.VISIBLE);
                    listaUsuarios.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String cadena = s.toString();
                if(cadena.isEmpty()){
                    listaUsuarios.clear();
                    recycler.setVisibility(View.GONE);
                    ivImagen.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void buscarUsuarios(EditText view) {

    }

}
