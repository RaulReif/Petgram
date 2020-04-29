package com.example.petgram.models;

import java.util.HashMap;

public class Usuario {

    private String email, estado, imagen, localidad, nombre, tipo, uid, conexion;
    private HashMap<String, Conversacion> conversaciones;
    private HashMap<String, Publicacion> publicaciones;
    private HashMap<String, String> amigos, solicitudesEnviadas;

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

    public String getConexion() {
        return conexion;
    }

    public void setConexion(String conexion) {
        this.conexion = conexion;
    }

    public HashMap<String, Conversacion> getConversaciones() {
        return conversaciones;
    }

    public void setConversaciones(HashMap<String, Conversacion> conversaciones) {
        this.conversaciones = conversaciones;
    }

    public HashMap<String, Publicacion> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(HashMap<String, Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public HashMap<String, String> getAmigos() {
        return amigos;
    }

    public void setAmigos(HashMap<String, String> amigos) {
        this.amigos = amigos;
    }

    public HashMap<String, String> getSolicitudesEnviadas() {
        return solicitudesEnviadas;
    }

    public void setSolicitudesEnviadas(HashMap<String, String> solicitudesEnviadas) {
        this.solicitudesEnviadas = solicitudesEnviadas;
    }
}
