package com.example.petgram;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.petgram.adapters.UsuarioAdapter;
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
public class SocialFragment extends Fragment {

    // Lista de usuarios
    private ArrayList<Usuario> lista;

    // Referencia de los usuarios
    private DatabaseReference reference;

    // Adapter de usuarios
    private UsuarioAdapter adapter;

    // Vistas
    private ImageView imagen;
    private RecyclerView recyclerView;

    SearchView searchView;

    public SocialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        // Asociación de las vistas
        recyclerView = view.findViewById(R.id.recyclerSocial);
        imagen = view.findViewById(R.id.imagenIvSocial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.GONE);

        // Asociamos al recycler un nuevo layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Creamos y asociamos el adapter al recycler
        lista = new ArrayList<>();
        adapter = new UsuarioAdapter(getActivity(), lista);
        recyclerView.setAdapter(adapter);

        // Creamos la referencia donde se almacenan los usuarios
        reference = FirebaseDatabase.getInstance().getReference("usuarios");

        return view;
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
            recyclerView.setVisibility(View.VISIBLE);
            imagen.setVisibility(View.GONE);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Limpiamos la lista de usuarios
                    lista.clear();

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
                                lista.add(usuario);

                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // Está vacío
            recyclerView.setVisibility(View.GONE);
            imagen.setVisibility(View.VISIBLE);
            lista.clear();
            adapter.notifyDataSetChanged();
        }
    }


}
