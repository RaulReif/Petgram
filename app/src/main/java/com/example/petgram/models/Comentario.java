package com.example.petgram.models;

public class Comentario {

    private String contenido,  nombreUsuario, uidUsuarioDelComentario, uidPublicacion, uidUsuarioPublicacion;

    private long timestamp;

    public Comentario( String contenido, String nombreUsuario, String uidUsuario,
                       String uidPublicacion, String uidUsuarioPublicacion) {
        this.contenido = contenido;
        this.uidUsuarioDelComentario = uidUsuario;
        this.uidPublicacion = uidPublicacion;
        this.uidUsuarioPublicacion = uidUsuarioPublicacion;
        this.nombreUsuario = nombreUsuario;
        this.timestamp = System.currentTimeMillis();
    }

    public Comentario() {}



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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
