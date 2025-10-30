package com.example.losmuchachossecurity.model;

import java.security.Timestamp;

public class ControlEvento {
    private String id;
    private String tipo; // "ABRIR_BARRERA", "ENCENDER_LUZ", etc.
    private String usuarioId;
    private Timestamp fechaHora;

    public ControlEvento() {}

    public ControlEvento(String id, String tipo, String usuarioId, Timestamp fechaHora) {
        this.id = id;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.fechaHora = fechaHora;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }
}