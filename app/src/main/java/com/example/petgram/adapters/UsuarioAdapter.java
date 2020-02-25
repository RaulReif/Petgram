package com.example.petgram.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.R;
import com.example.petgram.UsuarioActivity;
import com.example.petgram.models.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.FilaUsuarioHolder>{

    private Context context;
    private ArrayList<Usuario> lista;

    public UsuarioAdapter(Context context, ArrayList<Usuario> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public FilaUsuarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilaUsuarioHolder(LayoutInflater.from(context).inflate(R.layout.fila_usuario, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilaUsuarioHolder holder, int position) {
        // Obtenemos los datos
        final Usuario u = lista.get(position);
        String nombre = u.getNombre();
        String tipo = u.getTipo();
        String imagen = u.getImagen();

        // Asociamos los datos
        holder.tvNombre.setText(nombre);
        holder.tvTipo.setText(tipo);
        try {
            Picasso.get().load(imagen).into(holder.ivImagen);
        }catch (Exception e){}

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uid", u.getUid());
                Intent intent = new Intent(context, UsuarioActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class FilaUsuarioHolder extends RecyclerView.ViewHolder{

        private CircleImageView ivImagen;
        private TextView tvNombre, tvTipo;
        private CardView card;

        public FilaUsuarioHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.imagenIvFilaUsuario);
            tvNombre = itemView.findViewById(R.id.nombreTvFilaUsuario);
            tvTipo = itemView.findViewById(R.id.tipoTvFilaUsuario);
            card = itemView.findViewById(R.id.cardFilaUsuario);
        }
    }
}
