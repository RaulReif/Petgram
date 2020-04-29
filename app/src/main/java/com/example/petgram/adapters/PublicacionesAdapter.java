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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.ComentariosActivity;
import com.example.petgram.Configuracion.Utils;
import com.example.petgram.R;
import com.example.petgram.UsuarioActivity;
import com.example.petgram.models.Comentario;
import com.example.petgram.models.Publicacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class PublicacionesAdapter extends RecyclerView.Adapter<PublicacionesAdapter.PublicacionHolder> {

    private Context context;
    private ArrayList<Publicacion> lista;

    private PublicacionHolder holder;
    private Publicacion publicacion;

    public PublicacionesAdapter(Context context, ArrayList<Publicacion> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public PublicacionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PublicacionHolder(LayoutInflater.from(context).inflate(R.layout.publicacion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicacionHolder holder, int position) {
        this.holder = holder;

        publicacion = lista.get(position);
        ArrayList<Comentario> comentarios = new ArrayList<>();
        if (publicacion.getComentarios() != null)
            comentarios = new ArrayList<>(publicacion.getComentarios().values());


        holder.tvNombre.setText(publicacion.getNombreUsuario());
        holder.tvLugar.setText(publicacion.getLugar());
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
            case 0:
                holder.tvVerCmentarios.setVisibility(View.GONE);
                holder.tvNombrePrimerComentario.setVisibility(View.GONE);
                holder.tvPrimerComentario.setVisibility(View.GONE);
                holder.tvNombreSegundoComentario.setVisibility(View.GONE);
                holder.tvSegundoComentario.setVisibility(View.GONE);
                break;
            case 1: {
                ArrayList<Comentario> comentatiosAux =
                        new ArrayList<Comentario>(publicacion.getComentarios().values());
                holder.tvNombrePrimerComentario.setText(comentatiosAux.get(0).getNombreUsuario() + " ");
                holder.tvPrimerComentario.setText(comentatiosAux.get(0).getContenido());
                holder.tvNombreSegundoComentario.setVisibility(View.GONE);
                holder.tvSegundoComentario.setVisibility(View.GONE);
                break;
            }
            default:
                ArrayList<Comentario> comentariosAux =
                        new ArrayList<Comentario>(publicacion.getComentarios().values());
                holder.tvNombrePrimerComentario.setText(comentariosAux.get(0).getNombreUsuario() + " ");
                holder.tvPrimerComentario.setText(comentariosAux.get(0).getContenido());
                holder.tvNombreSegundoComentario.setText(comentariosAux.get(1).getNombreUsuario() + " ");
                holder.tvSegundoComentario.setText(comentariosAux.get(1).getContenido());
        }

        onClickListeners(publicacion);
    }

    private void onClickListeners(Publicacion publicacion) {
        clickVerComentarios();
        clickNombreUsuario();
        clickFotoDePerfil();
        clickEnviarComentario(publicacion);
    }

    private void clickVerComentarios() {
        holder.tvVerCmentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uidUsuario", publicacion.getUidUsuario());
                bundle.putString("uidPublicacion", publicacion.getUid());
                Intent intent = new Intent(context, ComentariosActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private void clickNombreUsuario() {
        holder.tvNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirigir();
            }
        });
    }

    private void clickFotoDePerfil() {
        holder.ivFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirigir();
            }
        });
    }

    private void redirigir() {
        Bundle bundle = new Bundle();
        bundle.putString("uid", publicacion.getUidUsuario());
        Intent intent = new Intent(context, UsuarioActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void clickEnviarComentario(final Publicacion publicacion) {
        holder.ivEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mensaje = holder.etComentario.getText().toString().trim();
                if (!mensaje.isEmpty()) {
                    Utils.getMyReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Comentario comentario = new Comentario( mensaje,
                                    dataSnapshot.child("nombre").getValue(String.class),
                                    dataSnapshot.child("uid").getValue(String.class),
                                    dataSnapshot.child("imagen").getValue(String.class),
                                    publicacion.getUid(), publicacion.getUidUsuario());

                            Utils.getUserReference(publicacion.getUidUsuario()).child("publicaciones")
                                    .child(publicacion.getUid()).child("comentarios").push().setValue(comentario);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    holder.etComentario.setText("");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class PublicacionHolder extends RecyclerView.ViewHolder {

        private ImageView ivFotoPerfil, ivFoto, ivEnviarComentario;
        private TextView tvNombre, tvLugar, tvNombrePrimerComentario, tvNombreSegundoComentario,
                tvPrimerComentario, tvSegundoComentario, tvFecha, tvVerCmentarios;
        private EditText etComentario;

        public PublicacionHolder(@NonNull View itemView) {
            super(itemView);
            ivFotoPerfil = itemView.findViewById(R.id.fotoIvComentario);
            ivFoto = itemView.findViewById(R.id.fotoIvPublicacion);
            ivEnviarComentario = itemView.findViewById(R.id.enviarComentarioIvPublicacion);
            tvNombre = itemView.findViewById(R.id.nombreTvPublicacion);
            tvLugar = itemView.findViewById(R.id.lugarTvPublicacion);
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
