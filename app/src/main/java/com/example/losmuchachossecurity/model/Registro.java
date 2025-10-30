package com.example.losmuchachossecurity.model;

import java.security.Timestamp;

public class Registro {
    private String id;
    private String usuarioId;
    private String plazaId;
    private Timestamp horaEntrada;
    private Timestamp horaSalida;

    public Registro() {}

    public Registro(String id, String usuarioId, String plazaId, Timestamp horaEntrada, Timestamp horaSalida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.plazaId = plazaId;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getPlazaId() { return plazaId; }
    public void setPlazaId(String plazaId) { this.plazaId = plazaId; }
    public Timestamp getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(Timestamp horaEntrada) { this.horaEntrada = horaEntrada; }
    public Timestamp getHoraSalida() { return horaSalida; }
    public void setHoraSalida(Timestamp horaSalida) { this.horaSalida = horaSalida; }
}