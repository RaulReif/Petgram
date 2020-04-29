package com.example.petgram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.ChatActivity;
import com.example.petgram.R;
import com.example.petgram.models.Conversacion;
import com.example.petgram.models.Mensaje;
import com.example.petgram.models.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversacionesAdapter extends RecyclerView.Adapter<ConversacionesAdapter.ConversacionHolder>{

    private ArrayList<Conversacion> listaConversaciones;
    private ArrayList<Usuario> listaUsuarios;
    private Context context;

    public ConversacionesAdapter(ArrayList<Conversacion> listaConversaciones, ArrayList<Usuario> listaUsuarios, Context context) {
        this.listaConversaciones = listaConversaciones;
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversacionHolder(LayoutInflater.from(this.context).inflate(R.layout.fila_conversacion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversacionHolder holder, int position) {

        //Solo ejecutaremos el onBindViewHolder si las listas tienen el mismo size
        if(this.listaConversaciones.size() == this.listaUsuarios.size()) {

            // Obtenemos los datos del usuario
            final String fotoPerfil = listaUsuarios.get(position).getImagen();
            final String nombre = listaUsuarios.get(position).getNombre();
            final String uid = listaUsuarios.get(position).getUid();

            // Obtenemos los mensajes de la conversación
            final HashMap<String, Mensaje> mensajes = listaConversaciones.get(position).getMensajes();

            // Obtenemos el texto del último mensaje
            final Mensaje mensaje = this.obtenerUltimoMensaje(mensajes);
            final long timestamp = mensaje.getTimestamp();
            String mensajeString = mensaje.getMensaje();
            if(mensajeString.length() > 28) {
                mensajeString = mensajeString.substring(0, 28) + "...";
            }

            // Pasamos el timestamp a un formato correcto
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(timestamp);
             String fecha = DateFormat.format("dd/MM HH:mm", calendar).toString();
             fecha = fecha.substring(0, 5) + " " + fecha.substring(5);

            // Asociamos los datos
            holder.nombre.setText(nombre);
            holder.fecha.setText(fecha);
            holder.ultimoMensaje.setText(mensajeString);
            Picasso.get().load(fotoPerfil).into(holder.fotoPerfil);

            // Damos el evento de ir al chat
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uid);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return this.listaConversaciones.size();
    }

    // Obtiene el último mensaje del HashMap de Mensajes pasado cómo parametro
    private Mensaje obtenerUltimoMensaje( HashMap<String, Mensaje> mensajes ) {
        long mayorTimestamp = 0;
        Mensaje mensaje = new Mensaje();
        for( Mensaje mensajeTemp : mensajes.values() ) {
            long timestamp = mensajeTemp.getTimestamp();
            if(timestamp > mayorTimestamp) {
                mayorTimestamp = timestamp;
                mensaje = mensajeTemp;

            }
        }
        return mensaje;
    }

    public class ConversacionHolder extends RecyclerView.ViewHolder {

        private CardView card;
        private CircleImageView fotoPerfil;
        private TextView nombre, fecha, ultimoMensaje;

        public ConversacionHolder(@NonNull View itemView) {
            super(itemView);
            this.fotoPerfil = itemView.findViewById(R.id.imagenIvFilaConversacion);
            this.nombre = itemView.findViewById(R.id.nombreTvFilaConversacion);
            this.fecha = itemView.findViewById(R.id.fechaTvFilaConversacion);
            this.card = itemView.findViewById(R.id.cardFilaConversacion);
            this.ultimoMensaje = itemView.findViewById(R.id.ultimoMensajeTvFilaConversacion);
        }
    }
}
