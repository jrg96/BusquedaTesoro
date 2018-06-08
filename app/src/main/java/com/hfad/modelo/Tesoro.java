package com.hfad.modelo;

public class Tesoro {
    public String id_usuario;
    public double latitud;
    public double longitud;
    public String tesoro_texto;
    public String url_imagen;

    public Tesoro(){
    }

    public Tesoro(String id_usuario, double latitud, double longitud, String tesoro_texto, String url_imagen) {
        this.id_usuario = id_usuario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tesoro_texto = tesoro_texto;
        this.url_imagen = url_imagen;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getTesoro_texto() {
        return tesoro_texto;
    }

    public void setTesoro_texto(String tesoro_texto) {
        this.tesoro_texto = tesoro_texto;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }
}
