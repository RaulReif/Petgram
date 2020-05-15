package com.example.petgram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.ComentariosActivity;
import com.example.petgram.Configuracion.Utils;
import com.example.petgram.R;
import com.example.petgram.UsuarioActivity;
import com.example.petgram.models.Comentario;
import com.example.petgram.models.Publicacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PublicacionesAdapter extends RecyclerView.Adapter<PublicacionesAdapter.PublicacionHolder> {

    private Context context;
    private ArrayList<Publicacion> lista;

    // Datos del usuario que está utilizando la aplicación
    private String uidUsuario;
    private String nombreUsuario;

    public PublicacionesAdapter(Context context, ArrayList<Publicacion> lista) {
        this.context = context;
        this.lista = lista;
        this.uidUsuario = FirebaseAuth.getInstance().getUid();
        Utils.getMyReference().child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreUsuario = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public PublicacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PublicacionHolder(LayoutInflater.from(context).inflate(R.layout.publicacion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicacionHolder holder, final int position) {
        final Publicacion publicacion = lista.get(position);

        ArrayList<Comentario> comentarios = new ArrayList<>();
        if (publicacion.getComentarios() != null) {
            comentarios = new ArrayList<>(publicacion.getComentarios().values());
            Toast.makeText(context, String.valueOf(comentarios.size()), Toast.LENGTH_SHORT).show();
        }


        holder.tvNombre.setText(publicacion.getNombreUsuario());
        holder.tvLugar.setText(publicacion.getLugar());
        holder.tvPieFoto.setText(publicacion.getPieFoto());
        Picasso.get().load(publicacion.getFoto()).into(holder.ivFoto);

        // Obtenemos la referencia del usuario de la publicación para obtener algunos datos
        Utils.getUserReference(publicacion.getUidUsuario()).child("imagen")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Picasso.get().load(dataSnapshot.getValue(String.class)).into(holder.ivFotoPerfil);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(publicacion.getTimestamp());
        holder.tvFecha.setText(DateFormat.format("dd/MM/yyyyy", calendar));

        switch (comentarios.size()) {
            case 0: {
                holder.tvVerCmentarios.setVisibility(View.GONE);
                holder.tvNombrePrimerComentario.setVisibility(View.GONE);
                holder.tvPrimerComentario.setVisibility(View.GONE);
                holder.tvNombreSegundoComentario.setVisibility(View.GONE);
                holder.tvSegundoComentario.setVisibility(View.GONE);
                break;
            }
            case 1: {
                ArrayList<Comentario> comentariosAux =
                        new ArrayList<Comentario>(publicacion.getComentarios().values());
                holder.tvNombrePrimerComentario.setText(comentariosAux.get(0).getNombreUsuario());
                holder.tvPrimerComentario.setText(comentariosAux.get(0).getContenido());
                holder.tvNombreSegundoComentario.setVisibility(View.GONE);
                holder.tvSegundoComentario.setVisibility(View.GONE);
                break;
            }
            default: {
                ArrayList<Comentario> comentariosAux =
                        new ArrayList<Comentario>(publicacion.getComentarios().values());
                holder.tvNombrePrimerComentario.setText(comentariosAux.get(0).getNombreUsuario());
                holder.tvPrimerComentario.setText(comentariosAux.get(0).getContenido());
                holder.tvNombreSegundoComentario.setText(comentariosAux.get(1).getNombreUsuario());
                holder.tvSegundoComentario.setText(comentariosAux.get(1).getContenido());
            }
        }

        // LISTENERS

        holder.ivEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mensaje = holder.etComentario.getText().toString().trim();

                if (!mensaje.isEmpty()) {

                    Comentario comentario = new Comentario(mensaje, nombreUsuario, uidUsuario,
                            publicacion.getId(), publicacion.getUidUsuario());

                    Utils.getUserReference(publicacion.getUidUsuario()).child("publicaciones")
                            .child(publicacion.getId()).child("comentarios").push().setValue(comentario);


                    if (lista.get(position).getComentarios() == null)
                        lista.get(position).setComentarios(new HashMap<String, Comentario>());

                    lista.get(position).getComentarios().put("new", comentario);
                    notifyDataSetChanged();

                    holder.etComentario.setText("");
                }
            }
        });

        holder.tvVerCmentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uidUsuario", publicacion.getUidUsuario());
                bundle.putString("uidPublicacion", publicacion.getId());
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.tvNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirigir(publicacion.getUidUsuario());
            }
        });

        holder.ivFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirigir(publicacion.getUidUsuario());
            }
        });
    }

    private void redirigir(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        Intent intent = new Intent(context, UsuarioActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class PublicacionHolder extends RecyclerView.ViewHolder {

        private ImageView ivFotoPerfil, ivFoto, ivEnviarComentario;
        private TextView tvNombre, tvLugar, tvPieFoto, tvNombrePrimerComentario, tvNombreSegundoComentario,
                tvPrimerComentario, tvSegundoComentario, tvFecha, tvVerCmentarios;
        private EditText etComentario;

        public PublicacionHolder(@NonNull View itemView) {
            super(itemView);
            ivFotoPerfil = itemView.findViewById(R.id.fotoIvComentario);
            ivFoto = itemView.findViewById(R.id.fotoIvPublicacion);
            ivEnviarComentario = itemView.findViewById(R.id.enviarComentarioIvPublicacion);
            tvNombre = itemView.findViewById(R.id.nombreTvPublicacion);
            tvLugar = itemView.findViewById(R.id.lugarTvPublicacion);
            tvPieFoto = itemView.findViewById(R.id.pieFotoTvPublicacion);
            tvNombrePrimerComentario = itemView.findViewById(R.id.nombrePrimerComentarioTvPublicacion);
            tvNombreSegundoComentario = itemView.findViewById(R.id.nombreSegundoComentarioTvPublicacion);
            tvPrimerComentario = itemView.findViewById(R.id.primerComentarioTvPublicacion);
            tvSegundoComentario = itemView.findViewById(R.id.segundoComentarioTvPublicacion);
            tvFecha = itemView.findViewById(R.id.fechaTvPublicacion);
            tvVerCmentarios = itemView.findViewById(R.id.verComentariosTvPublicacion);
            etComentario = itemView.findViewById(R.id.comentarioEtPublicacion);
        }
    }
}
