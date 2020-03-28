package com.example.petgram.models;
import java.util.HashMap;

public class Conversacion {

    private HashMap<String, Mensaje> mensajes;
    private boolean bloqueado;
    private int mensajesNoLeidos;
    private String uid;

    public Conversacion(String uid) {
        this.mensajesNoLeidos = 1;
        this.bloqueado = false;
        this.uid = uid;
    }

    public Conversacion(){}

    public HashMap<String, Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(HashMap<String, Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getMensajesNoLeidos() {
        return mensajesNoLeidos;
    }

    public void setMensajesNoLeidos(int mensajesNoLeidos) {
        this.mensajesNoLeidos = mensajesNoLeidos;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
