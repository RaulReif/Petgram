package com.example.petgram.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.petgram.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SolicitudesAmistadAdapter extends RecyclerView.Adapter<SolicitudesAmistadAdapter.SolicitudHolder>{

    private Context context;
    private ArrayList<Usuario> lista;
    private String miUid;

    public SolicitudesAmistadAdapter(Context context, ArrayList<Usuario> lista) {
        this.context = context;
        this.lista = lista;
        this.miUid = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public SolicitudHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SolicitudHolder(LayoutInflater.from(context).inflate(R.layout.solicitud_amistad, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudHolder holder, final int position) {
        final Usuario usuario = lista.get(position);

        // Asociamos los datos
        holder.tvNombre.setText(usuario.getNombre());
        holder.tvTipo.setText(usuario.getTipo());
        Picasso.get().load(usuario.getImagen()).into(holder.ivImagen);

        // Click aceptar solicitud
        holder.ivAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarSolicitudes(usuario);
                // Agregamos las uids a las listas de  amistades
                Utils.getMyReference().child("amigos").child(usuario.getUid()).setValue(usuario.getUid());
                Utils.getUserReference(usuario.getUid()).child("amigos").child(miUid).setValue(miUid);
                lista.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Has añadido a " + usuario.getNombre() + " a tus amigos", Toast.LENGTH_SHORT).show();

            }
        });

        // Click rechazar solicitud
        holder.ivRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Estas seguro que desea rechazar la solicitud de amistad de " + usuario.getNombre() + "?");
                builder.setNegativeButton("Cancelar", null);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarSolicitudes(usuario);
                        lista.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Has rechazado la solicitud de amistad", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }


    private void eliminarSolicitudes(Usuario usuario) {
        // Eliminamos la solicitud de nuestras solicitudes recibidas
        Utils.getMyReference().child("solicitudesRecibidas")
                .child(usuario.getUid()).removeValue();

        Utils.getMyReference().child("solicitudesEnviadas")
                .child(usuario.getUid()).removeValue();

        // Eliminamos la solicitud de las solicitudes enviadas del otro usuario
        Utils.getUserReference(usuario.getUid()).child("solicitudesEnviadas")
                .child(miUid).removeValue();

        // Eliminamos la solicitud de las solicitudes enviadas del otro usuario
        Utils.getUserReference(usuario.getUid()).child("solicitudesRecibidas")
                .child(miUid).removeValue();
    }

    public class SolicitudHolder extends RecyclerView.ViewHolder {

        private ImageView ivImagen, ivAceptar, ivRechazar;
        private TextView tvNombre, tvTipo;

        public SolicitudHolder(@NonNull View itemView) {
            super(itemView);
            this.ivImagen = itemView.findViewById(R.id.imagenIvSolicitudAmistad);
            this.ivAceptar = itemView.findViewById(R.id.aceptarIvSolicitudAmistad);
            this.ivRechazar = itemView.findViewById(R.id.rechazarIvSolicitudAmistad);
            this.tvNombre = itemView.findViewById(R.id.nombreTvSolicitudAmistad);
            this.tvTipo = itemView.findViewById(R.id.tipoTvSolicitudAmistad);
        }
    }
}
