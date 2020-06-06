package com.example.petgram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.PublicacionActivity;
import com.example.petgram.R;
import com.example.petgram.models.Publicacion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PublicacionesPerfilAdapter extends RecyclerView.Adapter<PublicacionesPerfilAdapter.PublicacionPerfilHolder>{

    private Context context;
    private ArrayList<Publicacion> lista;

    public PublicacionesPerfilAdapter(Context context, ArrayList<Publicacion> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public PublicacionPerfilHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PublicacionPerfilHolder(LayoutInflater.from(context).inflate(R.layout.publicacion_perfil, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionPerfilHolder holder, final int position) {
        final Publicacion publicacion = lista.get(position);
        Picasso.get().load(publicacion.getFoto()).into(holder.imagen);

        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("publicacion", publicacion);
                if(publicacion.getComentarios() != null)
                bundle.putParcelableArrayList("comentarios",
                        new ArrayList<Parcelable>(publicacion.getComentarios().values()));
                Intent intent = new Intent(context, PublicacionActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class PublicacionPerfilHolder extends RecyclerView.ViewHolder {

        private ImageView imagen;

        public PublicacionPerfilHolder(@NonNull View itemView) {
            super(itemView);
            this.imagen = itemView.findViewById(R.id.publicacionIvPublicacionesPerfil);
        }
    }
}
