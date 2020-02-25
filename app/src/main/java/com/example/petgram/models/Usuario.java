package com.example.petgram.models;

public class Usuario {

    private String email, estado, imagen, localidad, nombre, tipo, uid;

    public Usuario(String email, String estado, String imagen, String localidad, String nombre,
                   String tipo, String uid) {
        this.email = email;
        this.estado = estado;
        this.imagen = imagen;
        this.localidad = localidad;
        this.nombre = nombre;
        this.tipo = tipo;
        this.uid = uid;
    }

    public Usuario() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
