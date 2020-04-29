package com.example.petgram;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.SolicitudesAmistadAdapter;
import com.example.petgram.adapters.UsuariosAdapter;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    // Lista de usuarios buscados y solicitudes de asmistad
    private ArrayList<Usuario> listaUsuarios, listaSolicitudes;

    // Referencia de los usuarios
    private DatabaseReference reference;

    // Adapters
    private UsuariosAdapter adapterUsuarios;
    private SolicitudesAmistadAdapter adapterSolicitudes;

    // Vistas
    private ImageView imagen;
    private RecyclerView recyclerViewUsuarios, recyclerViewSolicitudes;

    private SearchView searchView;

    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        findViews(view);

        gestionarRecyclers();

        // Creamos la referencia donde se almacenan los usuarios
        reference = FirebaseDatabase.getInstance().getReference("usuarios");

        return view;
    }

    private void gestionarRecyclers() {
        gestionarRecyclerUsuarios();
        gestionarRecyclerSolicitudes();
    }

    private void gestionarRecyclerUsuarios() {
        // Asociamos al recycler un nuevo layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerViewUsuarios.setLayoutManager(layoutManager);

        // Creamos y asociamos el adapterUsuarios al recycler
        listaUsuarios = new ArrayList<>();
        adapterUsuarios = new UsuariosAdapter(getActivity(), listaUsuarios);
        recyclerViewUsuarios.setAdapter(adapterUsuarios);
    }

    private void gestionarRecyclerSolicitudes() {
        // Asociamos al recycler un nuevo layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerViewSolicitudes.setLayoutManager(layoutManager);

        // Creamos y asociamos el adapterUsuarios al recycler
        listaSolicitudes = new ArrayList<>();
        adapterSolicitudes = new SolicitudesAmistadAdapter(getActivity(), listaSolicitudes);
        recyclerViewSolicitudes.setAdapter(adapterSolicitudes);

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
                                    listaSolicitudes.add(dataSnapshot.getValue(Usuario.class));
                                    adapterSolicitudes.notifyDataSetChanged();
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
        recyclerViewUsuarios = view.findViewById(R.id.recyclerUsuariosBuscadosSocial);
        recyclerViewSolicitudes = view.findViewById(R.id.recyclerSolicitudesSocial);
        imagen = view.findViewById(R.id.imagenIvSocial);
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_buscara_usuario, menu);

        // Obtenemos el SearchView
        MenuItem menuItem = menu.findItem(R.id.buscarMenuBuscar);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Busca");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarUsuarios(query);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarUsuarios(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return true;
    }

    private void buscarUsuarios(final String cadena) {
        // Comprobamos si el SearchView está vacío, si es así, limpiaremos el array
        if (!cadena.isEmpty()) {
            // No está vacío
            recyclerViewUsuarios.setVisibility(View.VISIBLE);
            imagen.setVisibility(View.GONE);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    adapterUsuarios.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // Está vacío
            recyclerViewUsuarios.setVisibility(View.GONE);
            recyclerViewSolicitudes.setVisibility(View.VISIBLE);
            listaUsuarios.clear();
            adapterUsuarios.notifyDataSetChanged();
        }
    }


}
