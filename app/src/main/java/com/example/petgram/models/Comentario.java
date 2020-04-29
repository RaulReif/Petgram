package com.example.petgram.models;

public class Comentario {

    private String contenido, nombreUsuario, uidUsuarioDelComentario,
            fotoPerfil, uidPublicacion, uidUsuarioPublicacion;

    private long timestamp;

    public Comentario( String contenido, String nombreUsuario, String uidUsuario, String fotoPerfil,
                       String uidPublicacion, String uidUsuarioPublicacion) {
        this.contenido = contenido;
        this.nombreUsuario = nombreUsuario;
        this.uidUsuarioDelComentario = uidUsuario;
        this.fotoPerfil = fotoPerfil;
        this.uidPublicacion = uidPublicacion;
        this.uidUsuarioPublicacion = uidUsuarioPublicacion;
        this.timestamp = System.currentTimeMillis();
    }

    public Comentario() {}

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

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

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
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
}
