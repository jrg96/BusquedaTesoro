package com.hfad.modelo;

import com.google.firebase.Timestamp;

public class Comentario {
    public String id_usuario_comentario;
    public String email_usuario_comentario;
    public Timestamp timestamp;
    public String texto_comentario;

    public Comentario() {
    }

    public Comentario(String id_usuario_comentario, String email_usuario_comentario, Timestamp timestamp, String texto_comentario) {
        this.id_usuario_comentario = id_usuario_comentario;
        this.email_usuario_comentario = email_usuario_comentario;
        this.timestamp = timestamp;
        this.texto_comentario = texto_comentario;
    }

    public String getId_usuario_comentario() {
        return id_usuario_comentario;
    }

    public void setId_usuario_comentario(String id_usuario_comentario) {
        this.id_usuario_comentario = id_usuario_comentario;
    }

    public String getEmail_usuario_comentario() {
        return email_usuario_comentario;
    }

    public void setEmail_usuario_comentario(String email_usuario_comentario) {
        this.email_usuario_comentario = email_usuario_comentario;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTexto_comentario() {
        return texto_comentario;
    }

    public void setTexto_comentario(String texto_comentario) {
        this.texto_comentario = texto_comentario;
    }
}
