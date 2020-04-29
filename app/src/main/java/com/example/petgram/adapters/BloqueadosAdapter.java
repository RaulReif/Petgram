package com.example.petgram.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petgram.Configuracion.Utils;
import com.example.petgram.R;
import com.example.petgram.models.Conversacion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BloqueadosAdapter extends RecyclerView.Adapter<BloqueadosAdapter.BloqueadosHolder>{

    private ArrayList<Conversacion> lista;
    private Context context;

    public BloqueadosAdapter( Context context, ArrayList<Conversacion> lista) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public BloqueadosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BloqueadosHolder(LayoutInflater.from(context).inflate(R.layout.fila_bloqueado, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BloqueadosHolder holder, final int position) {
        final Conversacion c = lista.get(position);

        // Obtenemos y asociamos el nombre del usuario
        Utils.getUserReference(c.getUid()).child("nombre")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nombre = dataSnapshot.getValue(String.class);
                        holder.tvNombre.setText(nombre);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        // Obtenemos y asociamos la foto del usuario
        Utils.getUserReference(c.getUid()).child("imagen")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Picasso.get().load(dataSnapshot.getValue(String.class)).into(holder.ivFoto);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

        holder.ivBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Desbloquear");
                builder.setMessage("Estas seguro que deseas desbloquear a este usuario?");
                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        desbloquear(c.getUid());
                        lista.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class BloqueadosHolder extends RecyclerView.ViewHolder {

        private ImageView ivFoto, ivBoton;
        private TextView tvNombre;

        public BloqueadosHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.fotoIvFilaBloqueado);
            ivBoton = itemView.findViewById(R.id.desbloquearIvFilaBloqueado);
            tvNombre = itemView.findViewById(R.id.nombreTvFilaBloqueado);
        }
    }

    private void desbloquear(String uid) {
        Utils.getMyReference().child("conversaciones").child(uid).child("bloqueado").setValue(false);
    }
}
