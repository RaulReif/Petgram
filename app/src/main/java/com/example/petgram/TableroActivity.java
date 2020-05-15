package com.example.petgram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;


public class TableroActivity extends AppCompatActivity {

    private SpaceNavigationView nav;

    // Constantes Space items (items del navbar)
    private final String _PUBLICACIONES = "publicaciones";
    private final String _MENSAJES = "mensajes";
    private final String _SOCIAL = "social";
    private final String _PERFIL = "perfil";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablero);

        findViews();
        gestionarNavbar(savedInstanceState);

        // Cargamos el fragment de las Publicaciones por defecto
        PublicacionesFragment publicacionesFragment = new PublicacionesFragment();
        FragmentTransaction publicacionesTransaction = getSupportFragmentManager().beginTransaction();
        publicacionesTransaction.replace(R.id.fragment, publicacionesFragment, "");
        publicacionesTransaction.commit();
    }

    private void findViews() {
        nav = findViewById(R.id.navigation);
    }

    private void gestionarNavbar(Bundle savedInstanceState) {
        nav.initWithSaveInstanceState(savedInstanceState);
        nav.showIconOnly();
        nav.setCentreButtonSelectable(true);
        nav.setInActiveCentreButtonIconColor(getResources().getColor(R.color.darkWhite));
        nav.setActiveCentreButtonIconColor(getResources().getColor(R.color.colorWhite));
        nav.setActiveCentreButtonBackgroundColor(getResources().getColor(R.color.colorPrimary));
        crearSpaceItems();
        spaceItemsListeners();
    }

    private void crearSpaceItems() {
        nav.addSpaceItem(new SpaceItem(_PUBLICACIONES, R.drawable.ic_home));
        nav.addSpaceItem(new SpaceItem(_MENSAJES, R.drawable.ic_message));
        nav.setCentreButtonIcon(R.drawable.ic_add_photo);
        nav.addSpaceItem(new SpaceItem(_SOCIAL, R.drawable.ic_social));
        nav.addSpaceItem(new SpaceItem(_PERFIL, R.drawable.ic_profile));
    }

    private void spaceItemsListeners() {
        nav.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                SubirPublicacionFragment subirPublicacionFragment = new SubirPublicacionFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, subirPublicacionFragment, "")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                        .commit();
                nav.setCentreButtonSelected();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemName) {
                    case _PUBLICACIONES:
                        PublicacionesFragment publicacionesFragment = new PublicacionesFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, publicacionesFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case _MENSAJES:
                        MensajesFragment mensajesFragment = new MensajesFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, mensajesFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case _SOCIAL:
                        SocialFragment socialFragment = new SocialFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, socialFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case _PERFIL:
                        PerfilFragment perfilFragment = new PerfilFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, perfilFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });
    }


}
