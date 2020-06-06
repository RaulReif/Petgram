package com.example.petgram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
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
            Collections.sort(comentarios);
        }

        holder.tvLugar.setText(publicacion.getLugar());
        holder.tvPieFoto.setText(publicacion.getPieFoto());
        Picasso.get().load(publicacion.getFoto()).into(holder.ivFoto);

        // Obtenemos la referencia del usuario de la publicación para obtener algunos datos
        Utils.getUserReference(publicacion.getUidUsuario())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        Picasso.get().load(usuario.getImagen()).into(holder.ivFotoPerfil);
                        holder.tvNombre.setText(usuario.getNombre());
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.tvFecha.setText(Utils.getStringFecha(publicacion.getTimestamp()));

        // CONFIGURACIÓN DE COMENTARIOS
        switch (comentarios.size()) {
            case 0: {
                holder.tvVerComentarios.setVisibility(View.GONE);
                holder.tvNombrePrimerComentario.setVisibility(View.GONE);
                holder.tvPrimerComentario.setVisibility(View.GONE);
                holder.tvNombreSegundoComentario.setVisibility(View.GONE);
                holder.tvSegundoComentario.setVisibility(View.GONE);
                break;
            }
            case 1: {
                Utils.getUserReference(comentarios.get(0).getUidUsuarioDelComentario())
                        .child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                holder.tvNombrePrimerComentario
                                        .setText(dataSnapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                holder.tvPrimerComentario.setText(comentarios.get(0).getContenido());
                holder.tvNombreSegundoComentario.setVisibility(View.GONE);
                holder.tvSegundoComentario.setVisibility(View.GONE);
                break;
            }
            default: {
                Utils.getUserReference(comentarios.get(0).getUidUsuarioDelComentario())
                        .child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.tvNombrePrimerComentario
                                .setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.tvPrimerComentario.setText(comentarios.get(0).getContenido());
                Utils.getUserReference(comentarios.get(1).getUidUsuarioDelComentario())
                        .child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.tvNombreSegundoComentario
                                .setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.tvSegundoComentario.setText(comentarios.get(1).getContenido());
            }
        }

        // LISTENERS

        holder.ivEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mensaje = holder.etComentario.getText().toString().trim();

                if (!mensaje.isEmpty()) {

                    Comentario comentario = new Comentario(mensaje, uidUsuario,
                            publicacion.getId(), publicacion.getUidUsuario());

                    Utils.getUserReference(publicacion.getUidUsuario()).child("publicaciones")
                            .child(publicacion.getId()).child("comentarios").push().setValue(comentario);

                    // lista.get(position).addComentario(comentario);
                    holder.etComentario.setText("");

                    final String primerComentario = holder.tvPrimerComentario.getText().toString();
                    final String nombrePrimerComentario = holder.tvNombrePrimerComentario.getText().toString();

                    if(!primerComentario.isEmpty()) {
                        holder.tvPrimerComentario.setText(mensaje);
                        holder.tvNombrePrimerComentario.setText(nombreUsuario);
                        holder.tvSegundoComentario.setText(primerComentario);
                        holder.tvNombreSegundoComentario.setText(nombrePrimerComentario);

                        holder.tvPrimerComentario.setVisibility(View.VISIBLE);
                        holder.tvNombrePrimerComentario.setVisibility(View.VISIBLE);
                        holder.tvSegundoComentario.setVisibility(View.VISIBLE);
                        holder.tvNombreSegundoComentario.setVisibility(View.VISIBLE);
                        holder.tvVerComentarios.setVisibility(View.VISIBLE);

                    } else {
                        holder.tvPrimerComentario.setText(mensaje);
                        holder.tvNombrePrimerComentario.setText(nombreUsuario);

                        holder.tvPrimerComentario.setVisibility(View.VISIBLE);
                        holder.tvNombrePrimerComentario.setVisibility(View.VISIBLE);
                    }


                }
            }
        });

        holder.tvVerComentarios.setOnClickListener(new View.OnClickListener() {
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
                tvPrimerComentario, tvSegundoComentario, tvFecha, tvVerComentarios;
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
            tvFecha = itemView.findViewById(R.id.fechaYLocalidadTvPublicacion);
            tvVerComentarios = itemView.findViewById(R.id.verComentariosTvPublicacion);
            etComentario = itemView.findViewById(R.id.comentarioEtPublicacion);
        }
    }
}
