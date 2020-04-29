package com.example.petgram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petgram.models.Publicacion;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class PublicacionActivity extends AppCompatActivity {

    // Views
    private ImageView ivFoto;
    private TextView tvPie, tvFecha;

    private Publicacion p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        findViews();

        // Obtenemos la publicación
        this.p = getIntent().getExtras().getParcelable("publicacion");

        prepareToolbar();

        // Asociamos los datos
        bindData();
    }

    private void bindData() {
        // Obtenemos un formato de fecha correcto
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(p.getTimestamp());
        String fecha = DateFormat.format("dd/MM/yyyy", calendar).toString();

        // Asociamos los datos
        tvPie.setText(p.getPieFoto());
        tvFecha.setText(fecha);
        Picasso.get().load(p.getFoto()).into(ivFoto);
    }

    private void findViews() {
        ivFoto = findViewById(R.id.fotoIvPublicacion);
        tvPie = findViewById(R.id.pieFotoTvPublicacion);
        tvFecha = findViewById(R.id.fechaTvPublicacion);
    }

    private void prepareToolbar() {
        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.publicacionToolbar);
        toolbar.setTitle(p.getNombreUsuario());
        setSupportActionBar(toolbar);

        // Habilitamos el botón de atras en la toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
