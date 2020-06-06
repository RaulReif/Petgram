package com.example.petgram;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petgram.Configuracion.Utils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SocialFragment extends Fragment {

    private TabLayout tabLayout;

    public SocialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Buscar usuarios
                        cargarBuscarUsuariosFragment();
                        break;
                    case 1: // Solicitudes amistad
                        cargarSolicitudesAmistadFragment();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

       // Iniciamos el fragment de solicitudes si las hay, y si no, buscar usuarios
        Utils.getMyReference().child("solicitudesRecibidas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator =
                        new GenericTypeIndicator<HashMap<String, String>>() {};

                HashMap<String, String> conversacionesAux = dataSnapshot.getValue(genericTypeIndicator);

                if(conversacionesAux != null) { // Hay solicitudes
                    tabLayout.getTabAt(1).select();
                    cargarSolicitudesAmistadFragment();
                } else {
                    tabLayout.getTabAt(0).select();
                    cargarBuscarUsuariosFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void cargarBuscarUsuariosFragment() {
        BuscarUsuariosFragment buscarUsuariosFragment = new BuscarUsuariosFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSocial, buscarUsuariosFragment, "")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                .commit();
    }

    private void cargarSolicitudesAmistadFragment() {
        SolicitudesAmistadFragment solicitudesAmistadFragment = new SolicitudesAmistadFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSocial, solicitudesAmistadFragment, "")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                .commit();
    }

}
