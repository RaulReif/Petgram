package com.example.petgram.models;

public class Mensaje implements Comparable<Mensaje>{

    private String emisor, receptor, mensaje, visto;
    private long timestamp;

    public Mensaje(String emisor, String receptor, String mensaje) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.mensaje = mensaje;
        this.timestamp = System.currentTimeMillis();
        this.visto = "false";
    }

    public Mensaje() {}

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVisto() {
        return visto;
    }

    public void setVisto(String visto) {
        this.visto = visto;
    }

    @Override
    public int compareTo(Mensaje m) {
        if( this.timestamp < m.getTimestamp() )
            return -1;
        else
            return 1;
    }
}
