package com.example.petgram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final LinearLayout cambiarEmail = findViewById(R.id.cambiarEmailOpcionMenu);
        final LinearLayout cambiarContrasena = findViewById(R.id.cambiarContrasenaOpcionMenu);
        final LinearLayout listaBloqueados = findViewById(R.id.listaBloqueadosOpcionMenu);
        final LinearLayout cerrarSesion = findViewById(R.id.cerrarSesionOpcionMenu);

        cambiarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, CambiarEmailActivity.class));
            }
        });

        cambiarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, CambiarContrasenaActivity.class));
            }
        });

        listaBloqueados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, BloqueadosActivity.class));
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                MenuActivity.this.finish();
            }
        });
    }
}
