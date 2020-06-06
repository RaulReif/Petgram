package com.example.petgram.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comentario implements Parcelable, Comparable<Comentario> {

    private String contenido,  uidUsuarioDelComentario, uidPublicacion, uidUsuarioPublicacion;

    private long timestamp;

    public Comentario( String contenido, String uidUsuario, String uidPublicacion,
                       String uidUsuarioPublicacion) {
        this.contenido = contenido;
        this.uidUsuarioDelComentario = uidUsuario;
        this.uidPublicacion = uidPublicacion;
        this.uidUsuarioPublicacion = uidUsuarioPublicacion;
        this.timestamp = System.currentTimeMillis();
    }

    public Comentario() {}


    protected Comentario(Parcel in) {
        contenido = in.readString();
        uidUsuarioDelComentario = in.readString();
        uidPublicacion = in.readString();
        uidUsuarioPublicacion = in.readString();
        timestamp = in.readLong();
    }

    public static final Creator<Comentario> CREATOR = new Creator<Comentario>() {
        @Override
        public Comentario createFromParcel(Parcel in) {
            return new Comentario(in);
        }

        @Override
        public Comentario[] newArray(int size) {
            return new Comentario[size];
        }
    };

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getUidUsuarioDelComentario() {
        return uidUsuarioDelComentario;
    }

    public void setUidUsuarioDelComentario(String uidUsuarioDelComentario) {
        this.uidUsuarioDelComentario = uidUsuarioDelComentario;
    }

    public String getUidPublicacion() {
        return uidPublicacion;
    }

    public void setUidPublicacion(String uidPublicacion) {
        this.uidPublicacion = uidPublicacion;
    }

    public String getUidUsuarioPublicacion() {
        return uidUsuarioPublicacion;
    }

    public void setUidUsuarioPublicacion(String uidUsuarioPublicacion) {
        this.uidUsuarioPublicacion = uidUsuarioPublicacion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contenido);
        dest.writeString(uidUsuarioDelComentario);
        dest.writeString(uidPublicacion);
        dest.writeString(uidUsuarioPublicacion);
        dest.writeLong(timestamp);
    }

    @Override
    public int compareTo(Comentario c) {
        if(this.timestamp > c.timestamp) {
            return -1;
        } else {
            return 1;
        }
    }
}
