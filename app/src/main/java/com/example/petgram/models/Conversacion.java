package com.example.petgram.models;
import java.util.ArrayList;
import java.util.HashMap;

public class Conversacion implements Comparable<Conversacion> {

    private HashMap<String, Mensaje> mensajes;
    private boolean bloqueado;
    private int mensajesNoLeidos;
    private String uid;
    private long ultimoMensaje;

    public Conversacion(String uid) {
        this.mensajesNoLeidos = 0;
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

    public void cambiarMensajes(ArrayList<Mensaje> mensajes) {
        HashMap<String, Mensaje> mensajesAux = new HashMap<>();
        for (int i = 0; i < mensajes.size(); i++) {
            mensajesAux.put("m" + i, mensajes.get(i));
        }
        this.mensajes = mensajesAux;
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

    public long getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(long ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    @Override
    public int compareTo(Conversacion c) {
        if( this.ultimoMensaje > c.getUltimoMensaje() )
            return -1;
         else
            return 1;
    }


}
