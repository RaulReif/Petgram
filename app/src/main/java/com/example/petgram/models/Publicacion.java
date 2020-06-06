package com.example.petgram.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Publicacion implements Parcelable, Comparable<Publicacion> {

    private String foto, pieFoto, uidUsuario, lugar, id;
    private HashMap<String, Comentario> comentarios;
    private Long timestamp;

    public Publicacion(String idUsuario, String foto, String pieFoto, long timestamp, String lugar, String id) {
        this.foto = foto;
        this.pieFoto = pieFoto;
        this.uidUsuario = idUsuario;
        this.timestamp = timestamp;
        this.lugar = lugar;
        this.id = id;
    }

    public Publicacion() {
    }


    protected Publicacion(Parcel in) {
        foto = in.readString();
        pieFoto = in.readString();
        uidUsuario = in.readString();
        lugar = in.readString();
        id = in.readString();
        if (in.readByte() == 0) {
            timestamp = null;
        } else {
            timestamp = in.readLong();
        }
    }

    public static final Creator<Publicacion> CREATOR = new Creator<Publicacion>() {
        @Override
        public Publicacion createFromParcel(Parcel in) {
            return new Publicacion(in);
        }

        @Override
        public Publicacion[] newArray(int size) {
            return new Publicacion[size];
        }
    };

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getPieFoto() {
        return pieFoto;
    }

    public void setPieFoto(String pieFoto) {
        this.pieFoto = pieFoto;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(HashMap<String, Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(foto);
        dest.writeString(pieFoto);
        dest.writeString(uidUsuario);
        dest.writeString(lugar);
        dest.writeString(id);
        if (timestamp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(timestamp);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        this.id = uid;
    }

    public void addComentario(Comentario comentario) {
        final String key = String.valueOf(System.currentTimeMillis());
        this.comentarios.put(key, comentario);
    }

    @Override
    public int compareTo(Publicacion p) {
        if (this.timestamp > p.getTimestamp())
            return -1;
        else
            return 1;
    }
}