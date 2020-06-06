package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.ComentariosAdapter;
import com.example.petgram.models.Comentario;
import com.example.petgram.models.Publicacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class PublicacionActivity extends AppCompatActivity {

    // Views
    private ImageView ivFoto, ivEnviar;
    private TextView tvPie, tvFecha;
    private EditText etComentario;
    private NestedScrollView nestedScrollView;

    private RecyclerView recycler;
    private ComentariosAdapter adapter;

    private Publicacion publicacion;
    private ArrayList<Comentario> comentarios;

    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        findViews();

        // Obtenemos la publicación
        try {
            this.publicacion = getIntent().getExtras().getParcelable("publicacion");
            this.comentarios = getIntent().getExtras().getParcelableArrayList("comentarios");
            if(comentarios == null) {
                comentarios = new ArrayList<>();
            }
        } catch (NullPointerException e) {
        }

        Utils.getUserReference(publicacion.getUidUsuario()).child("nombre")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        nombreUsuario = dataSnapshot.getValue(String.class);

                        prepareToolbar();

                        // Asociamos los datos
                        bindData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void bindData() {
        // Obtenemos un formato de fecha correcto
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(publicacion.getTimestamp());
        String fecha = DateFormat.format("dd/MM/yyyy", calendar).toString();

        // Asociamos los datos
        tvPie.setText(publicacion.getPieFoto());
        tvFecha.setText(publicacion.getLugar() + " - " + fecha);
        Picasso.get().load(publicacion.getFoto()).into(ivFoto);

        // Preparamos el adapter
        if (comentarios != null) {
            Collections.sort(comentarios);
            adapter = new ComentariosAdapter(this, comentarios);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            recycler.setAdapter(adapter);
            recycler.setLayoutManager(layoutManager);
        }
    }

    private void findViews() {
        ivFoto = findViewById(R.id.fotoIvPublicacion);
        ivEnviar = findViewById(R.id.enviarComentarioIvComentariosActivity);
        tvPie = findViewById(R.id.pieFotoTvPublicacion);
        tvFecha = findViewById(R.id.fechaYLocalidadTvPublicacion);
        recycler = findViewById(R.id.recyclerComentariosPublicacion);
        etComentario = findViewById(R.id.comentarioEtPublicacionActivity);
        nestedScrollView = findViewById(R.id.nestedScrollViewPublicacion);
    }

    private void prepareToolbar() {
        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.publicacionToolbar);
        toolbar.setTitle(nombreUsuario);
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

    public void clickComentar(View view) {
        final String mensaje = etComentario.getText().toString().trim();
        if (!mensaje.isEmpty()) {
            String miUid = FirebaseAuth.getInstance().getUid();
            Comentario comentario = new Comentario(mensaje, miUid, publicacion.getId(), publicacion.getUidUsuario());

            Utils.getUserReference(publicacion.getUidUsuario()).child("publicaciones")
                    .child(publicacion.getId()).child("comentarios").push().setValue(comentario);

            comentarios.add(comentario);
            Collections.sort(comentarios);
            adapter.notifyDataSetChanged();
            etComentario.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (publicacion.getUidUsuario().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())) {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu_publicacion, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.eliminarMenuPublicacion: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Eliminar publicación");
                builder.setMessage("Estas seguro que deseas eliminar la publicación?");
                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.getMyReference().child("publicaciones").child(publicacion.getId()).removeValue();
                        finish();
                    }
                });
                builder.create().show();
                break;
            }
            case R.id.editarMenuPublicacion: {
                Bundle bundle = new Bundle();
                bundle.putParcelable("publicacion", publicacion);
                bundle.putParcelableArrayList("comentarios", comentarios);
                Intent intent = new Intent(this, ActualizarPublicacionActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            }

        }
        return true;
    }
}
