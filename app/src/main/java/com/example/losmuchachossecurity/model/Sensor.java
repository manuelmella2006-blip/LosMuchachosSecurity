package com.example.losmuchachossecurity.model;

public class Sensor {
    private String id;
    private String tipo;       // "ultrasonido", "barrera", "luz", etc.
    private boolean activo;
    private String plazaId;    // Relación con la plaza correspondiente

    public Sensor() {}

    public Sensor(String id, String tipo, boolean activo, String plazaId) {
        this.id = id;
        this.tipo = tipo;
        this.activo = activo;
        this.plazaId = plazaId;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getPlazaId() { return plazaId; }
    public void setPlazaId(String plazaId) { this.plazaId = plazaId; }
}