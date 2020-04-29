package com.example.petgram.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.R;
import com.example.petgram.UsuarioActivity;
import com.example.petgram.models.Comentario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentarioHolder> {

    private Context context;
    private ArrayList<Comentario> lista;

    public ComentariosAdapter(Context context, ArrayList<Comentario> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ComentarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComentarioHolder(LayoutInflater.from(context).inflate(R.layout.comentario, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioHolder holder, int position) {
        Comentario comentario = lista.get(position);

        holder.tvNombre.setText(comentario.getNombreUsuario() + " ");
        holder.tvContenido.setText(comentario.getContenido());
        Picasso.get().load(comentario.getFotoPerfil()).into(holder.ivFoto);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(comentario.getTimestamp());
        holder.tvFecha.setText(DateFormat.format("dd/MM/yyyy", calendar)); //

        onClickListeners(comentario, holder, position);
    }

    private void onClickListeners(Comentario comentario, ComentarioHolder holder, int position) {
        clickNombre(comentario, holder, position);
        clickFoto(comentario, holder, position);
        clickContenido(comentario, holder, position);
    }

    private void clickNombre(final Comentario comentario, ComentarioHolder holder, final int position) {
        holder.tvNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!comentario.getUidUsuarioDelComentario().equals(FirebaseAuth.getInstance().getUid()))
                    redirigir(comentario.getUidUsuarioDelComentario());
                else
                    eliminarComentario(comentario, position);
            }
        });
    }

    private void clickFoto(final Comentario comentario, ComentarioHolder holder, final int position) {
        holder.ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!comentario.getUidUsuarioDelComentario().equals(FirebaseAuth.getInstance().getUid()))
                    redirigir(comentario.getUidUsuarioDelComentario());
                else
                    eliminarComentario(comentario, position);
            }
        });
    }

    private void clickContenido(final Comentario comentario, ComentarioHolder holder, final int position) {
        holder.tvContenido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comentario.getUidUsuarioDelComentario().equals(FirebaseAuth.getInstance().getUid()))
                    eliminarComentario(comentario, position);
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

    private void eliminarComentario(final Comentario comentario, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar comentario");
        builder.setMessage("Estas seguro que deseas eliminar el comentario?");
        builder.setNegativeButton("Cancelar", null);
        // Damos la interacción cuando el usuario confirme que quiere eliminar el mensaje
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Obtenemos las referencias de los comentarios
                DatabaseReference reference = Utils.getUserReference(comentario.getUidUsuarioPublicacion())
                        .child("publicaciones").child(comentario.getUidPublicacion()).child("comentarios");

                Query query = reference.orderByChild("timestamp").equalTo(comentario.getTimestamp());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                            ds.getRef().removeValue();

                        lista.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Has eliminado el comentario", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ComentarioHolder extends RecyclerView.ViewHolder {

        private ImageView ivFoto;
        private TextView tvNombre, tvContenido, tvFecha;

        public ComentarioHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.fotoIvComentario);
            tvNombre = itemView.findViewById(R.id.nombreTvComentario);
            tvContenido = itemView.findViewById(R.id.comentarioTvComentario);
            tvFecha = itemView.findViewById(R.id.fechaTvComentario);
        }
    }
}
