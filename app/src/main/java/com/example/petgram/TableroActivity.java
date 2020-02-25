package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
                        FragmentTransaction publicacionesTransaction = getSupportFragmentManager().beginTransaction();
                        publicacionesTransaction.replace(R.id.fragment, publicacionesFragment, "");
                        publicacionesTransaction.commit();
                        break;

                    case R.id.mensajesNavItem:
                        toolbar.setVisibility(View.GONE);
                        MensajesFragment mensajesFragment = new MensajesFragment();
                        FragmentTransaction mensajesTransaction = getSupportFragmentManager().beginTransaction();
                        mensajesTransaction.replace(R.id.fragment, mensajesFragment, "");
                        mensajesTransaction.commit();
                        break;

                    case R.id.socialNavItem:
                        toolbar.setTitle("");
                        toolbar.setVisibility(View.VISIBLE);
                        SocialFragment socialFragment = new SocialFragment();
                        FragmentTransaction socialTransaction = getSupportFragmentManager().beginTransaction();
                        socialTransaction.replace(R.id.fragment, socialFragment, "");
                        socialTransaction.commit();
                        break;

                    case R.id.perfilNavItem:
                        toolbar.setTitle("Tu perfil");
                        toolbar.setVisibility(View.VISIBLE);
                        PerfilFragment perfilFragment = new PerfilFragment();
                        FragmentTransaction perfilTranscaction = getSupportFragmentManager().beginTransaction();
                        perfilTranscaction.replace(R.id.fragment, perfilFragment, "");
                        perfilTranscaction.commit();
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
