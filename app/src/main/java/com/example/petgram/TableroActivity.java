package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class TableroActivity extends AppCompatActivity {

    // Views
    private Toolbar toolbar;

    // FireBase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablero);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView nav = findViewById(R.id.navigation);

        // Creamos la interacción del navbar
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.publicacionesNavItem:
                        toolbar.setVisibility(View.GONE);
                        PublicacionesFragment publicacionesFragment = new PublicacionesFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, publicacionesFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.mensajesNavItem:
                        toolbar.setVisibility(View.GONE);
                        MensajesFragment mensajesFragment = new MensajesFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, mensajesFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.subirPublicacionNavItem:
                        toolbar.setVisibility(View.GONE);
                        SubirPublicacionFragment subirPublicacionFragment = new SubirPublicacionFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, subirPublicacionFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.socialNavItem:
                        toolbar.setTitle("");
                        toolbar.setVisibility(View.VISIBLE);
                        SocialFragment socialFragment = new SocialFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, socialFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                    case R.id.perfilNavItem:
                        toolbar.setTitle("Tu perfil");
                        toolbar.setVisibility(View.VISIBLE);
                        PerfilFragment perfilFragment = new PerfilFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment, perfilFragment, "")
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null)
                                .commit();
                        break;

                }

                return true;
            }
        });

        // Cargamos el fragment de las Publicaciones por defecto
        PublicacionesFragment publicacionesFragment = new PublicacionesFragment();
        FragmentTransaction publicacionesTransaction = getSupportFragmentManager().beginTransaction();
        publicacionesTransaction.replace(R.id.fragment, publicacionesFragment, "");
        publicacionesTransaction.commit();

    }


}
