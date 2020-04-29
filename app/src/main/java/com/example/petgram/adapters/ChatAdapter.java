package com.example.petgram.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.R;
import com.example.petgram.models.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MensajeHolder> {

    // Constantes
    private final int FILA_DERECHA = 1;
    private final int FILA_IZQUIERDA = 0;

    // Atributos de la clase
    private Context context;
    private ArrayList<Mensaje> listaMensajes;

    // UID del usuario propio
    private String uid;

    public ChatAdapter(Context context, ArrayList<Mensaje> listaMensajes) {
        this.context = context;
        this.listaMensajes = listaMensajes;

        // Obtenemos el UID del usuario
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public int getItemViewType(int position) {
        /* Devolvemos el valor de fila derecha si el usuario es el emisor del mensaje,
           de no ser así devolveremos fila izquierda */
        if (listaMensajes.get(position).getEmisor().equals(uid))
            return FILA_DERECHA;
        else
            return FILA_IZQUIERDA;
    }

    @NonNull
    @Override
    public MensajeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Hincharemos una fila diferente segín el viewType que se nos pase
        if (viewType == FILA_DERECHA)
            return new MensajeHolder(LayoutInflater.from(context).inflate(R.layout.fila_chat_derecha, null));
        else
            return new MensajeHolder(LayoutInflater.from(context).inflate(R.layout.fila_chat_izquierda, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeHolder holder, int position) {

        // Obtenemos los datos
        String mensaje = listaMensajes.get(position).getMensaje();
        final long timestamp = listaMensajes.get(position).getTimestamp();
        String leido = listaMensajes.get(position).getVisto();
        final String emisor = listaMensajes.get(position).getEmisor();
        final String receptor = listaMensajes.get(position).getReceptor();

        // Pasamos el timestamp a un formato de fecha correcto
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String fecha = DateFormat.format("dd/MM HH:mm", calendar).toString();

        // Asociamos los datos
        holder.mensaje.setText(mensaje);
        holder.fecha.setText(fecha);
        /* Solo mostraremos el TextView leído si és el ultimo mensaje del chat ademas
         *  de que el usuario sea el emisor*/
        if (position == listaMensajes.size() - 1 &&
                listaMensajes.get(position).getEmisor().equals(uid))
            if (leido.equals("true"))
                holder.leido.setText("Leído");
            else
                holder.leido.setText("Enviado");
        else
            holder.leido.setVisibility(View.GONE);

        if (emisor.equals(uid)) {
            // Le damos el evento para eliminar el mensaje
            holder.mensaje.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Creamos y configuramos el AlertDialog para eliminar el mensaje
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Eliminar mensaje");
                    builder.setMessage("Estas seguro que deseas eliminar el mensaje?");
                    builder.setNegativeButton("Cancelar", null);
                    // Damos la interacción cuando el usuario confirme que quiere eliminar el mensaje
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Obtenemos las referencias de los mensajes
                            DatabaseReference mensajesReference = Utils.getUserReference(emisor)
                                    .child("conversaciones").child(receptor).child("mensajes");

                            Query query = mensajesReference.orderByChild("timestamp").equalTo(timestamp);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }


                            });

                            DatabaseReference mensajesReference2 = Utils.getUserReference(receptor)
                                    .child("conversaciones").child(emisor).child("mensajes");

                            Query query2 = mensajesReference2.orderByChild("timestamp").equalTo(timestamp);
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }


                            });

                        }
                    });
                    builder.create().show();

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public class MensajeHolder extends RecyclerView.ViewHolder {

        private TextView mensaje, fecha, leido;

        public MensajeHolder(@NonNull View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.mensajeTvFilaChat);
            fecha = itemView.findViewById(R.id.fechTvFilaChat);
            leido = itemView.findViewById(R.id.leidoTvFilaChat);
        }
    }

}
