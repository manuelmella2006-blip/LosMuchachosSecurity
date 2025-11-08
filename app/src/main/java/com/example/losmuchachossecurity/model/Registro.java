package com.example.losmuchachossecurity.model;

public class Registro {

    private String id;
    private String userId;
    private String tipo; // "entrada", "salida", "reserva", "liberacion"
    private String plazaId;
    private String plazaNumero;
    private Long timestamp;
    private String nombreUsuario;
    private String detalles;

    // Constructor vacío requerido para Firebase
    public Registro() {
    }

    // Constructor básico
    public Registro(String id, String userId, String tipo, Long timestamp) {
        this.id = id;
        this.userId = userId;
        this.tipo = tipo;
        this.timestamp = timestamp;
    }

    // Constructor completo
    public Registro(String id, String userId, String tipo, String plazaId,
                    String plazaNumero, Long timestamp, String nombreUsuario, String detalles) {
        this.id = id;
        this.userId = userId;
        this.tipo = tipo;
        this.plazaId = plazaId;
        this.plazaNumero = plazaNumero;
        this.timestamp = timestamp;
        this.nombreUsuario = nombreUsuario;
        this.detalles = detalles;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(String plazaId) {
        this.plazaId = plazaId;
    }

    public String getPlazaNumero() {
        return plazaNumero;
    }

    public void setPlazaNumero(String plazaNumero) {
        this.plazaNumero = plazaNumero;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    // Métodos auxiliares
    public String getDescripcionCompleta() {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(tipo.toUpperCase()).append(" - ");

        if (plazaNumero != null) {
            descripcion.append("Plaza ").append(plazaNumero);
        }

        if (nombreUsuario != null) {
            descripcion.append(" (").append(nombreUsuario).append(")");
        }

        return descripcion.toString();
    }

    @Override
    public String toString() {
        return "Registro{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", tipo='" + tipo + '\'' +
                ", plazaId='" + plazaId + '\'' +
                ", plazaNumero='" + plazaNumero + '\'' +
                ", timestamp=" + timestamp +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", detalles='" + detalles + '\'' +
                '}';
    }
}