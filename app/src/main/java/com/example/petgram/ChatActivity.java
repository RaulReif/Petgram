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
import com.example.petgram.Configuracion.Utils;
import com.example.petgram.adapters.ChatAdapter;
import com.example.petgram.models.Conversacion;
import com.example.petgram.models.Mensaje;
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    // Views
    private CircleImageView ivFotoPerfil;
    private TextView tvNombre, tvConexion;
    private EditText etMensaje;
    private RecyclerView recyclerView;
    private volatile MenuItem miBloquear;

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

    // Booleano que nos permite saber si el otro usuario nos tiene bloqueados;
    private boolean bloqueado;

    // El otro usuario
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.findViews();

        // Obtenemos los UIDs
        uidOtroUsuario = getIntent().getExtras().getString("uid");
        uidUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtenemos la referencia del usuario receptor y asociamos los datos
        this.obtenerDatos();

        // Obtenemos la referencia de las conversaciones en los nodos de ambos usuarios
        this.conversacionUsuario = Utils.getMyReference().child("conversaciones").child(this.uidOtroUsuario);

        this.conversacionOtroUsuario = Utils.getUserReference(uidOtroUsuario).child("conversaciones")
                .child(this.uidUsuario);

        // Comprobamos si ya se ha iniciado una conversación con el otro usuario
        this.existeConversacion = existeConversacion();

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

    private void listeners() {
        if (existeConversacion) {
            this.leerMensajes();
            this.comprobarLeido();
            this.comprobarBloqueados();
        }
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

        this.conversacionOtroUsuario.child("mensajes")
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

        this.conversacionUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Conversacion c = dataSnapshot.getValue(Conversacion.class);
                listaMensajes.clear();
                if(c.getMensajes() != null)
                for (Mensaje mensaje : c.getMensajes().values())
                    listaMensajes.add(mensaje);

                Collections.sort(listaMensajes);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(listaMensajes.size() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }

    private void comprobarBloqueados() {
        conversacionUsuario.child("bloqueado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean bloqueado = dataSnapshot.getValue(Boolean.class);
                if (bloqueado) {
                    miBloquear.setTitle("Desbloquear");
                } else {
                    miBloquear.setTitle("Bloquear");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        conversacionOtroUsuario.child("bloqueado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bloqueado = dataSnapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void findViews() {
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
    }

    private void obtenerDatos() {
        DatabaseReference reference = Utils.getUserReference(uidOtroUsuario);

        // Obtenemos una única vez la imagen y el nombre del usuario de la referencia
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtenemos el usuario receptor
                usuario = dataSnapshot.getValue(Usuario.class);

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
    }

    private boolean existeConversacion() {

        this.conversacionUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                existeConversacion = dataSnapshot.hasChild("mensajes");
                if(existeConversacion)
                    listeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return true;
    }

    private void cambiarEstadoConexion(String conexion) {
        // Actualizamos el estado de conexion de nuestro usuario
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(CamposBD.CONEXION, conexion);
        Utils.getMyReference().updateChildren(hashMap);
    }

    public void enviar(View view) {
        if (!bloqueado) {
            String mensajeString = etMensaje.getText().toString().trim();
            if (!mensajeString.isEmpty()) {
                // Creamos el Objeto Mensaje
                Mensaje mensaje = new Mensaje(uidUsuario, uidOtroUsuario, mensajeString);

                // Comprobamos si ya existe la conversación y de no ser asi la creamos
                if (!this.existeConversacion) {
                    this.conversacionUsuario.setValue(new Conversacion(this.uidOtroUsuario));
                    this.conversacionOtroUsuario.setValue(new Conversacion(this.uidUsuario));
                    this.existeConversacion = true;
                    listeners();
                }

                // Creamos las referencias donde queremos almacenar el mensaje  y lo guardamos
                this.conversacionUsuario.child("mensajes").push().setValue(mensaje);
                this.conversacionOtroUsuario.child("mensajes").push().setValue(mensaje);

                // Cambiamos el timestamp del último mensaje de las conversaciones
                this.conversacionUsuario.child("ultimoMensaje").setValue(System.currentTimeMillis());
                this.conversacionOtroUsuario.child("ultimoMensaje").setValue(System.currentTimeMillis());

                // Limpiamos el campo de texto
                etMensaje.setText("");
            }
        } else {
            Toast.makeText(this, this.usuario.getNombre() + " te ha bloqueado", Toast.LENGTH_SHORT).show();
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
        this.miBloquear = menu.findItem(R.id.bloquearMenuChat);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.denunciarMenuChat: {
                Toast.makeText(this, "Se desea denunciar al usuario", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.bloquearMenuChat: {
                this.conversacionUsuario.child("bloqueado")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean bloqueadoAux = dataSnapshot.getValue(Boolean.class);
                                dataSnapshot.getRef().setValue(!bloqueadoAux);
                                if (bloqueadoAux) {
                                    Toast.makeText(ChatActivity.this, "Has desbloqueado a " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChatActivity.this, "Has bloqueado a " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
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
