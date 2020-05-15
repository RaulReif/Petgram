package com.example.petgram;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


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
                        BuscarUsuariosFragment buscarUsuariosFragment = new BuscarUsuariosFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentSocial, buscarUsuariosFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;
                    case 1: // Solicitudes amistad
                        SolicitudesAmistadFragment solicitudesAmistadFragment = new SolicitudesAmistadFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentSocial, solicitudesAmistadFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
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

        // Cargamos el fragment de buscar usuarios por defecto
        BuscarUsuariosFragment buscarUsuariosFragment = new BuscarUsuariosFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentSocial, buscarUsuariosFragment, "")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                .commit();

        return view;
    }

}
