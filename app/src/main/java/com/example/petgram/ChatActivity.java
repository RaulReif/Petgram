package com.example.petgram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petgram.Configuracion.CamposBD;
import com.example.petgram.adapters.ChatAdapter;
import com.example.petgram.models.Conversacion;
import com.example.petgram.models.Mensaje;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    // Views
    private CircleImageView ivFotoPerfil;
    private TextView tvNombre, tvConexion;
    private EditText etMensaje;
    private RecyclerView recyclerView;

    // UIDs
    private String uidUsuario; // UID del usuario propio
    private String uidOtroUsuario; // UID del usuario con el que se va a chatear

    // Lista de mensajes
    private ArrayList<Mensaje> listaMensajes;

    // Adapter
    private ChatAdapter chatAdapter;

    // Booleano que nos indicara si ya existe una conversación con el otro usuario
    private boolean existeConversacion;

    // Referencias de las conversaciones
    private DatabaseReference conversacionOtroUsuario;
    private DatabaseReference conversacionUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Asociamos y cambiamos el texto de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        toolbar.setTitle("");
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

        // Asociamos las vistas
        ivFotoPerfil = findViewById(R.id.fotoPerfilIvChat);
        tvNombre = findViewById(R.id.nombreTvChat);
        tvConexion = findViewById(R.id.conexionTvChat);
        etMensaje = findViewById(R.id.mensajeEtChat);

        // Obtenemos los UIDs
        uidOtroUsuario = getIntent().getExtras().getString("uid");
        uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtenemos la referencia del usuario receptor
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(uidOtroUsuario);

        // Obtenemos una única vez la imagen y el nombre del usuario de la referencia
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtenemos el usuario receptor
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                // Asociamos los datos
                tvNombre.setText(usuario.getNombre());
                tvConexion.setText(usuario.getConexion());
                Picasso.get().load(usuario.getImagen()).into(ivFotoPerfil);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtenemos en tiempo real el estado de conexion del usuario
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtenemos el usuario receptor
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                // Asociamos los datos
                tvConexion.setText(usuario.getConexion());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Obtenemos la referencia de las conversaciones en los nodos de ambos usuarios
        this.conversacionUsuario = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(this.uidUsuario)
                .child("conversaciones")
                .child(this.uidOtroUsuario);

        this.conversacionOtroUsuario = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(this.uidOtroUsuario)
                .child("conversaciones")
                .child(this.uidUsuario);

        // Comprobamos si ya se ha iniciado una conversación con el otro usuario
        this.existeConversacion = existeConversacion();

        // Leemos los mensajes
        leerMensajes();

        // Activamos el listener del leido
        comprobarLeido();

        // Cambiamos el estado de conexion de nuestro usuario
        cambiarEstadoConexion("En línea");

        etMensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cambiarEstadoConexion("Escribiendo...");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mensaje = String.valueOf(s);
                if (mensaje.isEmpty())
                    cambiarEstadoConexion("En línea");

                else
                    cambiarEstadoConexion("Escribiendo...");
            }
        });
    }

    private boolean existeConversacion() {

        this.conversacionUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                existeConversacion = dataSnapshot.hasChild("mensajes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return true;
    }

    private void comprobarLeido() {
        this.conversacionUsuario.child("mensajes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Mensaje mensaje = ds.getValue(Mensaje.class);
                            // Comprobamos que sean los mensajes donde el otro usuario es el emisor
                            if (mensaje.getEmisor().equals(uidOtroUsuario) && mensaje.getReceptor().equals(uidUsuario)) {
                                // Actualizamos el estado a leído
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(CamposBD.VISTO, "true");
                                ds.getRef().updateChildren(hashMap);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /* Como estó se ejecutará al iniciar el activity pasaremos todos los leídos del otro usuario
        a true */


    }

    private void leerMensajes() {
        recyclerView = findViewById(R.id.chatRecyclerView);
        listaMensajes = new ArrayList<>();

        // Cremos y asociamos el LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        // Creamos y asciamos el adapter
        chatAdapter = new ChatAdapter(this, listaMensajes);
        recyclerView.setAdapter(chatAdapter);

        this.conversacionUsuario.child("mensajes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaMensajes.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                            listaMensajes.add(ds.getValue(Mensaje.class));

                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(listaMensajes.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void cambiarEstadoConexion(String conexion) {
        // Actualizamos el estado de conexion de nuestro usuario
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CamposBD.CONEXION, conexion);
        reference.updateChildren(hashMap);
    }

    public void enviar(View view) {
        String mensajeString = etMensaje.getText().toString().trim();
        if (!mensajeString.isEmpty()) {
            // Creamos el Objeto Mensaje
            Mensaje mensaje = new Mensaje(uidUsuario, uidOtroUsuario, mensajeString);

            // Comprobamos si ya existe la conversación y de no ser asi la creamos
            if(!this.existeConversacion) {
                this.conversacionUsuario.setValue(new Conversacion(this.uidOtroUsuario));
                this.conversacionOtroUsuario.setValue(new Conversacion(this.uidUsuario));
                this.existeConversacion = true;
            }

            // Creamos las referencias donde queremos almacenar el mensaje  y lo guardamos
            this.conversacionUsuario.child("mensajes").push().setValue(mensaje);
            this.conversacionOtroUsuario.child("mensajes").push().setValue(mensaje);

            // Limpiamos el campo de texto
            etMensaje.setText("");
        }
    }

    public void irPerfil(View view) {
        // Redirigimos al UsuarioActivity pasandole el UID del usuario receptor
        Intent intent = new Intent(this, UsuarioActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", this.uidOtroUsuario);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.denunciarMenuChat: {
                Toast.makeText(this, "Se desea denunciar al usuario", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(System.currentTimeMillis());
        String conexion = "Última conexión - " + DateFormat.format("dd/MM/yyyy hh:mm aa", calendar);
        cambiarEstadoConexion(conexion);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(System.currentTimeMillis());
        String conexion = "Última conexión - " + DateFormat.format("dd/MM/yyyy hh:mm aa", calendar);
        cambiarEstadoConexion(conexion);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(System.currentTimeMillis());
        String conexion = "Última conexión - " + DateFormat.format("dd/MM/yyyy hh:mm aa", calendar);
        cambiarEstadoConexion(conexion);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cambiarEstadoConexion("En línea");
    }
}
