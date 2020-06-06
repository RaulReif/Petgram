package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.models.Conversacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.ArrayList;
import java.util.HashMap;


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

        // Traera el parametro si venimos de crear una publicación
        String crearPublicacion = null;
        try {
            crearPublicacion = getIntent().getExtras().getString("crearPublicacion");
        } catch (NullPointerException e) {}

        if(crearPublicacion == null) {
            // Cargamos el fragment de las Publicaciones por defecto
            PublicacionesFragment publicacionesFragment = new PublicacionesFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, publicacionesFragment, "")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                    .commit();
        } else {
            nav.changeCurrentItem(1);
        }

        badgesListeners();
    }

    private void findViews() {
        nav = findViewById(R.id.navigation);
    }

    private void gestionarNavbar(Bundle savedInstanceState) {
        nav.initWithSaveInstanceState(savedInstanceState);
        nav.showIconOnly();
        nav.shouldShowFullBadgeText(true);
        nav.setCentreButtonSelectable(true);
        nav.setInActiveCentreButtonIconColor(getResources().getColor(R.color.darkWhite));
        nav.setActiveCentreButtonIconColor(getResources().getColor(R.color.colorWhite));
        nav.setActiveCentreButtonBackgroundColor(getResources().getColor(R.color.colorPrimary));
        crearSpaceItems();
        spaceItemsListeners();
    }

    private void crearSpaceItems() {
        nav.addSpaceItem(new SpaceItem(_PUBLICACIONES, R.drawable.ic_home));
        nav.addSpaceItem(new SpaceItem(_PERFIL, R.drawable.ic_profile));
        nav.setCentreButtonIcon(R.drawable.ic_add_photo);
        nav.addSpaceItem(new SpaceItem(_SOCIAL, R.drawable.ic_social));
        nav.addSpaceItem(new SpaceItem(_MENSAJES, R.drawable.ic_message));
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

    private void badgesListeners() {
        mensajesListener();
        solicitudesAmistadListener();
    }

    public void solicitudesAmistadListener() {
        Utils.getMyReference().child("conversaciones")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, Conversacion>> genericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String, Conversacion>>() {};

                        HashMap<String, Conversacion> conversacionesAux = dataSnapshot.getValue(genericTypeIndicator);

                        ArrayList<Conversacion> conversaciones = new ArrayList<>();
                        if(conversacionesAux != null)
                        conversaciones = new ArrayList<>(conversacionesAux.values());

                        int contadorMensajes = 0;
                        for (Conversacion c : conversaciones) {
                            contadorMensajes = contadorMensajes + c.getMensajesNoLeidos();
                        }

                        try {
                            if (contadorMensajes > 0)
                                nav.showBadgeAtIndex(3, contadorMensajes, getResources().getColor(R.color.red));
                            else
                                nav.hideBadgeAtIndex(3);
                        } catch (IndexOutOfBoundsException e){}

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void mensajesListener() {
        Utils.getMyReference().child("solicitudesRecibidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> genericTypeIndicator =
                        new GenericTypeIndicator<HashMap<String, String>>() {};

                HashMap<String, String> conversacionesAux = dataSnapshot.getValue(genericTypeIndicator);

                try {
                    if (conversacionesAux != null) // No hay solicitudes de amistad recibidas
                        nav.showBadgeAtIndex(2, conversacionesAux.size(), getResources().getColor(R.color.red));
                    else
                        nav.hideBadgeAtIndex(2);
                }catch (IndexOutOfBoundsException e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
